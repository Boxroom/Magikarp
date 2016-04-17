package model;

/**
 * @author nilsw
 */
public class Location {

    public Vector2D position;
    public Timeline timeline;
    public String   name;
    int[][] attributes;
    private double priority;

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(final Timeline timeline) {
        this.timeline = timeline;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(final double priority) {
        this.priority = priority;
    }
}
