package model;

import javax.print.DocFlavor;

/**
 * @author nilsw, jendrik
 */
public class Location extends SimElement {

    public static String[] names =  {"Disco","Universität","Bibliothek","Zuhause"};

    private Timeline timeline;

    private String name;


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

    public Location(int id){
        super(id);
        setPosition(500,500);
        timeline = new Timeline();
    }

}
