package model;

/**
 * @author nilsw
 */
public class Location extends SimElement {

    private Timeline timeline;

    private String name;

    private Vector2D position;

    private double[] attributes = new double[5];


    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(final Timeline timeline) {
        this.timeline = timeline;
    }

}
