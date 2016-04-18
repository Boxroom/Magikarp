package model;

/**
 * Created by mlg on 18.04.2016.
 */
public class SimElem {
    private Vector2D position;

    private double priority, composition, teamSkill, learning, partying, drinking, teambuilding; /*double, to represent the percentage of the students' composition (30% drinkers, 20% nerds etc.)*/

    public double getPriority() {
        return priority;
    }

    public void setPriority(final double priority) {
        this.priority = priority;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
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
