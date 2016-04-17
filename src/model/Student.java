package model;


/**
 * @author Sebastian, nilsw
 */
public class Student {

    private static int counter = 0;
    private final int id;

    private Vector2D position;
    private Vector2D direction;

    private int     condition;
    private boolean binged, failed, moving;
    private int[] grades = new int[2]; //grades, count of 2 for now -->fail 2 times -->failed=true-->student's gone
    private String forename, surname, enrolmentNumber, course;
    private double priority, composition, teamSkill, learning, partying, drinking, teambuilding; /*double, to represent the percentage of the students' composition (30% drinkers, 20% nerds etc.)*/

    public Student() {
        this.id = counter++;
    }

    /* following only getters and setters */

    public double getPriority() {
        return priority;
    }

    public void setPriority(final double priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(final boolean moving) {
        this.moving = moving;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(final int condition) {
        this.condition = condition;
    }

    public boolean isBinged() {
        return binged;
    }

    public void setBinged(final boolean binged) {
        this.binged = binged;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public void setDirection(final Vector2D direction) {
        this.direction = direction;
    }

    public int[] getGrades() {
        return grades;
    }

    public void setGrades(final int[] grades) {
        this.grades = grades;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(final String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getEnrolmentNumber() {
        return enrolmentNumber;
    }

    public void setEnrolmentNumber(final String enrolmentNumber) {
        this.enrolmentNumber = enrolmentNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(final String course) {
        this.course = course;
    }

    public double getComposition() {
        return composition;
    }

    public void setComposition(final double composition) {
        this.composition = composition;
    }

    public double getTeamSkill() {
        return teamSkill;
    }

    public void setTeamSkill(final double teamSkill) {
        this.teamSkill = teamSkill;
    }

    public double getLearning() {
        return learning;
    }

    public void setLearning(final double learning) {
        this.learning = learning;
    }

    public double getPartying() {
        return partying;
    }

    public void setPartying(final double partying) {
        this.partying = partying;
    }

    public double getDrinking() {
        return drinking;
    }

    public void setDrinking(final double drinking) {
        this.drinking = drinking;
    }

    public double getTeambuilding() {
        return teambuilding;
    }

    public void setTeambuilding(final double teambuilding) {
        this.teambuilding = teambuilding;
    }
}
