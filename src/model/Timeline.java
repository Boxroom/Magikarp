package model;

import java.util.List;

/**
 * @author nilsw
 */
public class Timeline {

    private List<TimelineEvent> events;

    public List<TimelineEvent> getEvents() {
        return events;
    }

    public void addEvent(final TimelineEvent event) {
        this.events.add(event);
    }

    public boolean removeEvent(final TimelineEvent event) {
        return this.events.remove(event);
    }
}
