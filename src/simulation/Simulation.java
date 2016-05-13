package simulation;

import java.util.List;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import model.*;

/**
 * @author Jendrik, nilsw
 */
public class Simulation {

    public        double distanceStudentInfluence             = 1;
    public        double distanceLocationInfluence            = 0.05;
    public        double timelineInfluence                    = 1;
    public        double directionInfluenceByStudents         = 0.0002;
    public        double directionInfluenceByLocations        = 0.0007;
    public        double adjustAttributesInfluenceByStudents  = 0.000001;
    public        double adjustAttributesInfluenceByLocations = 0.00000000001;
    public        double healthDecreaseOnDanger               = 0.004;
    public        int    daysPerSemester                      = 3;
    private final double attributesInfluence                  = 1.0;
    private final double directionInfluence                   = 0.1;
    private final double studentsVMAX                         = 0.7;
    private final double attributesInfluenceByStudents        = 0.000000001;
    private final double attributesInfluenceByLocations       = 0.00001;
    private final double minGapBetweenStudents                = 1.0;
    private final double discoMultiplier                      = 6.0;
    private final double restMultiplier                       = 2.5;
    private final double lockDistanceStudentLocation          = 50.0;
    private final double stayFactor                           = 0.1;
    private final double leadershipInfluence                  = 1.0;
    private final double attributesInfluenceInsideLocation    = 0.001;
    private final double klausurDeath                         = 0.001;
    private final int    simSpeed                             = 3;
    private List<Student>  students;
    private List<Location> locations;
    private BooleanProperty minutePassed     = new SimpleBooleanProperty(false);
    private DoubleProperty  semesterProgress = new SimpleDoubleProperty(0.0);
    private IntegerProperty day              = new SimpleIntegerProperty(1);
    private boolean         klausurenTime    = false;
    private double[]        time             = {0.0, 0.0, 0.0};
    private double          studentsPrioMAX  = 0.0;
    private double          locationsPrioMAX = 0.0;
    private int             semesterCount    = 1;


    public Simulation(List<Student> students, List<Location> locations) {
        this.students = students;
        this.locations = locations;
    }

    public void handle(long elapsed) {
        addTime(elapsed);
        simAllStudents(elapsed * simSpeed);
        simEvents();
    }

    //time0 hours, time1 minutes, time2 seconds
    private void addTime(long elapsed) {
        final double secondsElapsed = ((double) elapsed) / (1000000000.0 / 2000);
        time[2] += secondsElapsed;
        if (time[2] >= 60) {
            time[1]++;
            time[2] -= 60;
            if (time[1] >= 60) {
                time[0]++;
                time[1] -= 60;
                if (time[0] == 24) {
                    incDay();
                    time[0] = 0;
                }
                handleSemesterProgress();
            }
            setMinutePassed(true);
        }
        if (getMinutePassed()) {
            setMinutePassed(false);
        }
    }

    private void simAllStudents(long elapsed) {
        locationsPrioMAX = 0;
        studentsPrioMAX = 0;
        for (Student student : students) {
            if (!student.isDisabled()) {
                if (student.isAlive()) {
                    simStudent(student, elapsed);
                }
                else {
                    simDeath(student);
                }
            }
        }
    }

    private void simEvents() {
        Status s;
        for (Location l : locations) {
            s = l.getTimeline().getStatus(time[0]);
            if (l.isShowingNotification()) {
                l.animNotification();
            }
            else if ((s == Status.BEFORE_EVENT) && !l.isShowingNotification()) {
                l.showNotification(l.getTimeline().getCurrentEvent(time[0]).name);
            }
        }
    }

    private void handleSemesterProgress() { //called every hour
        final double semesterProgress = (((getDay() - 1) + time[0] / 24) % daysPerSemester) / daysPerSemester;
        setSemesterProgress(semesterProgress);
        if (semesterProgress >= 0.9 && !klausurenTime) {
            klausurenTime = true;
        }
        if (getDay() > daysPerSemester) {
            klausurenTime = false;
            ++semesterCount;
        }
    }

    private void simStudent(Student student, long elapsed) {
        //analyse students
        this.prioritizeStudents(student);

        //analyse locations
        this.prioritizeLocations(student);

        student.calcDanger();
        this.setDangerColor(student);

        if (student.getDanger() > (SimElement.dangerMAX / 1.7)) {
            student.setHealth(student.getHealth() - (student.getDanger() / (SimElement.dangerMAX / 1.7)) * healthDecreaseOnDanger);
        }

        if (klausurenTime) {
            student.setHealth(student.getHealth() - ((110 - student.getAttribute(Student.LEARNING)) * klausurDeath));
        }

        //check the status flag
        if (student.isMoving()) {
            //adjust attributes
            this.adjustAttributes(student);

            if (student.getHealth() <= 0) {
                student.setFailed(true);
            }

            //adjust direction according to students and locations
            adjustDirection(student);

            //move the Student
            student.move(elapsed);

            if (student.getInLocationCnt() > 0) {
                student.setInLocationCnt(student.getInLocationCnt() - 1);
            }
        }
        else {
            this.adjustAttributesInsideLocation(student);

            student.setInLocationCnt(student.getInLocationCnt() + 4);

            if (student.getInsideLocation().getPriority() > locationsPrioMAX * 0.8) {
                leaveLocation(student, student.getInsideLocation());
            }
        }
    }

    private void simDeath(Student student) {
        student.simDeath();
        if (student.getDeathAnimCnt() > Student.deathAnimMax) {
            student.vanish();
        }
    }

    private void prioritizeStudents(Student referenceStudent) {
        //iterate through all students and compare attributes
        double attributesDifference;
        double distance;
        double direction;
        studentsPrioMAX = 0.0;
        double[] refAttr = referenceStudent.getAttributes();
        for (Student student : students) {
            if (student.isAlive() && (student.getId() != referenceStudent.getId() || !student.isMoving())) {
                //compare attributes
                attributesDifference = 0.0;

                double[] sAttr = student.getAttributes();
                double pot;
                for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
                    pot = Math.abs(sAttr[i] - refAttr[i]);
                    attributesDifference += pot * pot * pot;
                }
                attributesDifference = attributesDifference + (Math.abs(referenceStudent.getDanger() - student.getDanger()) * Math.abs(referenceStudent.getDanger() - student.getDanger()));

                //compare position
                Vector2D refPos = referenceStudent.getPosition();
                Vector2D sPos = student.getPosition();
                distance = refPos.getDistanceTo(sPos);
                student.setDist(distance);

                if (distance < minGapBetweenStudents) {
                    distance += (minGapBetweenStudents - distance) * 4;
                }

                //compare Direction
                Vector2D refDir = referenceStudent.getDirection();
                Vector2D sDir = student.getDirection();
                direction = refDir.getDistanceTo(sDir);

                //combine those
                student.setPriority((100 - student.getAttribute(Student.LEADERSHIP)) * leadershipInfluence + attributesDifference * attributesInfluence * attributesInfluenceByStudents + distance * distanceStudentInfluence + direction * directionInfluence);

                if (student.getPriority() > studentsPrioMAX) {
                    studentsPrioMAX = student.getPriority();
                }
            }
            else {
                student.setPriority(-1);
            }
        }
    }

    private void prioritizeLocations(Student referenceStudent) {
        locationsPrioMAX = 0;
        for (Location location : locations) {
            prioritizeLocation(referenceStudent, location);
        }
    }

    private void setDangerColor(Student s) {
        s.getCircle().setFill(Color.rgb((int) (55 + 200 * (s.getDanger() / SimElement.dangerMAX)), 0, 255 - (int) (55 + 200 * (s.getDanger() / SimElement.dangerMAX))));
    }

    //adjusting attributes
    private void adjustAttributes(Student currentStudent) {
        double[] csAttr = currentStudent.getAttributes();
        for (Student student : students) {
            if (student.isAlive() && student.getPriority() > 0) {
                csAttr = compareToOtherElement(csAttr, student, studentsPrioMAX, adjustAttributesInfluenceByStudents);
            }
        }

        for (Location location : locations) {
            csAttr = compareToOtherElement(csAttr, location, locationsPrioMAX, adjustAttributesInfluenceByLocations);
        }
        currentStudent.setAttributes(csAttr);
    }

    //adjusting direction
    private void adjustDirection(Student referenceStudent) {
        Vector2D refPos = referenceStudent.getPosition();
        Vector2D pos2;
        Vector2D delta;
        Vector2D studentsDir = new Vector2D(0, 0);
        Vector2D locationsDir = new Vector2D(0, 0);

        for (Student student : students) {
            if (student.getID() != referenceStudent.getID() && student.getPriority() > 0 && student.isAlive()) {
                pos2 = student.getPosition();
                delta = refPos.subtract(pos2);
                delta.normalize2();

                delta.scalarMultiplication2(1 - (student.getPriority() / studentsPrioMAX));
                studentsDir.add2(delta);
            }
        }

        for (Location location : locations) {
            pos2 = location.getCenterPosition();
            delta = refPos.subtract(pos2);
            delta.normalize2();

            delta.scalarMultiplication2(1 - (location.getPriority() / locationsPrioMAX));
            locationsDir.add2(delta);
        }

        locationsDir.scalarMultiplication2(students.size() / locations.size());

        Vector2D refDir = referenceStudent.getDirection();
        studentsDir.scalarMultiplication2(directionInfluenceByStudents);
        locationsDir.scalarMultiplication2(directionInfluenceByLocations);
        refDir.subtract2(studentsDir);
        refDir.subtract2(locationsDir);

        refDir.normalize2();
        refDir.scalarMultiplication2(studentsVMAX);
        referenceStudent.setDirection(refDir);
    }

    private void adjustAttributesInsideLocation(Student student) {
        double studentAttr, locationAttr, locationMultiplier;
        Location loc;
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            studentAttr = student.getAttribute(i);
            loc = student.getInsideLocation();
            locationAttr = loc.getAttribute(i);
            locationMultiplier = loc.getName().equals("Disco") ? discoMultiplier : restMultiplier;
            student.setAttributes(i, studentAttr + (locationAttr - studentAttr) * attributesInfluenceInsideLocation * locationMultiplier);
        }
    }

    private void leaveLocation(Student s, Location l) {
        if (l.shrink()) {
            s.getCircle().setVisible(true);
            s.setMoving(true);

        }
    }

    private void prioritizeLocation(Student referenceStudent, Location location) {
        //compare attributes
        double attributesDifference = 0.0;
        double[] lAttr = location.getAttributes();
        double[] refAttr = referenceStudent.getAttributes();
        double pot;
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            pot = Math.abs(lAttr[i] - refAttr[i]);
            attributesDifference += pot;
        }
        attributesDifference = attributesDifference + (Math.abs(referenceStudent.getDanger() - location.getDanger()) * Math.abs(referenceStudent.getDanger() - location.getDanger()));

        //compare position
        Vector2D refPos = referenceStudent.getPosition();
        Vector2D lPos = location.getCenterPosition();
        double distance = refPos.getDistanceTo(lPos);
        location.setDist(distance);

        if (distance < lockDistanceStudentLocation && referenceStudent.isMoving()) {
            //lock on to location
            if (referenceStudent.getInLocationCnt() > 0) {
                if (referenceStudent.getInsideLocation().getID() != location.getID()) {
                    enterLocation(referenceStudent, location);
                }
            }
            else {
                enterLocation(referenceStudent, location);
            }
        }
        double stay = 0;
        if (referenceStudent.getInsideLocation() != null) {
            if (location.getID() == referenceStudent.getInsideLocation().getID()) {
                stay = referenceStudent.getInLocationCnt();
            }
        }
        double prio = stay * stayFactor + distanceLocationInfluence * distance + Status.toInt(location.getTimeline().getStatus(getTime(0))) * timelineInfluence + attributesDifference * attributesInfluenceByLocations * attributesInfluence;
        location.setPriority(prio);
        if (location.getPriority() > locationsPrioMAX) {
            locationsPrioMAX = location.getPriority();
        }
    }

    private double[] compareToOtherElement(double[] csAttr, SimElement element, double priomax, double influence) {
        double[] elemAttr = element.getAttributes();
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            csAttr[i] += ((elemAttr[i] - csAttr[i]) * (1 - (element.getPriority() / priomax)) * influence);
        }
        return csAttr;
    }

    private void enterLocation(Student s, Location l) {
        if (l.grow()) {
            s.getCircle().setVisible(false);
            s.setMoving(false);
            s.setInsideLocation(l);
            s.setInLocationCnt(0);
        }
    }

    public double getSemesterProgress() {
        return semesterProgress.get();
    }

    public DoubleProperty semesterProgressProperty() {
        return semesterProgress;
    }

    public void setSemesterProgress(final double semesterProgress) {
        this.semesterProgress.set(semesterProgress);
    }

    public boolean getMinutePassed() {
        return minutePassed.get();
    }

    public BooleanProperty minutePassedProperty() {
        return minutePassed;
    }

    public void setMinutePassed(final boolean minutePassed) {
        this.minutePassed.set(minutePassed);
    }

    public int getDay() {
        return day.get();
    }

    public IntegerProperty dayProperty() {
        return day;
    }

    public void setDay(final int day) {
        this.day.set(day);
    }

    public void incDay() {
        this.day.set(day.get() + 1);
    }

    public double[] getTime() {
        return time;
    }

    private double getTime(int i) {
        return time[i];
    }

    public int getSemesterCount() {
        return semesterCount;
    }
}
