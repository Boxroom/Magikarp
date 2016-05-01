package Dhimulate;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.*;
import simulation.Simulation;
import view.Window;

/**
 * @author Jendrik, nilsw
 */
public class Dhimulate extends Application {
    private static final Map<String, Scene> m_ScenesMap = new HashMap<>();
    private static Stage      m_PrimaryStage;
    private static Simulation m_Simulation;
    private        String     m_OS;
    public   static int MAXStudentCNT = 200;
    private    int StudentCNT = 100;
    private    int StudentNumberStart = 0;
    private    int StudentNumber = 0;
    private double[] referenceattributes = new double[SimElement.ATTR_COUNT];
    private List<Student> m_students;
    private List<Location> m_locations;
    private String MainGameSceneName = "sim3";
    private double adjustingtoreference = 0.5;
    private static Label timelabel;
    private static Label studentslabel;
    private static Label semesterlabel;
    private StackPane toppane;
    private static Rectangle darkness;
    private ProgressBar semesterprogress;
    private Button m_pausebutton;
    private Pane klausurenpane;
    private Label semestercntLabel;
    private int semestercnt=1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        m_PrimaryStage = primaryStage;
        loadScenes();
        primaryStage.setScene(getScene("config"));

        primaryStage.setTitle("Magikarp DHBW Simulation");
        primaryStage.setResizable(false);
        primaryStage.show();


        ((Button)getScene("config").lookup("#startButton")).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                referenceattributes[SimElement.ALCOHOL]=((Slider)getScene("config").lookup("#alcSlider")).getValue();

                referenceattributes[SimElement.PARTY]=((Slider)getScene("config").lookup("#partySlider")).getValue();
                referenceattributes[SimElement.LEADERSHIP]=((Slider)getScene("config").lookup("#leaderSlider")).getValue();
                referenceattributes[SimElement.TEAM]=((Slider)getScene("config").lookup("#teamSlider")).getValue();
                referenceattributes[SimElement.LEARNING]=((Slider)getScene("config").lookup("#learnSlider")).getValue();
                StudentCNT=(int)((((Slider)getScene("config").lookup("#countSlider")).getValue()/100)*MAXStudentCNT);
                StudentNumberStart=StudentCNT;
                StudentNumber=StudentCNT;
                System.out.println(((Slider)getScene("config").lookup("#countSlider")).getValue());
                configandstartSim();
            }
        });

        ((Button)getScene(MainGameSceneName).lookup("#pauseButton")).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handlepause((Button)event.getSource());
            }
        });

        timelabel=((Label)getScene(MainGameSceneName).lookup("#timeLabel"));
        semesterlabel=((Label)getScene(MainGameSceneName).lookup("#semesterLabel"));
        studentslabel=((Label)getScene(MainGameSceneName).lookup("#studentenLabel"));
        darkness=((Rectangle) getScene(MainGameSceneName).lookup("#darkness"));
        semesterprogress=((ProgressBar) getScene(MainGameSceneName).lookup("#semesterprogress"));
        toppane=((StackPane) getScene(MainGameSceneName).lookup("#stackpane"));
        m_pausebutton = (Button)getScene(getMainGameSceneName()).lookup("#pauseButton");
        klausurenpane = (Pane)getScene(getMainGameSceneName()).lookup("#klausurenpane");
        semestercntLabel = ((Label)getScene(MainGameSceneName).lookup("#semestercnt"));
    }

    public void handlepause(Button b){
        if(m_Simulation.isRunning()==true){
            m_Simulation.stop();
            b.setText("Weiter");
        }else{
            m_Simulation.start();
            b.setText("Stop");
        }
    }

    private void getConstants(){
        m_Simulation.onesemesterisxdays=((Slider)getScene("config").lookup("#onesemesterisxdaysSlider")).getValue();
        m_Simulation.healthdecreaseondanger=((Slider)getScene("config").lookup("#healthdecreaseondangerSlider")).getValue();
        m_Simulation.adjustattributesInfluenceByStudents=((Slider)getScene("config").lookup("#adjustattributesInfluenceByStudentsSlider")).getValue();
        m_Simulation.adjustattributesInfluenceByLocations=((Slider)getScene("config").lookup("#adjustattributesInfluenceByLocationsSlider")).getValue();
        m_Simulation.directionInfluenceByStudents=((Slider)getScene("config").lookup("#directionInfluenceByStudentsSlider")).getValue();
        m_Simulation.directionInfluenceByLocations=((Slider)getScene("config").lookup("#directionInfluenceByLocationsSlider")).getValue();
        m_Simulation.timelineInfluence=((Slider)getScene("config").lookup("#timelineInfluenceSlider")).getValue();
        m_Simulation.distanceStudentInfluence=((Slider)getScene("config").lookup("#distanceStudentInfluenceSlider")).getValue();
        m_Simulation.distanceLocationInfluence=((Slider)getScene("config").lookup("#distanceLocationInfluenceSlider")).getValue();
    }

    public void updateTime(int day, double[] time){
        timelabel.setText(day+". Tag"+"  "+stockTime(""+((int)time[0]))+":"+stockTime(""+((int)time[1]))/*+":"+((int)time[2])*/+"Uhr");
        if(time[0]>12){
            darkness.setOpacity(-0.3+((time[0]%12)/12));
        }else{
            darkness.setOpacity(0.7-((time[0]/12)));
        }
    }

    private void updateStudentsLabel(){
        studentslabel.setText("Studenten: "+StudentNumber+"/"+StudentNumberStart);
    }

    public void killStudent(Student s){
        if(s!=null) {
            StudentNumber--;
            updateStudentsLabel();
            s.die();
        }
    }

    private String stockTime(String part){
        if(part.length()!=2){
            part = "0"+part;
        }
        return part;
    }

    private void configandstartSim(){
        initGame();
        m_PrimaryStage.setScene(getScene(MainGameSceneName));
        m_Simulation.start();
    }

    private void initGame(){
        createLocations();
        createStudents(StudentCNT);
        m_Simulation = new Simulation(this,m_students, m_locations);
        getConstants();
        toppane.toFront();
        klausurenpane.setVisible(false);
    }

    private void createStudents(int cnt){
        //create new list
        m_students = new LinkedList<>();
        Circle c;
        Student s;
        ImageView img;
        if(cnt<MAXStudentCNT) {
            for (int i = 0; i < cnt; i++) {
                //create new student object
                s = new Student(i);

                //get circle object
                c = (Circle) getScene(MainGameSceneName).lookup("#student" + i);

                //get death object
                img = (ImageView) getScene(MainGameSceneName).lookup("#death" + i);

                //add circle to student
                s.setCircle(c);

                //add circle to student
                s.setDeathImg(img);

                //setAttributes
                for(int p=0;p<SimElement.ATTR_COUNT;p++){
                    s.setAttributes(p,Math.random()*100);
                }

                //adjust attributes according to reference attributes
                double dif;
                for(int p=0;p<SimElement.ATTR_COUNT;p++){
                    dif=referenceattributes[p]-s.getAttribute(p);
                    s.setAttributes(p,s.getAttribute(p)+dif*Math.random()*adjustingtoreference);
                }

                //add student to list
                m_students.add(s);
            }
        }
        if(cnt<MAXStudentCNT){
            for (int i=cnt;i<MAXStudentCNT;i++){
                c = (Circle)getScene(MainGameSceneName).lookup("#student"+i);
                c.setVisible(false);
            }
        }
        updateStudentsLabel();
    }

    private void createLocations(){
        //create new list
        m_locations = new LinkedList<>();
        Location l;

        for (int i=0;i<Location.names.length;i++){
            //create new location object
            l = new Location(i);

            //setAttributes
            l.setName(Location.names[i]);
            switch(l.getName()){
                case "Universität":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#uni"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#lectureLabel"));
                    l.setAttributes(SimElement.ALCOHOL,00);
                    l.setAttributes(SimElement.LEADERSHIP,100);
                    l.setAttributes(SimElement.LEARNING,100);
                    l.setAttributes(SimElement.PARTY,0);
                    l.setAttributes(SimElement.TEAM,0);
                    l.setPosition(200,150);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Vorlesungsbeginn!",8,11));
                    l.getTimeline().addEvent(new TimelineEvent("Vorlesungsbeginn!",13,16));
                    break;
                case "Disco":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#disco"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#partyLabel"));
                    l.setAttributes(SimElement.ALCOHOL,100);
                    l.setAttributes(SimElement.LEADERSHIP,0);
                    l.setAttributes(SimElement.LEARNING,0);
                    l.setAttributes(SimElement.PARTY,100);
                    l.setAttributes(SimElement.TEAM,0);
                    l.setPosition(450,550);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Party!",21,5));
                    break;
                case  "Bibliothek":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#bib"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#learningLabel"));
                    l.setAttributes(SimElement.ALCOHOL,0);
                    l.setAttributes(SimElement.LEADERSHIP,00);
                    l.setAttributes(SimElement.LEARNING,100);
                    l.setAttributes(SimElement.PARTY,0);
                    l.setAttributes(SimElement.TEAM,100);
                    l.setPosition(860,150);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Endlich lernen!",15,20));
                    break;
                case "Zuhause":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#home"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#sleepingLabel"));
                    l.setAttributes(SimElement.ALCOHOL,100);
                    l.setAttributes(SimElement.LEADERSHIP,0);
                    l.setAttributes(SimElement.LEARNING,100);
                    l.setAttributes(SimElement.PARTY,0);
                    l.setAttributes(SimElement.TEAM,0);
                    l.setPosition(1030,600);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Schlafenszeit!",22,6));
                    break;
            }

            //add student to list
            m_locations.add(l);
        }
    }

    //load all scenes from the view folder
    private void loadScenes() {
        String path = "";
        try {
            path = new File(".").getCanonicalPath();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        List<String> files = getFilesOsDependent(path);
        fillScenesMap(files);
    }

    //to fix issues between the way linux stores files and the way windows does
    private List<String> getFilesOsDependent(final String path) {
        File folder;
        if (isWindows()) {
            folder = new File(path + "\\src\\view");
        }
        else {
            folder = new File(path + "/src/view");
        }
        return getAllFilesOfFolder(folder);
    }

    //check if the os is windows
    private boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    private String getOsName() {
        if (m_OS == null) {
            m_OS = System.getProperty("os.name");
        }
        return m_OS;
    }

    //load all files from the specified folder
    private List<String> getAllFilesOfFolder(final File folder) {
        List<String> files = new LinkedList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory() && fileEntry.getName().matches("(.*)fxml")) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

    //Fill the different levels with objects etc.
    private void fillScenesMap(List<String> files) {
        String name;
        for (String filename : files) {
            try {
                name = filename.split("\\.")[0];
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("view/" + filename)));
                m_ScenesMap.put(name, scene);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //switch to the scene with the title (name)
    private boolean setScene(String name) {
        Scene scene = getScene(name);
        if (scene == null) {
            return false;
        }
        m_PrimaryStage.setScene(scene);
        return true;
    }

    public Scene getScene(String name) { //unsauber!!!
        return m_ScenesMap.get(name);
    }

    private Stage getPrimaryStage() {
        return m_PrimaryStage;
    }

    @Override
    public void stop() {}

    public static void main(String[] args) {
        launch(args);
    }

    public List<Student> getStudents() {
        return m_students;
    }

    public List<Location> getLocations() {
        return m_locations;
    }

    public void setsemesterprogress(double p) {
        semesterprogress.setProgress(p);
    }

    public String getMainGameSceneName() {
        return MainGameSceneName;
    }

    public void handlesemesterend() {
        handlepause(m_pausebutton);
        klausurenpane.setVisible(false);
        semestercnt++;
        semestercntLabel.setText(semestercnt+". Semester");
    }

    public void handleKlausuren(){
        klausurenpane.setVisible(true);
    }

    public void handlesimulationend() {
        handlesemesterend();
        showreport();
    }

    private void calcreport(){

    };

    public void showreport(){
        calcreport();
        m_PrimaryStage.setScene(getScene("report"));
    }
}
