package model;

/**
 * Created by Jendrik, nilsw
 */
public abstract class SimElement {

    public static final int    ATTR_COUNT = 5;
    public static final int    LEADERSHIP = 1;
    public static final int    TEAM       = 2;
    public static final int    LEARNING   = 3;
    public static final int    ALCOHOL    = 4;
    public static final int    PARTY      = 0;
    public static final double dangerMAX  = 200;
    final int id;
    private Vector2D position = new Vector2D(0, 0);
    private double priority;
    private double[] attributes = new double[ATTR_COUNT];
    private double danger;
    private double dist = 0;

    SimElement(int id) {
        this.id = id;
        danger = 0;
    }

    public void calcDanger() {
        double danger = 0;
        danger += getAttribute(SimElement.ALCOHOL);
        danger += getAttribute(SimElement.PARTY);
        danger -= getAttribute(SimElement.LEARNING);
        danger -= getAttribute(SimElement.TEAM);
        if (danger < 0) {
            danger = 0;
        }
        else if (danger > dangerMAX) {
            danger = dangerMAX;
        }
        setDanger(danger);
    }

    public double getDanger() {
        return danger;
    }

    public void setDanger(double danger) {
        this.danger = danger;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        if (dist == 0) {
            dist = 0.00000000001;
        }
        this.dist = dist;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(final double priority) {
        this.priority = priority;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }

    public void setPosition(double x, double y) {
        position.mX = x;
        position.mY = y;
    }

    public double[] getAttributes() {
        return attributes;
    }

    public void setAttributes(final double[] attributes) {
        this.attributes = attributes;
    }

    public void setAttributes(int index, double attr) {
        this.attributes[index] = attr;
    }

    public int getID() {
        return id;
    }

    public double getAttribute(int index) {
        return attributes[index];
    }
}
