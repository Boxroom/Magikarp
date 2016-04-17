package model;

/**
 * @author Nils
 */
public class Vector2D {
    public static final Vector2D X_AXIS = new Vector2D(1.0, 0.0);
    public static final Vector2D Y_AXIS = new Vector2D(0.0, 1.0);

    public double mX;
    public double mY;

    public Vector2D(double x, double y) {
        mX = x;
        mY = y;
    }

    private Vector2D() {
    }

    //adds a vector2D (v) to this Vector2D and returns a new Vector2D
    //this does not change any of the vectors being added
    public Vector2D add(Vector2D v) {
        Vector2D result = new Vector2D();
        result.mX = this.mX + v.mX;
        result.mY = this.mY + v.mY;
        return result;
    }

    //adds a Vector2D (v) to the Vector2D
    //this changes the vector being added to,the vector being added is not changed
    public void add2(Vector2D v) {
        mX += v.mX;
        mY += v.mY;
    }

    //substract a vector2D (v) from this Vector2D and return a new Vector2D
    //this does not change any of the vectors being used
    public Vector2D subtract(Vector2D v) {
        Vector2D result = new Vector2D();
        result.mX = this.mX - v.mX;
        result.mY = this.mY - v.mY;
        return result;
    }

    //subtracts a Vector2D (v) from the Vector2D
    //this changes the vector being subtracted from,the vector being subtracted is not changed
    public void subtract2(Vector2D v) {
        mX -= v.mX;
        mY -= v.mY;
    }

    //rotates the Vector2D
    public void rotate(double f) {
        mX = -Math.sin(f);
        mY = Math.cos(f);
    }

    //calculates the distance between the Vector2D and another Vector2D (v). This assumes that the vectors represent points
    public double getDistanceTo(Vector2D v) {
        final Vector2D connectingVector = this.subtract(v);
        return connectingVector.length();
    }

    //returns the length of the Vector2D
    public double length() {
        return Math.sqrt(square(this.mX) + square(this.mY));
    }

    //returns the square of a number, needed since ^ is not defined for double values
    private double square(double a) {
        return a * a;
    }


    //calculates the angle of this Vector2D to another Vector2D (v)
    public double getAngleTo(Vector2D v) {
        final double cosPhi = this.scalarProduct(v) / (this.length() * v.length());
        return Math.acos(cosPhi);
    }

    //returns the scalar product of this Vector2D and another Vector2D (v) as a new Vector
    public double scalarProduct(Vector2D v) {
        return this.mX * v.mX + this.mY * v.mY;
    }

    //calculates the normal vector to the left
    public Vector2D getNormalToLeft() {
        return new Vector2D(-this.mY, this.mX);
    }

    //calculates the normal vector to the right
    public Vector2D getNormalToRight() {
        return new Vector2D(this.mY, -this.mX);
    }

    //scales the vector to a vector of the length 1
    public Vector2D normalize() {
        final double factor = 1 / this.length();
        return this.scalarMultiplication(factor);
    }

    //multiplies the Vector2D with a constant factor and returns the result as a new Vector
    //the Vector2D is not changed
    public Vector2D scalarMultiplication(double factor) {
        Vector2D result = new Vector2D();
        result.mX = this.mX * factor;
        result.mY = this.mY * factor;
        return result;
    }

    //multiplies the Vector2D with a constant factor
    //the Vector2D is changed
    public void scalarMultiplication2(double factor) {
        mX *= factor;
        mY *= factor;
    }
}
