package simulation;

import java.util.LinkedList;
import java.util.List;

import Dhimulate.Dhimulate;
import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.*;

/**
 * @author Jendrik, nilsw
 */
public class Simulation extends AnimationTimer {

    private List<Student>  students;
    private List<Location> locations;

    private double attributesInfluence            = 0.2; //0.2
    public double distanceStudentInfluence       = 1;//1
    public double distanceLocationInfluence      = 0.1;//0.1
    private double directionInfluence             = 0.01;
    private double studentInfluence               = 1;
    private double locationInfluence              = 1;
    public double timelineInfluence              = 1;
    private double studentsPrioMAX                = 0.0;
    private double locationsPrioMAX               = 0.0;
    private double studentsVMAX                   = 0.5;//0.5
    public double directionInfluenceByStudents   = 0.0002; //0.0002
    public double directionInfluenceByLocations  = 0.002;//0.002
    private double attributesInfluenceByStudents  = 0.1;//0.1
    private double attributesInfluenceByLocations = 0.003;//0.003
    public double adjustattributesInfluenceByStudents  = 0.00001;//0.000001
    public double adjustattributesInfluenceByLocations = 0.00000001;//0.00000001
    private double minGapBetweenStudents          = 1;
    public double healthdecreaseondanger = 0.1;
    private int day = 0;
    private double[] time = new double[3];
    private double semesterprogress = 0;
    public double onesemesterisxdays=3;

    private long lastnano = 0;
    private double simspeed = 2;
    private double lockDistanceStudentLocation = 50;
    private Dhimulate m_dhimulate;

    private boolean running = false;

    public Simulation(Dhimulate dh,List<Student> students, List<Location> locations){
        this.students = students;
        this.locations = locations;
        time[0]=0;time[1]=0;time[2]=0;
        day=0;
        m_dhimulate = dh;
    }


    public void simAllStudents(long elapsed) {
        locationsPrioMAX=0;
        studentsPrioMAX= 0;
        for (Student element : students) {
            if(element.isDisabled()!=true){
                if(element.isAlive()==true){
                    simStudent(element,elapsed);
                }else{
                    simDeath(element);
                }
            }
        }
    }



    private void simDeath(Student element) {
        element.simDeath();
        if(element.getdeathanimcnt()>Student.deathanimMax){
            element.vanish();
        }
    }

    private void simStudent(Student student,long elapsed) {
        //analyse students
        this.prioritizeStudents(student);

        //analyse locations
        this.prioritizeLocations(student);

        //adjust attributes
        this.adjustAttributes(student);


        student.calcDanger();
        this.setDangerColor(student);

        if(student.getDanger()>(SimElement.dangerMAX/2)){
            student.setHealth(student.getHealth()-healthdecreaseondanger);
        }


        //check the status flag
        if (student.isMoving()) {
            if(student.getHealth()<=0){
                m_dhimulate.killStudent(student);
            }

            //adjust direction according to students and locations
            this.adjustDirection(student,elapsed);

            //move the Student
            student.move(elapsed);

            //inside location
        }
        else {

            //handle the attributes

            //interact
        }
    }

    private void setDangerColor(Student s){
        s.getCircle().setFill(Color.rgb( (int)(55+200*(s.getDanger()/SimElement.dangerMAX)),0,255-(int)(55+200*(s.getDanger()/SimElement.dangerMAX))));
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
            if(student.isAlive()==true) {
                //exit if its the student we are comparing to
                if (student.getId() == referenceStudent.getId() && student.isMoving() == true) {
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
                if (distance < minGapBetweenStudents) {
                    distance = distance + (minGapBetweenStudents - distance) * 4;
                }

                //compare Direction
                Vector2D refDir = referenceStudent.getDirection();
                Vector2D sDir = student.getDirection();
                direction = refDir.getDistanceTo(sDir);

                //combine those
                student.setPriority(attributesDifference * attributesInfluence * attributesInfluenceByStudents + distance * distanceStudentInfluence + direction * directionInfluence);

                if (student.getPriority() > studentsPrioMAX) {
                    studentsPrioMAX = student.getPriority();
                }
            }
        }
    }

    //adjusting attributes
    private void adjustAttributes(Student currentStudent) {
        double[] csAttr = currentStudent.getAttributes();
        for (Student student : students) {
            if(student.isAlive()==true) {
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

    private double[] compareToOtherElement(double[] csAttr, SimElement element, double priomax, double influence) {
        double[] elemAttr = element.getAttributes();
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            csAttr[i] += ((elemAttr[i] - csAttr[i]) * (1 - (element.getPriority() / priomax)) * influence);
        }
        return csAttr;
    }

    private double[] getTime() {
        return time;
    }

    private double getTime(int i) {
        return time[i];
    }

    private void addTime(long nano){
        int lastmin=(int)time[1];
        double seconds = ((double)nano) / (1000000000.0/2000);
        time[2]+=seconds;
        if(time[2]>60){
            time[1]++;
            time[2]=time[2]-60;
            if(time[1]>60){
                time[0]++;
                handlesemesterprogress();
                time[1]=time[1]-60;
                if(time[0]==24){
                    day++;
                    time[0]=0;
                }
            }
        }
        if(((int)time[1])!=lastmin){
            m_dhimulate.updateTime(day,time);
        };
    }


    //adjusting direction
    private void adjustDirection(Student referenceStudent,long elapsed) {
        //iterate through all students and locations
        double factor;
        Vector2D refPos = referenceStudent.getPosition();
        Vector2D pos2;
        Vector2D delta;
        Vector2D studentsDir = new Vector2D(0,0);
        Vector2D locationsDir = new Vector2D(0,0);

        //students
        for (Student student : students) {
            if(student.getID()!=referenceStudent.getID() && student.getPriority()>0 && student.isAlive()==true){
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

        locationsDir.mX*=(students.size()/locations.size());
        locationsDir.mY*=(students.size()/locations.size());


        Vector2D refDir = referenceStudent.getDirection();
        double dX = refDir.mX - studentsDir.mX  * directionInfluenceByStudents - locationsDir.mX * directionInfluenceByLocations;
        double dY = refDir.mY - studentsDir.mY * directionInfluenceByStudents - locationsDir.mY * directionInfluenceByLocations;


        Vector2D newDir = new Vector2D(dX, dY);
        factor = studentsVMAX / newDir.length();

        newDir.scalarMultiplication2(factor);
        referenceStudent.setDirection(newDir);
    }

    private void prioritizeLocations(Student referenceStudent) {
        locationsPrioMAX=0;
        for (Location location : locations) {
            prioritizeLocation(referenceStudent, location);
        }
    }

    private void enterLocation(Student s, Location l){
        if(l.grow()==true) {
            s.getCircle().setVisible(false);
            s.setMoving(false);
        }
    }

    private void leaveLocation(Student s, Location l){
        if(l.shrink()==true) {
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
        attributesDifference =attributesDifference+(Math.abs(referenceStudent.getDanger() - location.getDanger())*Math.abs(referenceStudent.getDanger() - location.getDanger()));

        //compare position
        Vector2D refPos = referenceStudent.getPosition();
        Vector2D lPos = location.getCenterPosition();
        double distance = refPos.getDistanceTo(lPos);
        if (distance < lockDistanceStudentLocation) {
            //lock on to location
            if(referenceStudent.isMoving()==true){enterLocation(referenceStudent,location);}
        }

        double prio = distanceLocationInfluence * distance + location.getTimeline().getStatus(getTime(0)).toInt() * timelineInfluence + attributesDifference*attributesInfluenceByLocations * attributesInfluence;
        location.setPriority(prio);
        if (location.getPriority() > locationsPrioMAX) {
            locationsPrioMAX = location.getPriority();
        }
    }

    @Override
    public void handle(long nownano) {
        long elapsed = nownano-lastnano;
        lastnano=nownano;
        addTime(elapsed);
        simAllStudents((long)(elapsed*simspeed));
    }

    private void handlesemesterprogress() { //called every minute
        semesterprogress=((day+time[0]/24)%onesemesterisxdays)/onesemesterisxdays;
        m_dhimulate.setsemesterprogress(semesterprogress);
    }

    @Override
    public void start(){
        super.start();
        lastnano=System.nanoTime();
        running = true;

    }

    public boolean isRunning(){return running;}

    @Override
    public void stop(){
        super.stop();
        running = false;


    }

}
