package model;


import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * @author Sebastian, nilsw, jendrik
 */
public class Student extends SimElement {

    private Vector2D direction = new Vector2D(0, 0);

    private int condition;
    private boolean binged, failed = false, moving = true;
    private int[] grades = new int[2]; //grades, count of 2 for now -->fail 2 times -->failed=true-->student's gone
    private String forename, surname, enrolmentNumber, course;
    private Circle    m_circle;
    private ImageView deathImg;
    private boolean   alive;
    private       int deathanimcnt = 0;
    public static int deathanimMax = 150;

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean disabled = false;

    public void setHealth(double health) {
        this.health = health;
    }

    public double getHealth() {
        return health;
    }

    private double health = 100;

    public void setInlocationcnt(int inlocationcnt) {
        this.inlocationcnt = inlocationcnt;
    }

    public int getInLocationCnt() {
        return inlocationcnt;
    }

    public void setInsidelocation(Location insidelocation) {
        this.insidelocation = insidelocation;
    }

    public Location getInsidelocation() {
        return insidelocation;
    }

    private Location insidelocation;


    private int inlocationcnt = 0;

    public Student(int id) {
        super(id);
        alive = true;
        setPosition(Math.random() * 1280, 50 + Math.random() * 700);
        setDirection(Math.random(), Math.random());
    }


    /* following only getters and setters */

    public int getId() {
        return id;
    }

    public void setCircle(Circle c) {
        m_circle = c;
    }

    public Circle getCircle() {
        return m_circle;
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

    public boolean isBinged() {
        return binged;
    }

    public void setBinged(final boolean binged) {
        this.binged = binged;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public void setDirection(final Vector2D direction) {
        this.direction = direction;
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

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        setPosition(getPosition());
    }

    public int[] getGrades() {
        return grades;
    }

    public void setGrades(final int[] grades) {
        this.grades = grades;
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

    public int getdeathanimcnt() {
        return deathanimcnt;
    }

    private void setdeathanimcnt(int deathanimcnt) {
        this.deathanimcnt = deathanimcnt;
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
}
