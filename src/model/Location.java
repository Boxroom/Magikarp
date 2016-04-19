package model;

/**
 * @author nilsw
 */
public class Location extends SimElement {

    private Timeline timeline;

    private String name;


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
