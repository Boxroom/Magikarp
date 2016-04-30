package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.print.DocFlavor;

/**
 * @author nilsw, jendrik
 */
public class Location extends SimElement {

    public static String[] names =  {"Disco","Universität","Bibliothek","Zuhause"};

    private Timeline timeline;

    private String name = "default";

    private ImageView image;

    public String getName() {
        return name;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTimeline(final Timeline timeline) {
        this.timeline = timeline;
    }

    private int studentsinside = 0;

    public void setImage(ImageView img){
        this.image = img;
    }

    public ImageView getImage(){
        return image;
    }

    private void adjustScaling(){
        double factor=((double)1)+((double)(studentsinside))/((double)20);
        image.setScaleX(factor);
        image.setScaleY(factor);
        image.setScaleZ(factor);
    }

    public Boolean grow(){
        if(image!=null) {
            studentsinside++;
            adjustScaling();
            return true;
        }
        return false;
    }

    public Boolean shrink(){
        if(image!=null && studentsinside>0) {
            studentsinside--;
            adjustScaling();
            return true;
        }
        return false;
    }

    @Override
    public void setPosition(final Vector2D pos){
        super.setPosition(pos);
        if(image!=null){
            image.setLayoutX(pos.mX);
            image.setLayoutY(pos.mY);
        }else{
            System.out.println(getName()+" was null");
        }
    }

    public Vector2D getCenterPosition() {
        Vector2D pos = super.getPosition();
        Vector2D n = new Vector2D(pos.mX,pos.mY);
        n.mX += 20;
        n.mY += 20;
        return n;
    }


    @Override
    public void setPosition(double x, double y){
        super.setPosition(x, y);
        if(image!=null){
            image.setLayoutX(x);
            image.setLayoutY(y);
        }else{
            System.out.println(getName() +" was null");
        }
    }

    public Location(int id){
        super(id);
        timeline = new Timeline();
    }

}
