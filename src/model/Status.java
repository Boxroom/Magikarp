package model;

/**
 * Created by nilsw
 */
public enum Status {
    NO_EVENT,
    IN_EVENT,
    BEFORE_EVENT;

    public int toInt() {
        switch (this) {
            case IN_EVENT:
                return 2;
            case BEFORE_EVENT:
                return 3;
            default: // NO_EVENT
                return 1;
        }
    }
}
