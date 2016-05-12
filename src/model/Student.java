package model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * @author Sebastian, nilsw, jendrik
 */
public class Student extends SimElement {

    public static int      deathAnimMax = 150;
    public        boolean  disabled     = false;
    private       Vector2D direction    = new Vector2D(0, 0);
    private int condition;
    private boolean         moving = true;
    private BooleanProperty failed = new SimpleBooleanProperty(false);
    private String forename, surname, enrolmentNumber, course;
    private Circle    m_circle;
    private ImageView deathImg;
    private boolean   alive;
    private int    deathAnimCnt = 0;
    private double health       = 100;
    private Location insidelocation;
    private int inlocationcnt = 0;

    public Student(int id) {
        super(id);
        alive = true;
        setPosition(Math.random() * 1280, 50 + Math.random() * 700);
        setDirection(Math.random(), Math.random());
    }

    public void setDirection(double x, double y) {
        Vector2D v = getDirection();
        v.mX = x;
        v.mY = y;
        setDirection(v);
    }

    @Override
    public void setPosition(final Vector2D pos) {
        super.setPosition(pos);
        Circle c = getCircle();
        if (c != null) {
            getCircle().setLayoutX(pos.mX);
            getCircle().setLayoutY(pos.mY);
            //if(id==1){System.out.println("[moved] id:"+id+" to x:"+pos.mX+" y:"+pos.mY +" direction:"+getDirection().mX);}
        }
        else {
            //System.out.println("student [id:"+id+"] was null");
        }
    }

    public Vector2D getDirection() {
        return direction;
    }

    public Circle getCircle() {
        return m_circle;
    }

    public void setCircle(Circle c) {
        m_circle = c;
    }

    public void setDirection(final Vector2D direction) {
        this.direction = direction;
    }

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        setPosition(getPosition());
    }

    public boolean getFailed() {
        return failed.get();
    }

    public void setFailed(final boolean failed) {
        this.failed.set(failed);
    }

    public BooleanProperty failedProperty() {
        return failed;
    }

    public boolean isDisabled() {
        return disabled;
    }


    /* following only getters and setters */

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setInlocationcnt(int inlocationcnt) {
        this.inlocationcnt = inlocationcnt;
    }

    public int getInLocationCnt() {
        return inlocationcnt;
    }

    public Location getInsidelocation() {
        return insidelocation;
    }

    public void setInsidelocation(Location insidelocation) {
        this.insidelocation = insidelocation;
    }

    public int getId() {
        return id;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(final boolean moving) {
        this.moving = moving;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(final int condition) {
        this.condition = condition;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(final String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getEnrolmentNumber() {
        return enrolmentNumber;
    }

    public void setEnrolmentNumber(final String enrolmentNumber) {
        this.enrolmentNumber = enrolmentNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(final String course) {
        this.course = course;
    }

    public void move(long elapsed) {
        setPosition(getPosition().mX + getDirection().mX * (elapsed / 10000000), getPosition().mY + getDirection().mY * (elapsed / 10000000));
    }

    public void die() {
        getCircle().setVisible(false);
        deathImg.setVisible(true);
        deathImg.setLayoutX(getCircle().getLayoutX() - deathImg.getFitWidth() / 2 - 100);
        deathImg.setLayoutY(getCircle().getLayoutY() - deathImg.getFitHeight() / 2 - 100);
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setDeathImg(ImageView deathImg) {
        this.deathImg = deathImg;
    }

    public void vanish() {
        deathImg.setVisible(false);
        disabled = true;
    }

    public void simDeath() {
        setdeathanimcnt(getdeathanimcnt() + 1);
        deathImg.setOpacity(deathImg.getOpacity() - 0.005);
        deathImg.setScaleX(deathImg.getScaleX() * 1.005);
        deathImg.setScaleY(deathImg.getScaleX() * 1.005);
    }

    private void setdeathanimcnt(int deathanimcnt) {
        this.deathAnimCnt = deathanimcnt;
    }

    public int getdeathanimcnt() {
        return deathAnimCnt;
    }
}
