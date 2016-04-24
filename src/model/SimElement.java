package model;

/**
 * Created by Jendrik, nilsw
 */
public abstract class SimElement {

    protected int id;

    public static final int ATTR_COUNT = 5;

    private Vector2D position = new Vector2D(0,0);

    private double priority;
    private double[] attributes = new double[ATTR_COUNT]; /*double, to represent the percentage of the students' composition (30% drinkers, 20% nerds etc.)*/

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
        Vector2D v = getPosition();
        v.mX = x;
        v.mY = y;
        setPosition(v);
    }
    public double[] getAttributes() {
        return attributes;
    }

    public void setAttributes(final double[] attributes) {
        this.attributes = attributes;
    }

    public int getID(){
        return id;
    }

    public SimElement(int id){
        this.id=id;
    }
}
