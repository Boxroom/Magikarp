package simulation;

import java.util.List;
import model.Student;

/**
 * @author Jendrik, Nils
 */
public class Simulation {

	List<Student> students;
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
	private double directionInfluenceByStudents   = 0.001;
	private double directionInfluenceByLocations  = 0.005;
	private double attributesInfluenceByStudents  = 0.00001;
	private double attributesInfluenceByLocations = 0.00005;


	public void simAllStudents(List<Student> students, List<Location> locations) {
		this.students = students;
		this.locations = locations;
		for (Student element : students) {
			simStudent(element);
		}
	}

	private boolean simStudent(Student currentStudent) {
		//analyse students
		this.prioritizeStudents(currentStudent);

		//analyse locations
		this.prioritizeLocations(currentStudent);

		//adjust attributes
		this.adjustAttributs(currentStudent);

		//check the status flag
		switch (currentStudent.getStatus()) {

			//moving around
			case Status.Moving:

				//adjust direction according to students and locations
				this.adjustDirection(currentStudent);

				//move the Student
				currentStudent.setX(currentStudent.getX() + currentStudent.getVX());
				currentStudent.setY(currentStudent.getY() + currentStudent.getVY());

				break;

			//inside location
			case Status.Static:

				//handle the attributes

				//interact
		}
	}

	//adjusting attributes
	private void adjustAttributs(Student currentStudent) {
		//from students
		for (Student element : students) {
			currentStudent.getTeamSkill() + ((element.getTeamSkill() - currentStudent.getTeamSkill()) * (1 - (element.getPriority() / studentsPrioMAX)) * attributesInfluenceByStudents);
			currentStudent.getLearning() + ((element.getLearning() - currentStudent.getLearning()) * (1 - (element.getPriority() / studentsPrioMAX)) * attributesInfluenceByStudents);
			currentStudent.getPartying() + ((element.getPartying() - currentStudent.getPartying()) * (1 - (element.getPriority() / studentsPrioMAX)) * attributesInfluenceByStudents);
			currentStudent.getDrinking() + ((element.getDrinking() - currentStudent.getDrinking()) * (1 - (element.getPriority() / studentsPrioMAX)) * attributesInfluenceByStudents);
			currentStudent.getTeambuilding() + ((element.getTeambuilding() - currentStudent.getTeambuilding()) * (1 - (element.getPriority() / studentsPrioMAX)) * attributesInfluenceByStudents);
		}

		for (Location location : locations) {
			currentStudent.getTeamSkill() + ((location.getTeamSkill() - currentStudent.getTeamSkill()) * (1 - (location.getPriority() / studentsPrioMAX)) * attributesInfluenceByLocations);
			currentStudent.getLearning() + ((location.getLearning() - currentStudent.getLearning()) * (1 - (location.getPriority() / studentsPrioMAX)) * attributesInfluenceByLocations);
			currentStudent.getPartying() + ((location.getPartying() - currentStudent.getPartying()) * (1 - (location.getPriority() / studentsPrioMAX)) * attributesInfluenceByLocations);
			currentStudent.getDrinking() + ((location.getDrinking() - currentStudent.getDrinking()) * (1 - (location.getPriority() / studentsPrioMAX)) * attributesInfluenceByLocations);
			currentStudent.getTeambuilding() + ((location.getTeambuilding() - currentStudent.getTeambuilding()) * (1 - (location.getPriority() / studentsPrioMAX)) * attributesInfluenceByLocations);
		}
	}

	//adjusting direction
	private void adjustDirection(Student referenceStudent) {
		//iterate through all students and locations and
		double dX = 0.0;
		double dY = 0.0;
		double studentsdVX = 0.0;
		double studentsdVY = 0.0;
		double locationsdVX = 0.0;
		double locationsdVY = 0.0;

		//students
		for (Student element : students) {
			dX = element.getX() - referenceStudent.getX();
			dY = element.getY() - referenceStudent.getY();

			studentsdVX += dX * (1 - (element.getPriority() / studentsPrioMAX));
			studentsdVY += dY * (1 - (element.getPriority() / studentsPrioMAX));
		}

		//locations
		for (Location location : locations) {
			dX = location.getX() - referenceStudent.getX();
			dY = location.getY() - referenceStudent.getY();
			locationsdVX += dX * (1 - (location.getPriority() / locationsPrioMAX));
			locationsdVY += dY * (1 - (location.getPriority() / locationsPrioMAX));
		}

		dX = referenceStudent.getVX() + studentsdVX * directionInfluenceByStudents + locationsdVX * directionInfluenceByLocations;
		dY = referenceStudent.getVY() + studentsdVY * directionInfluenceByStudents + locationsdVY * directionInfluenceByLocations;

		double factor = Math.sqr((referenceStudent.getVMAX() * referenceStudent.getVMAX()) / ((dX * dX) + (dY * dY)));

		referenceStudent.setVX(dX * factor);
		referenceStudent.setVY(dY * factor);
	}

	private void leaveLocation() {
		Timeline timeline = m_dhimulate.getTimeline();
	}


	private void prioritizeAllLocations(Student referencestudent) {
		prioritizeLocation(m_dhimulate.getDisco());
		prioritizeLocation(m_dhimulate.getBib());
		prioritizeLocation(m_dhimulate.getUni());
		prioritizeLocation(m_dhimulate.getHome());
	}

	private void prioritizeLocation(Student referencestudent, Location location) {
		double distance = getDistance(referenceStudent.getX(), referenceStudent.getY(), location.getX(), location.getY());
		Timeline timeline = m_dhimulate.getTimeline();
		double timelinePrio = location.getTimelinePrio(timeline.getStatus());

		location.setPriority(distanceLocationInfluence * distance + timelinePrio * timelineInfluence);
		if (location.getPriority() > locationsPrioMAX) {
			locationsPrioMAX = location.getPriority();
		}
	}

	private double getDistance(double x1, double y1, double x2, double y2) {
		double dX = x1 - x2;
		double dY = y1 - y2;
		return Math.sqr(dX * dX + dY * dY);
	}

	private void prioritizeStudents(Student referenceStudent) {
		List<Student> prioritizedList = new LinkedList<Student>();
		//iterate through all students and compare attributes
		double attributesDifference = 0.0;
		double distance = 0.0;
		double dX = 0.0;
		double dY = 0.0;
		double distanceV = 0.0;
		double dVX = 0.0;
		double dVY = 0.0;
		double priority = 0.0;
		studentsPrioMAX = 0.0;
		for (Student element : students) {

			//exit if its the student we are comparing to
			if (element.id == referenceStudent.id) {
				continue;
			}

			//compare attributes
			attributesDifference = 0.0;
			attributesDifference += Math.abs(element.getTeamSkill() - referenceStudent.getTeamSkill());
			attributesDifference += Math.abs(element.getLearning() - referenceStudent.getLearning());
			attributesDifference += Math.abs(element.getPartying() - referenceStudent.getPartying());
			attributesDifference += Math.abs(element.getDrinking() - referenceStudent.getDrinking());
			attributesDifference += Math.abs(element.getTeambuilding() - referenceStudent.getTeambuilding());

			//compare position
			distance = getDistance(element.getX(), element.getY(), referenceStudent.getX(), referenceStudent.getY());

			if (distance < studentsgap) {
				distance *= 3;
			}

			//compare Direction
			distanceV = getDistance(element.getVX(), element.getVY(), referenceStudent.getVX(), referenceStudent.getVY())

			//combine those
			element.setPriority(attributesDifference * attributesInfluence + distance * distanceStudentInfluence + distanceV * directionInfluence);

			if (element.getPriority() > studentsPrioMAX) {
				studentsPrioMAX = element.getPriority();
			}
		}
	}
}