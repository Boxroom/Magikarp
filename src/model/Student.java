package model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * @author Sebastian, nilsw, jendrik
 */
public class Student extends SimElement {

    public static final int             deathAnimMax = 150;
    private             boolean         disabled     = false;
    private             Vector2D        direction    = new Vector2D(0, 0);
    private             boolean         moving       = true;
    private final       BooleanProperty failed       = new SimpleBooleanProperty(false);
    private Circle    m_circle;
    private ImageView deathImg;
    private boolean alive        = true;
    private int     deathAnimCnt = 0;
    private double  health       = 100;
    private Location insideLocation;
    private int inLocationCnt = 0;

    public Student(int id) {
        super(id);
        setPosition(Math.random() * 1280, 53 + Math.random() * 720);
        setDirection(Math.random(), Math.random());
    }

    public void setDirection(double x, double y) {
        final Vector2D v = getDirection();
        v.mX = x;
        v.mY = y;
        setDirection(v);
    }

    @Override
    public void setPosition(final Vector2D pos) {
        super.setPosition(pos);
        final Circle c = getCircle();
        if (c != null) {
            m_circle.setLayoutX(pos.mX);
            m_circle.setLayoutY(pos.mY);
        }
    }

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        setPosition(position);
    }

    public void move(long elapsed) {
        setPosition(position.mX + direction.mX * (elapsed / 10000000), position.mY + direction.mY * (elapsed / 10000000));
    }

    public void die() {
        moving = false;
        if (m_circle.isVisible()) {
            m_circle.setVisible(false);
            deathImg.setVisible(true);
            deathImg.setLayoutX(m_circle.getLayoutX() - deathImg.getFitWidth() / 2 - 100);
            deathImg.setLayoutY(m_circle.getLayoutY() - deathImg.getFitHeight() / 2 - 100);
        }
        alive = false;
    }

    public void vanish() {
        deathImg.setVisible(false);
        disabled = true;
    }

    public void simDeath() {
        ++deathAnimCnt;
        deathImg.setOpacity(deathImg.getOpacity() - 0.005);
        deathImg.setScaleX(deathImg.getScaleX() * 1.005);
        deathImg.setScaleY(deathImg.getScaleX() * 1.005);
    }


    /* following only basic getters and setters */

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

    public int getDeathAnimCnt() {
        return deathAnimCnt;
    }

    private void setDeathAnimCnt(int deathanimcnt) {
        this.deathAnimCnt = deathanimcnt;
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

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getInLocationCnt() {
        return inLocationCnt;
    }

    public void setInLocationCnt(int inLocationCnt) {
        this.inLocationCnt = inLocationCnt;
    }

    public Location getInsideLocation() {
        return insideLocation;
    }

    public void setInsideLocation(Location insideLocation) {
        this.insideLocation = insideLocation;
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

    public boolean isAlive() {
        return alive;
    }

    public void setDeathImg(ImageView deathImg) {
        this.deathImg = deathImg;
    }
}
