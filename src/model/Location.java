package model;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * @author nilsw, jendrik
 */
public class Location extends SimElement {

    private final int notificationCntMax = 300;
    private Timeline  timeline;
    private String    name;
    private ImageView image;
    private int     notificationCnt     = 0;
    private boolean showingNotification = false;
    private int     studentsInside      = 0;
    private Label notificationLabel;

    public Location(int id, String name) {
        super(id);
        this.name = name;
        timeline = new Timeline();
    }

    public boolean isShowingNotification() {
        return showingNotification;
    }

    public Label getNotificationLabel() {
        return notificationLabel;
    }

    public void setNotificationLabel(Label notificationLabel) {
        this.notificationLabel = notificationLabel;

        resetNotification();
    }

    private void resetNotification() {
        notificationLabel.setVisible(false);
        final Vector2D v = getPosition();
        notificationLabel.setLayoutX(v.mX - 20);
        notificationLabel.setLayoutY(v.mY - 30);
        setNotificationCnt(0);
        notificationLabel.setOpacity(1);
        setShowingNotification(false);
    }

    private void setShowingNotification(boolean showingNotification) {
        this.showingNotification = showingNotification;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(final Timeline timeline) {
        this.timeline = timeline;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView img) {
        this.image = img;
    }

    public Boolean grow() {
        if (image != null) {
            studentsInside++;
            adjustScaling();
            return true;
        }
        return false;
    }

    private void adjustScaling() {
        final double factor = 1.0 + studentsInside / 50.0;
        image.setScaleX(factor);
        image.setScaleY(factor);
        image.setScaleZ(factor);
    }

    public Boolean shrink() {
        if (image != null && studentsInside > 0) {
            studentsInside--;
            adjustScaling();
            return true;
        }
        return false;
    }

    @Override
    public void setPosition(final Vector2D pos) {
        super.setPosition(pos);
        image.setLayoutX(pos.mX);
        image.setLayoutY(pos.mY);
    }

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        image.setLayoutX(x);
        image.setLayoutY(y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2D getCenterPosition() {
        final Vector2D pos = super.getPosition();
        final Vector2D n = new Vector2D(pos.mX, pos.mY);
        n.mX += 20;
        n.mY += 20;
        return n;
    }

    public void animNotification() {
        ++notificationCnt;
        notificationLabel.setOpacity(notificationLabel.getOpacity() - 0.002);
        notificationLabel.setLayoutY(notificationLabel.getLayoutY() - 0.1);
        if (notificationCnt > notificationCntMax) {
            resetNotification();
        }
    }

    public int getNotificationCnt() {
        return notificationCnt;
    }

    public void setNotificationCnt(int notificationCnt) {
        this.notificationCnt = notificationCnt;
    }

    public void showNotification(String txt) {
        resetNotification();
        notificationLabel.setText(txt);
        notificationLabel.setVisible(true);
        setShowingNotification(true);
    }

}
