package model;

import java.util.List;

/**
 * @author nilsw
 */
public class Timeline {

    private List<TimelineEvent> events;

    private double b4range = 1000;

    public List<TimelineEvent> getEvents() {
        return events;
    }

    public void addEvent(final TimelineEvent event) {
        this.events.add(event);
    }

    public boolean removeEvent(final TimelineEvent event) {
        return this.events.remove(event);
    }

    public double getStatus(double now){
        for( TimelineEvent event : events ){
            if(now > event.start && now < event.end ){
                return Status.InEvent;
            }else if( event.start - now < b4range ){
                return Status.B4Event;
            }
        }
        return Status.NoEvent;
    }
}
