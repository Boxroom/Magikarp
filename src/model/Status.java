package model;

/**
 * Created by nilsw
 */
public enum Status {
    NO_EVENT,
    IN_EVENT,
    BEFORE_EVENT;

    public static int toInt(Status s) {
        switch (s) {
            case IN_EVENT:
                return 1;
            case BEFORE_EVENT:
                return 15;
            default: // NO_EVENT
                return 20;
        }
    }
}
