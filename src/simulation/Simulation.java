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

    private double attributesInfluence            = 1; //
    private double distanceStudentInfluence       = 0.1;//0.1
    private double distanceLocationInfluence      = 0.01;//0.003
    private double directionInfluence             = 1;
    private double studentInfluence               = 1;
    private double locationInfluence              = 1;
    private double timelineInfluence              = 0.01;
    private double studentsPrioMAX                = 0.0;
    private double locationsPrioMAX               = 0.0;
    private double studentsVMAX                   = 0.5;//0.5
    private double directionInfluenceByStudents   = 0.0005; //0.0002
    private double directionInfluenceByLocations  = 0.005;//0.001
    private double attributesInfluenceByStudents  = 0.01;//0.01
    private double attributesInfluenceByLocations = 0.003;//0.02
    private double adjustattributesInfluenceByStudents  = 0.000001;//0.0000001
    private double adjustattributesInfluenceByLocations = 0.00000001;//0.00000001
    private double minGapBetweenStudents          = 1;

    Dhimulate m_dhimulate;
    private long lastnano = 0;
    private double simspeed = 2;
    private double lockDistanceStudentLocation = 50;

    public Simulation(Dhimulate dhimulate){
        m_dhimulate = dhimulate;
    }


    public void simAllStudents(List<Student> students, List<Location> locations,long elapsed) {
        locationsPrioMAX=0;
        studentsPrioMAX= 0;
        this.students = students;
        this.locations = locations;
        for (Student element : students) {
            simStudent(element,elapsed);
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



        //check the status flag
        if (student.isMoving()) {
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
        s.getCircle().setFill(Color.rgb( (int)(255*(s.getDanger()/SimElement.dangerMAX)),0,255-(int)(255*(s.getDanger()/SimElement.dangerMAX))));
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

            //exit if its the student we are comparing to
            if (student.getId() == referenceStudent.getId() && student.isMoving()==true) {
                student.setPriority(-1);
                continue;
            }

            //compare attributes
            attributesDifference = 0.0;

            double[] sAttr = student.getAttributes();
            double pot;
            for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
                pot = Math.abs(sAttr[i] - refAttr[i]);
                attributesDifference += pot*pot*pot;
            }
            attributesDifference =attributesDifference+(Math.abs(referenceStudent.getDanger() - student.getDanger())*Math.abs(referenceStudent.getDanger() - student.getDanger()));

            //compare position
            Vector2D refPos = referenceStudent.getPosition();
            Vector2D sPos = student.getPosition();
            distance = refPos.getDistanceTo(sPos);
            if (distance < minGapBetweenStudents) {
                distance = distance+(minGapBetweenStudents-distance)*4;
            }

            //compare Direction
            Vector2D refDir = referenceStudent.getDirection();
            Vector2D sDir = student.getDirection();
            direction = refDir.getDistanceTo(sDir);

            //combine those
            student.setPriority(attributesDifference * attributesInfluence *attributesInfluenceByStudents + distance * distanceStudentInfluence + direction * directionInfluence);

            if (student.getPriority() > studentsPrioMAX) {
                studentsPrioMAX = student.getPriority();
            }
        }
    }

    //adjusting attributes
    private void adjustAttributes(Student currentStudent) {
        double[] csAttr = currentStudent.getAttributes();
        for (Student student : students) {
            if(student.getPriority()>0){csAttr = compareToOtherElement(csAttr, student, studentsPrioMAX, adjustattributesInfluenceByStudents);}
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

    private double getTime() {
        return 0;
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
            if(student.getID()!=referenceStudent.getID() && student.getPriority()>0){
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
        double time = getTime();
        Status timelinePrio = location.getTimeline().getStatus(time);

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

        double prio = distanceLocationInfluence * distance + /*timelinePrio.toInt() * timelineInfluence*/ + attributesDifference*attributesInfluenceByLocations * attributesInfluence;
        location.setPriority(prio);
        if (location.getPriority() > locationsPrioMAX) {
            locationsPrioMAX = location.getPriority();
        }
    }

    @Override
    public void handle(long nownano) {
        long elapsed = nownano-lastnano;
        lastnano=nownano;
        simAllStudents(m_dhimulate.getStudents(), m_dhimulate.getLocations(),(long)(elapsed*simspeed));
    }

    @Override
    public void start(){
        super.start();
        lastnano=System.nanoTime();


    }

    @Override
    public void stop(){
        super.stop();



    }

}
