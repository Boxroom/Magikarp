package model;

/**
 * @author nilsw
 */
public class TimelineEvent {
    public String name;
    public int    start, end;

    public TimelineEvent(final String name, final int start, final int end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }
}
