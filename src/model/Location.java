package model;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * @author nilsw, jendrik
 */
public class Location extends SimElement {

    public static String[] names = {"Disco", "Universität", "Bibliothek", "Zuhause"};

    private Timeline timeline;

    private String name = "default";


    public void setShowingnotification(boolean shwoingnotification) {
        this.showingnotification = shwoingnotification;
    }

    public boolean isShowingNotification() {
        return showingnotification;
    }


    private ImageView image;
    private int notificationcntMAX = 300;

    public void setNotificationcnt(int notificationcnt) {
        this.notificationcnt = notificationcnt;
    }

    public int getNotificationcnt() {
        return notificationcnt;
    }

    private int     notificationcnt     = 0;
    private boolean showingnotification = false;

    public void setNotificationlabel(Label notificationlabel) {
        this.notificationlabel = notificationlabel;

        resetNotification();
    }

    public Label getNotificationlabel() {
        return notificationlabel;
    }

    private Label notificationlabel;

    public String getName() {
        return name;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeline(final Timeline timeline) {
        this.timeline = timeline;
    }

    private int studentsinside = 0;

    public void setImage(ImageView img) {
        this.image = img;
    }

    public ImageView getImage() {
        return image;
    }

    private void adjustScaling() {
        double factor = ((double) 1) + ((double) (studentsinside)) / ((double) 20);
        image.setScaleX(factor);
        image.setScaleY(factor);
        image.setScaleZ(factor);
    }

    public Boolean grow() {
        if (image != null) {
            studentsinside++;
            adjustScaling();
            return true;
        }
        return false;
    }

    public Boolean shrink() {
        if (image != null && studentsinside > 0) {
            studentsinside--;
            adjustScaling();
            return true;
        }
        return false;
    }

    @Override
    public void setPosition(final Vector2D pos) {
        super.setPosition(pos);
        if (image != null) {
            image.setLayoutX(pos.mX);
            image.setLayoutY(pos.mY);
        }
        else {
            System.out.println(getName() + " was null");
        }
    }

    public Vector2D getCenterPosition() {
        Vector2D pos = super.getPosition();
        Vector2D n = new Vector2D(pos.mX, pos.mY);
        n.mX += 20;
        n.mY += 20;
        return n;
    }


    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        if (image != null) {
            image.setLayoutX(x);
            image.setLayoutY(y);
        }
        else {
            System.out.println(getName() + " was null");
        }
    }

    public void animNotification() {
        setNotificationcnt(getNotificationcnt() + 1);
        notificationlabel.setOpacity(notificationlabel.getOpacity() - 0.002);
        notificationlabel.setLayoutY(notificationlabel.getLayoutY() - 0.1);
        if (getNotificationcnt() > notificationcntMAX) {
            hideNotification();
        }
    }

    public void resetNotification() {
        notificationlabel.setVisible(false);
        Vector2D v = getPosition();
        notificationlabel.setLayoutX(v.mX - 20);
        notificationlabel.setLayoutY(v.mY - 30);
        setNotificationcnt(0);
        notificationlabel.setOpacity(1);
        setShowingnotification(false);
    }

    public void hideNotification() {
        resetNotification();
    }

    public void showNotification(String txt) {
        resetNotification();
        notificationlabel.setText(txt);
        notificationlabel.setVisible(true);
        setShowingnotification(true);
    }

    public Location(int id) {
        super(id);
        timeline = new Timeline();
    }

}
