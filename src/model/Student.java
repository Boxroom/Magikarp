
public class Student {
	
	private int totalNumber, condition, deathByAlc, fallThrough;
	private int[] position = new int[2]; //Position
	private int[] grades = new int[2]; //Noten, erstmal auf 2 festgelegt-->2Mal durch Prüfung fallen-->falltrough=1-->weg mit dem Student
	private String forename, name, matrikelnumber, course;
	private double composition, teamSkill, learning, partying, drinking, teambuilding; /*double, damit die Attribute leicht in Prozent umgerechnet werden können
																									für die Zusammensetzung der Studenten (30% Säufer, 20%Streber etc..)*/
	/**
	 * @return the totalNumber of students
	 */
	public int getTotalNumber() {
		return totalNumber;
	}
	/**
	 * @return the condition of a student
	 */
	public int getCondition() {
		return condition;
	}
	/**
	 * @return the deathByAlc: 0 if alive, 1 if dead
	 */
	public int getDeathByAlc() {
		return deathByAlc;
	}
	/**
	 * @return the fallThrough: 0 if still in the course, 1 if exmatrikuliert
	 */
	public int getFallThrough() {
		return fallThrough;
	}
	/**
	 * @return the position of a student, based on the x- and y-axis
	 */
	public int[] getPosition() {
		return position;
	}
	/**
	 * @return the grades of a student
	 */
	public int[] getGrades() {
		return grades;
	}
	/**
	 * @return the forename
	 */
	public String getForename() {
		return forename;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the matrikelnumber
	 */
	public String getMatrikelnumber() {
		return matrikelnumber;
	}
	/**
	 * @return the course
	 */
	public String getCourse() {
		return course;
	}
	/**
	 * @return the composition of all students...i think this class is not the right place for this variable
	 */
	public double getComposition() {
		return composition;
	}
	/**
	 * @return the teamSkill
	 */
	public double getTeamSkill() {
		return teamSkill;
	}
	/**
	 * @return the learning
	 */
	public double getLearning() {
		return learning;
	}
	/**
	 * @return the partying
	 */
	public double getPartying() {
		return partying;
	}
	/**
	 * @return the drinking
	 */
	public double getDrinking() {
		return drinking;
	}
	/**
	 * @return the teambuilding
	 */
	public double getTeambuilding() {
		return teambuilding;
	}
	/**
	 * @param totalNumber the totalNumber to set
	 */
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(int condition) {
		this.condition = condition;
	}
	/**
	 * @param deathByAlc the deathByAlc to set
	 */
	public void setDeathByAlc(int deathByAlc) {
		this.deathByAlc = deathByAlc;
	}
	/**
	 * @param fallThrough the fallThrough to set
	 */
	public void setFallThrough(int fallThrough) {
		this.fallThrough = fallThrough;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(int[] position) {
		this.position = position;
	}
	/**
	 * @param grades the grades to set
	 */
	public void setGrades(int[] grades) {
		this.grades = grades;
	}
	/**
	 * @param forename the forename to set
	 */
	public void setForename(String forename) {
		this.forename = forename;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param matrikelnumber the matrikelnumber to set
	 */
	public void setMatrikelnumber(String matrikelnumber) {
		this.matrikelnumber = matrikelnumber;
	}
	/**
	 * @param course the course to set
	 */
	public void setCourse(String course) {
		this.course = course;
	}
	/**
	 * @param composition the composition to set
	 */
	public void setComposition(double composition) {
		this.composition = composition;
	}
	/**
	 * @param teamSkill the teamSkill to set
	 */
	public void setTeamSkill(double teamSkill) {
		this.teamSkill = teamSkill;
	}
	/**
	 * @param learning the learning to set
	 */
	public void setLearning(double learning) {
		this.learning = learning;
	}
	/**
	 * @param partying the partying to set
	 */
	public void setPartying(double partying) {
		this.partying = partying;
	}
	/**
	 * @param drinking the drinking to set
	 */
	public void setDrinking(double drinking) {
		this.drinking = drinking;
	}
	/**
	 * @param teambuilding the teambuilding to set
	 */
	public void setTeambuilding(double teambuilding) {
		this.teambuilding = teambuilding;
	}


}