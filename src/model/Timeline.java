package model;

import java.util.List;

/**
 * @author nilsw, Jendrik
 */
public class Timeline {

    private List<TimelineEvent> events;

    private double beforeRange = 1000;

    public List<TimelineEvent> getEvents() {
        return events;
    }

    public void addEvent(final TimelineEvent event) {
        this.events.add(event);
    }

    public boolean removeEvent(final TimelineEvent event) {
        return this.events.remove(event);
    }

    public Status getStatus(double now) {
        for (TimelineEvent event : events) {
            if (now > event.start && now < event.end) {
                return Status.IN_EVENT;
            }
            else if (event.start - now < beforeRange) {
                return Status.BEFORE_EVENT;
            }
        }
        return Status.NO_EVENT;
    }
}
