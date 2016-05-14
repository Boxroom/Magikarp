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
    private Label notificationlabel;

    public Location(int id, String name) {
        super(id);
        this.name = name;
        timeline = new Timeline();
    }

    public boolean isShowingNotification() {
        return showingNotification;
    }

    public Label getNotificationlabel() {
        return notificationlabel;
    }

    public void setNotificationlabel(Label notificationlabel) {
        this.notificationlabel = notificationlabel;

        resetNotification();
    }

    public void resetNotification() {
        notificationlabel.setVisible(false);
        final Vector2D v = getPosition();
        notificationlabel.setLayoutX(v.mX - 20);
        notificationlabel.setLayoutY(v.mY - 30);
        setNotificationCnt(0);
        notificationlabel.setOpacity(1);
        setShowingNotification(false);
    }

    public void setShowingNotification(boolean showingNotification) {
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
        final double factor = ((double) 1) + ((double) (studentsInside)) / ((double) 20);
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
        setNotificationCnt(getNotificationCnt() + 1);
        notificationlabel.setOpacity(notificationlabel.getOpacity() - 0.002);
        notificationlabel.setLayoutY(notificationlabel.getLayoutY() - 0.1);
        if (getNotificationCnt() > notificationCntMax) {
            hideNotification();
        }
    }

    public int getNotificationCnt() {
        return notificationCnt;
    }

    public void setNotificationCnt(int notificationCnt) {
        this.notificationCnt = notificationCnt;
    }

    public void hideNotification() {
        resetNotification();
    }

    public void showNotification(String txt) {
        resetNotification();
        notificationlabel.setText(txt);
        notificationlabel.setVisible(true);
        setShowingNotification(true);
    }

}
