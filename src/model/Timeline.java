package model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author nilsw, Jendrik
 */
public class Timeline {

    private List<TimelineEvent> events;

    private double beforeRange = 1.0;

    public Timeline() {
        this.events = new LinkedList<>();
    }

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
            if(event.start<event.end) {
                if (event.start <= now && now <= event.end) {
                    // if the event is running
                    return Status.IN_EVENT;
                } else if (event.start - beforeRange <= now && now < event.start) {
                    // if we're right before the event
                    return Status.BEFORE_EVENT;

                }
            }else if(event.start>event.end) {
                if (event.start <= now || now <= event.end) {
                    // if the event is running
                    return Status.IN_EVENT;
                } else if (event.start - beforeRange <= now && now < event.start) {
                    // if we're right before the event
                    return Status.BEFORE_EVENT;

                }
            }
        }
        return Status.NO_EVENT;
    }

    public TimelineEvent getCurrentEvent(double now){
        for (TimelineEvent event : events) {
            if(event.start<event.end) {
                if (event.start <= now && now <= event.end) {
                    // if the event is running
                    return event;
                }
                else if (event.start - beforeRange <= now && now < event.start) {
                    // if we're right before the event
                    return event;
                }
            }else if(event.start>event.end) {
                if (event.start <= now || now <= event.end) {
                    // if the event is running
                    return event;
                }
                else if (event.start - beforeRange <= now && now < event.start) {
                    // if we're right before the event
                    return event;
                }
            }
        }
        return null;
    }
}
