package simulation;

import Dhimulate.Dhimulate;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import model.*;

/**
 * @author Jendrik, nilsw
 */
public class Simulation extends AnimationTimer {

    private final boolean laptop = false;//true;
    public double distanceStudentInfluence             = 0.1;//1
    public double distanceLocationInfluence            = 0.01;//0.1
    public double timelineInfluence                    = 0.4;
    public double directionInfluenceByStudents         = 0.0002; //0.0002
    public double directionInfluenceByLocations        = 0.0002;//0.002
    public double adjustattributesInfluenceByStudents  = 0.001;//0.000001
    public double adjustattributesInfluenceByLocations = 0.00000000001;//0.00000001
    public double healthDecreaseOnDanger               = 0.002;
    public double oneSemesterIsXDays                   = 3;
    private List<Student>  students;
    private List<Location> locations;
    private double attributesInfluence            = 1; //0.2
    private double directionInfluence             = 0.1;
    private double studentInfluence               = 1;
    private double locationInfluence              = 1;
    private double studentsPrioMAX                = 0.0;
    private double locationsPrioMAX               = 0.0;
    private double studentsVMAX                   = 0.8;//0.5
    private double attributesInfluenceByStudents  = 0.000000001;//0.1
    private double attributesInfluenceByLocations = 0.0000001;//0.003
    private double minGapBetweenStudents          = 1;
    private int    day                            = 0;
    private int[]  time                           = new int[3];
    private double semesterProgress               = 0;
    private long   lastNano                       = 0;
    private double simSpeed                       = 2;
    private double lockDistanceStudentLocation    = 50;
    private Dhimulate m_dhimulate;

    private boolean running                           = false;
    private double  stayFactor                        = 0.04;
    private double  leadershipInfluence               = 1;
    private double  attributesInfluenceInsideLocation = 0.001;

    public Simulation(Dhimulate dh, List<Student> students, List<Location> locations) {
        this.students = students;
        this.locations = locations;
        time[0] = 0;
        time[1] = 0;
        time[2] = 0;
        day = 0;
        m_dhimulate = dh;
    }

    private int[] getTime() {
        return time;
    }

    @Override
    public void handle(long nownano) {
        long elapsed = nownano - lastNano;
        lastNano = nownano;
        addTime(elapsed);
        simAllStudents((long) (elapsed * simSpeed));
        simEvents();
    }

    private void addTime(long nano) {
        int lastmin = time[1];
        double seconds = ((double) nano) / (1000000000.0 / 2000);
        time[2] += seconds;
        if (time[2] > 60) {
            time[1]++;
            time[2] = time[2] - 60;
            if (time[1] > 60) {
                time[0]++;
                handleSemesterProgress();
                time[1] = time[1] - 60;
                if (time[0] == 24) {
                    day++;
                    time[0] = 0;
                }
            }
        }
        if ((time[1]) != lastmin) {
            m_dhimulate.updateTime(day, time);
        }
    }

    public void simAllStudents(long elapsed) {
        locationsPrioMAX = 0;
        studentsPrioMAX = 0;
        for (Student element : students) {
            if (!element.isDisabled()) {
                if (element.isAlive()) {
                    simStudent(element, elapsed);
                }
                else {
                    simDeath(element);
                }
            }
        }
    }

    private void simEvents() {
        Status s;
        for (Location l : locations) {
            s = l.getTimeline().getStatus(time[0]);
            if (l.isShowingnotification()) {
                l.animNotification();
            }
            else if ((s == Status.BEFORE_EVENT) && !l.isShowingnotification()) {
                l.showNotification(l.getTimeline().getCurrentEvent(time[0]).name);
            }
        }
    }

    private void handleSemesterProgress() { //called every minute
        semesterProgress = ((day + time[0] / 24) % oneSemesterIsXDays) / oneSemesterIsXDays;
        m_dhimulate.setsemesterprogress(semesterProgress);
    }


    private void setlaptopconfig(){
        attributesInfluence            = 1; //0.2    #1
        distanceStudentInfluence       = 1;//1        #1
        distanceLocationInfluence      = 0.06;//0.1   #0.06
        directionInfluence             = 0.1;        //#0.1
        studentInfluence               = 1;
        locationInfluence              = 1;
        timelineInfluence              = 1;           //#1
        studentsPrioMAX                = 0.0;
        locationsPrioMAX               = 0.0;
        studentsVMAX                   = 0.6;//0.5   //#0.6
        directionInfluenceByStudents   = 0.0002; //0.0002  #0.0002
        directionInfluenceByLocations  = 0.001;//0.002       #0.001
        attributesInfluenceByStudents  = 0.000000001;//0.1 #0.000000001
        attributesInfluenceByLocations = 0.00001;//0.003  #0.00001
        adjustattributesInfluenceByStudents  = 0.000001;//0.000001  #0.000001
        adjustattributesInfluenceByLocations = 0.00000000001;//0.00000001  #0.00000000001
        minGapBetweenStudents          = 1;
        healthdecreaseondanger = 0.05; //#0.05
        onesemesterisxdays=3;
        stay_factor = 0.02;
        leadershipinfluence =1;
        attributesinfluenceinsidelocation = 0.001;
    }

    private void simStudent(Student student,long elapsed) {
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


        //check the status flag
        if (student.isMoving()) {
            //adjust attributes
            this.adjustAttributes(student);

            if (student.getHealth() <= 0) {
                m_dhimulate.killStudent(student);
            }

            //adjust direction according to students and locations
            this.adjustDirection(student, elapsed);

            //move the Student
            student.move(elapsed);

            if (student.getInlocationcnt() > 0) {
                student.setInlocationcnt(student.getInlocationcnt() - 1);
            }
        }
        else {

            this.adjustAttributesInsideLocation(student);

            student.setInlocationcnt(student.getInlocationcnt() + 4);

            if (student.getInsidelocation().getPriority() > locationsPrioMAX * 0.8) {
                leaveLocation(student, student.getInsidelocation());
            }
        }
    }

    private void simDeath(Student element) {
        element.simDeath();
        if (element.getDeathAnimCnt() > Student.deathanimMax) {
            element.vanish();
        }
    }

    private void prioritizeStudents(Student referenceStudent) {
        //iterate through all students and compare attributes
        double attributesDifference;
        double distance;
        double direction;
        double priority = 0.0;
        studentsPrioMAX = 0.0;
        double[] refAttr = referenceStudent.getAttributes();
        for (Student student : students) {
            if (student.isAlive()) {
                //exit if its the student we are comparing to
                if (student.getId() == referenceStudent.getId() && student.isMoving()) {
                    student.setPriority(-1);
                    continue;
                }

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
                    distance = distance + (minGapBetweenStudents - distance) * 4;
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
            if (student.isAlive()) {
                if (student.getPriority() > 0) {
                    csAttr = compareToOtherElement(csAttr, student, studentsPrioMAX, adjustattributesInfluenceByStudents);
                }
            }
        }

        for (Location location : locations) {
            csAttr = compareToOtherElement(csAttr, location, locationsPrioMAX, adjustattributesInfluenceByLocations);
        }
        currentStudent.setAttributes(csAttr);
    }

    //adjusting direction
    private void adjustDirection(Student referenceStudent, long elapsed) {
        //iterate through all students and locations
        double factor;
        Vector2D refPos = referenceStudent.getPosition();
        Vector2D pos2;
        Vector2D delta;
        Vector2D studentsDir = new Vector2D(0, 0);
        Vector2D locationsDir = new Vector2D(0, 0);

        //students
        for (Student student : students) {
            if (student.getID() != referenceStudent.getID() && student.getPriority() > 0 && student.isAlive()) {
                pos2 = student.getPosition();
                delta = refPos.subtract(pos2);

                factor = 1 / delta.length();
                delta.scalarMultiplication2(factor);

                studentsDir.mX += delta.mX * (1 - (student.getPriority() / studentsPrioMAX));
                studentsDir.mY += delta.mY * (1 - (student.getPriority() / studentsPrioMAX));
            }
        }

        //locations
        for (Location location : locations) {
            pos2 = location.getCenterPosition();
            delta = refPos.subtract(pos2);

            factor = 1 / delta.length();
            delta.scalarMultiplication2(factor);

            locationsDir.mX += delta.mX * (1 - (location.getPriority() / locationsPrioMAX));
            locationsDir.mY += delta.mY * (1 - (location.getPriority() / locationsPrioMAX));
        }

        locationsDir.mX *= (students.size() / locations.size());
        locationsDir.mY *= (students.size() / locations.size());


        Vector2D refDir = referenceStudent.getDirection();
        double dX = refDir.mX - studentsDir.mX * directionInfluenceByStudents - locationsDir.mX * directionInfluenceByLocations;
        double dY = refDir.mY - studentsDir.mY * directionInfluenceByStudents - locationsDir.mY * directionInfluenceByLocations;


        Vector2D newDir = new Vector2D(dX, dY);
        factor = studentsVMAX / newDir.length();

        newDir.scalarMultiplication2(factor);
        referenceStudent.setDirection(newDir);
    }

    private void adjustAttributesInsideLocation(Student student) {
        double v1, v2, f;
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            f = 1;
            v1 = student.getAttribute(i);
            v2 = student.getInsidelocation().getAttribute(i);
            if (student.getInsidelocation().getName().equals("Disco")) {
                f = 3;
            }
            student.setAttribute(i, v1 + (v2 - v1) * attributesInfluenceInsideLocation * f);
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

        if (distance < lockDistanceStudentLocation) {
            //lock on to location
            if (referenceStudent.isMoving()) {
                if (referenceStudent.getInlocationcnt() > 0) {
                    if (referenceStudent.getInsidelocation().getID() != location.getID()) {
                        enterLocation(referenceStudent, location);
                    }
                }
                else {
                    enterLocation(referenceStudent, location);
                }
            }
        }
        double stay = 0;
        if (referenceStudent.getInsidelocation() != null) {
            if (location.getID() == referenceStudent.getInsidelocation().getID()) {
                stay = referenceStudent.getInlocationcnt();
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
            s.setInsidelocation(l);
            s.setInlocationcnt(0);
        }
    }

    private double getTime(int i) {
        return time[i];
    }

    @Override
    public void start() {
        super.start();
        lastNano = System.nanoTime();
        running = true;
        if(laptop==true){
            setlaptopconfig();
        }
    }

    @Override
    public void stop() {
        super.stop();
        running = false;


    }

    public boolean isRunning() {
        return running;
    }

}
