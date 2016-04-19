package model;

/**
 * Created by nilsw
 */
public enum Status {
    NO_EVENT,
    IN_EVENT,
    BEFORE_EVENT;


    public static int toInteger(Status d) {
        int r = 1;
        switch (d) {
            case NO_EVENT:
                r = 1;
                break;
            case IN_EVENT:
                r = 2;
                break;
            case BEFORE_EVENT:
                r = 3;
                break;
        }
        return r;
    }
}
