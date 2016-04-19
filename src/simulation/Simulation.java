package simulation;

import java.util.LinkedList;
import java.util.List;
import model.*;

/**
 * @author Jendrik, nilsw
 */
public class Simulation {

    List<Student>  students;
    List<Location> locations;

    private double attributesInfluence            = 1;
    private double distanceStudentInfluence       = 1;
    private double distanceLocationInfluence      = 1;
    private double directionInfluence             = 1;
    private double studentInfluence               = 1;
    private double locationInfluence              = 1;
    private double timelineInfluence              = 1;
    private double studentsPrioMAX                = 0.0;
    private double locationsPrioMAX               = 0.0;
    private double studentsVMAX                   = 0.0;
    private double directionInfluenceByStudents   = 0.001;
    private double directionInfluenceByLocations  = 0.005;
    private double attributesInfluenceByStudents  = 0.00001;
    private double attributesInfluenceByLocations = 0.00005;
    private double minGapBetweenStudents          = 1;


    public void simAllStudents(List<Student> students, List<Location> locations) {
        this.students = students;
        this.locations = locations;
        for (Student element : students) {
            simStudent(element);
        }
    }

    private void simStudent(Student student) {
        //analyse students
        this.prioritizeStudents(student);

        //analyse locations
        this.prioritizeLocations(student);

        //adjust attributes
        this.adjustAttributes(student);

        //check the status flag
        if (student.isMoving()) {

            //adjust direction according to students and locations
            this.adjustDirection(student);

            //move the Student
            Vector2D pos = student.getPosition();
            Vector2D dir = student.getDirection();
            double xPos = pos.mX + dir.mX;
            double yPos = pos.mY + dir.mY;
            Vector2D newPos = new Vector2D(xPos, yPos);
            student.setPosition(newPos);

            //inside location
        }
        else {

            //handle the attributes

            //interact
        }
    }

    private void prioritizeStudents(Student referenceStudent) {
        List<Student> prioritizedList = new LinkedList<>();
        //iterate through all students and compare attributes
        double attributesDifference;
        double distance;
        double direction;
        double priority = 0.0;
        studentsPrioMAX = 0.0;
        double[] refAttr = referenceStudent.getAttributes();
        for (Student student : students) {

            //exit if its the student we are comparing to
            if (student.getId() == referenceStudent.getId()) {
                continue;
            }

            //compare attributes
            attributesDifference = 0.0;
            double[] sAttr = student.getAttributes();

            for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
                attributesDifference += Math.abs(sAttr[i] - refAttr[i]);
            }

            //compare position
            Vector2D refPos = referenceStudent.getPosition();
            Vector2D sPos = student.getPosition();
            distance = refPos.getDistanceTo(sPos);
            if (distance < minGapBetweenStudents) {
                distance *= 3;
            }

            //compare Direction
            Vector2D refDir = referenceStudent.getDirection();
            Vector2D sDir = student.getDirection();
            direction = refDir.getDistanceTo(sDir);

            //combine those
            student.setPriority(attributesDifference * attributesInfluence + distance * distanceStudentInfluence + direction * directionInfluence);

            if (student.getPriority() > studentsPrioMAX) {
                studentsPrioMAX = student.getPriority();
            }
        }
    }

    private void prioritizeLocations(Student referenceStudent) {

    }

    //adjusting attributes
    private void adjustAttributes(Student currentStudent) {
        double[] csAttr = currentStudent.getAttributes();
        for (Student student : students) {
            csAttr = compareToOtherElement(csAttr, student);
        }

        for (Location location : locations) {
            csAttr = compareToOtherElement(csAttr, location);
        }
        currentStudent.setAttributes(csAttr);
    }

    private double[] compareToOtherElement(double[] csAttr, SimElement element) {
        double[] locAttr = element.getAttributes();
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            csAttr[i] += ((locAttr[i] - csAttr[i]) * (1 - (element.getPriority() / studentsPrioMAX)) * attributesInfluenceByLocations);
        }
        return csAttr;
    }

    private double getTime() {
        return 0;
    }

    //adjusting direction
    private void adjustDirection(Student referenceStudent) {
        //iterate through all students and locations

        //students
        double studentsdVX = 0.0;
        double studentsdVY = 0.0;
        Vector2D refPos = referenceStudent.getPosition();
        for (Student student : students) {
            Vector2D sPos = student.getPosition();
            Vector2D delta = refPos.subtract(sPos);

            studentsdVX += delta.mX * (1 - (student.getPriority() / studentsPrioMAX));
            studentsdVY += delta.mY * (1 - (student.getPriority() / studentsPrioMAX));
        }

        //locations
        double locationsdVX = 0.0;
        double locationsdVY = 0.0;
        for (Location location : locations) {
            Vector2D lPos = location.getPosition();
            Vector2D delta = refPos.subtract(lPos);

            locationsdVX += delta.mX * (1 - (location.getPriority() / locationsPrioMAX));
            locationsdVY += delta.mY * (1 - (location.getPriority() / locationsPrioMAX));
        }

        Vector2D refDir = referenceStudent.getDirection();
        double dX = refDir.mX + studentsdVX * directionInfluenceByStudents + locationsdVX * directionInfluenceByLocations;
        double dY = refDir.mY + studentsdVY * directionInfluenceByStudents + locationsdVY * directionInfluenceByLocations;

        Vector2D newDir = new Vector2D(dX, dY);
        double factor = studentsVMAX / newDir.length();

        newDir.scalarMultiplication2(factor);
        referenceStudent.setDirection(newDir);
    }

    private void prioritizeAllLocations(Student referenceStudent) {
        for (Location location : locations) {
            prioritizeLocation(referenceStudent, location);
        }
    }

    private void prioritizeLocation(Student referenceStudent, Location location) {
        Vector2D sPos = referenceStudent.getPosition();
        Vector2D lPos = location.getPosition();
        double distance = sPos.getDistanceTo(lPos);
        Timeline timeline = location.getTimeline();
        double time = getTime();
        Status timelinePrio = location.getTimeline().getStatus(time);

        location.setPriority(distanceLocationInfluence * distance + Status.toInteger(timelinePrio) * timelineInfluence);
        if (location.getPriority() > locationsPrioMAX) {
            locationsPrioMAX = location.getPriority();
        }
    }
}
