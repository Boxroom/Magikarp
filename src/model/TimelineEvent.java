package model;

/**
 * @author nilsw
 */
public class TimelineEvent {
    public String name;
    public double   start, end;

    public TimelineEvent(final String name, double start, double end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }
}
