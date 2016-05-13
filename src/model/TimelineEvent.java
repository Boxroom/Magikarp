package model;

/**
 * @author nilsw
 */
public class TimelineEvent {
    public final String name;
    public final double start;
    public final double end;

    public TimelineEvent(final String name, double start, double end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }
}
