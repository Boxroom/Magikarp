package Dhimulate;

import java.io.IOException;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.*;
import save.Save;
import simulation.Simulation;

/**
 * @author Jendrik, nilsw
 */
public class Dhimulate extends Application {
    public static        int                MaxStudentCount = 200;
    private static final Map<String, Scene> m_ScenesMap     = new HashMap<>();
    private static Stage      m_PrimaryStage;
    private static Simulation m_Simulation;
    private static SimTimer   m_Timer;
    private static Label      timelabel;
    private static Label      studentslabel;
    private static Label      semesterlabel;
    private static Rectangle  darkness;
    private        String     m_OS;
    private int      studentStartCount   = 100;
    private int      currentStudentCount = 0;
    private double[] referenceattributes = new double[SimElement.ATTR_COUNT];
    private List<Student>  m_students;
    private List<Location> m_locations;
    private String MainGameSceneName    = "sim3";
    private double adjustingToReference = 0.5;
    private        StackPane   toppane;
    private        ProgressBar semesterprogress;
    private        Button      m_pausebutton;
    private        Pane        klausurenpane;
    private        Label       semestercntLabel;
    private int semesterCount = 1;
    private TitledPane  zwischenstand;
    private ProgressBar studentenBar;
    private ProgressBar teamBar;
    private ProgressBar partyBar;
    private ProgressBar lernenBar;
    private ProgressBar fuhrungBar;
    private ProgressBar alkoholBar;
    private double[] startAttributes   = new double[SimElement.ATTR_COUNT];
    private double[] currentAttributes = new double[SimElement.ATTR_COUNT];
    private boolean  semesterEnded     = false;
    private ProgressBar studentenBarv;
    private ProgressBar teamBarv;
    private ProgressBar partyBarv;
    private ProgressBar lernenBarv;
    private ProgressBar fuhrungBarv;
    private ProgressBar alkoholBarv;
    private ProgressBar studentenBarn;
    private ProgressBar teamBarn;
    private ProgressBar partyBarn;
    private ProgressBar lernenBarn;
    private ProgressBar fuhrungBarn;
    private ProgressBar alkoholBarn;

    public static void main(String[] args) {
        launch(args);
    }

    public int getSemesterCount() {
        return semesterCount;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        m_PrimaryStage = primaryStage;
        loadScenes();
        primaryStage.setScene(getScene("config"));

        primaryStage.setTitle("Magikarp DHBW Simulation");
        primaryStage.setResizable(false);
        primaryStage.show();


        ((Button) getScene("config").lookup("#startButton")).setOnAction(event -> {
            referenceattributes[SimElement.ALCOHOL] = ((Slider) getScene("config").lookup("#alcSlider")).getValue();

            referenceattributes[SimElement.PARTY] = ((Slider) getScene("config").lookup("#partySlider")).getValue();
            referenceattributes[SimElement.LEADERSHIP] = ((Slider) getScene("config").lookup("#leaderSlider")).getValue();
            referenceattributes[SimElement.TEAM] = ((Slider) getScene("config").lookup("#teamSlider")).getValue();
            referenceattributes[SimElement.LEARNING] = ((Slider) getScene("config").lookup("#learnSlider")).getValue();
            studentStartCount = (int) ((Slider) getScene("config").lookup("#countSlider")).getValue();
            currentStudentCount = studentStartCount;
            configAndStartSim();
        });

        ((Button) getScene(MainGameSceneName).lookup("#pauseButton")).setOnAction(event -> handlePause((Button) event.getSource()));

        ((Button) getScene("report2").lookup("#save")).setOnAction(event -> {
            save();
            m_PrimaryStage.close();
        });

        timelabel = ((Label) getScene(MainGameSceneName).lookup("#timeLabel"));
        semesterlabel = ((Label) getScene(MainGameSceneName).lookup("#semesterLabel"));
        studentslabel = ((Label) getScene(MainGameSceneName).lookup("#studentenLabel"));
        darkness = ((Rectangle) getScene(MainGameSceneName).lookup("#darkness"));
        semesterprogress = ((ProgressBar) getScene(MainGameSceneName).lookup("#semesterprogress"));
        toppane = ((StackPane) getScene(MainGameSceneName).lookup("#stackpane"));
        m_pausebutton = (Button) getScene(getMainGameSceneName()).lookup("#pauseButton");
        klausurenpane = (Pane) getScene(getMainGameSceneName()).lookup("#klausurenpane");
        zwischenstand = (TitledPane) getScene(getMainGameSceneName()).lookup("#semesterbericht");
        semestercntLabel = ((Label) getScene(MainGameSceneName).lookup("#semestercnt"));

        studentenBar = ((ProgressBar) getScene(MainGameSceneName).lookup("#studentenBar"));
        teamBar = ((ProgressBar) getScene(MainGameSceneName).lookup("#teamBar"));
        partyBar = ((ProgressBar) getScene(MainGameSceneName).lookup("#partyBar"));
        lernenBar = ((ProgressBar) getScene(MainGameSceneName).lookup("#lernenBar"));
        fuhrungBar = ((ProgressBar) getScene(MainGameSceneName).lookup("#fuhrungBar"));
        alkoholBar = ((ProgressBar) getScene(MainGameSceneName).lookup("#alkoholBar"));

        studentenBarv = ((ProgressBar) getScene("report2").lookup("#studentenBarv"));
        teamBarv = ((ProgressBar) getScene("report2").lookup("#teamBarv"));
        partyBarv = ((ProgressBar) getScene("report2").lookup("#partyBarv"));
        lernenBarv = ((ProgressBar) getScene("report2").lookup("#lernenBarv"));
        fuhrungBarv = ((ProgressBar) getScene("report2").lookup("#fuhrungBarv"));
        alkoholBarv = ((ProgressBar) getScene("report2").lookup("#alkoholBarv"));

        studentenBarn = ((ProgressBar) getScene("report2").lookup("#studentenBarn"));
        teamBarn = ((ProgressBar) getScene("report2").lookup("#teamBarn"));
        partyBarn = ((ProgressBar) getScene("report2").lookup("#partyBarn"));
        lernenBarn = ((ProgressBar) getScene("report2").lookup("#lernenBarn"));
        fuhrungBarn = ((ProgressBar) getScene("report2").lookup("#fuhrungBarn"));
        alkoholBarn = ((ProgressBar) getScene("report2").lookup("#alkoholBarn"));
    }

    //load all scenes from the view folder
    private void loadScenes() {
        List<String> files = new LinkedList<>();
        files.add("config.fxml");
        files.add("report.fxml");
        files.add("report2.fxml");
        files.add("sim.fxml");
        files.add("sim2.fxml");
        files.add("sim3.fxml");
        fillScenesMap(files);
    }

    public Scene getScene(String name) { //unsauber!!!
        return m_ScenesMap.get(name);
    }

    private void configAndStartSim() {
        initGame();
        m_Timer = new SimTimer(m_Simulation);
        m_PrimaryStage.setScene(getScene(MainGameSceneName));
        klausurenpane.toFront();
        zwischenstand.toFront();

        lernenBar.toFront();
        studentenBar.toFront();
        partyBar.toFront();
        alkoholBar.toFront();
        fuhrungBar.toFront();
        teamBar.toFront();

        toppane.toFront();
        m_Timer.start();
    }

    public void handlePause(Button b) {
        if (m_Timer.isRunning()) {
            m_Timer.stop();
            b.setText("Weiter");
        }
        else {
            if (semesterEnded) {
                startNextSemester();
            }
            m_Timer.start();
            b.setText("Stop");
        }
    }

    public void save() {
        double[] before = new double[SimElement.ATTR_COUNT + 1];
        double[] after = new double[SimElement.ATTR_COUNT + 1];
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            before[i + 1] = startAttributes[i];
            after[i + 1] = currentAttributes[i];
        }
        before[0] = studentStartCount;
        after[0] = currentStudentCount;
        Save.save(before, after);
    }

    public String getMainGameSceneName() {
        return MainGameSceneName;
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

    private void initGame() {
        createLocations();
        createStudents(studentStartCount);
        m_Simulation = new Simulation(this, m_students, m_locations);
        getConstants();
        handleZwischenstand(false);
        calcAttributesTotal(startAttributes);
    }

    private void startNextSemester() {
        semesterCount++;
        semestercntLabel.setText(semesterCount + ". Sem.");
        for (Student student : m_students) {
            student.setPosition(Math.random() * 1280, 50 + Math.random() * 700);
            student.setHealth(100);
        }
        handleZwischenstand(false);
        semesterEnded = false;
    }

    private void createLocations() {
        //create new list
        m_locations = new LinkedList<>();
        Location l;

        for (int i = 0; i < Location.names.length; i++) {
            //create new location object
            l = new Location(i);

            //setAttributes
            l.setName(Location.names[i]);
            switch (l.getName()) {
                case "Universität":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#uni"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#lectureLabel"));
                    l.setAttributes(SimElement.ALCOHOL, 0);
                    l.setAttributes(SimElement.LEADERSHIP, 100);
                    l.setAttributes(SimElement.LEARNING, 100);
                    l.setAttributes(SimElement.PARTY, 0);
                    l.setAttributes(SimElement.TEAM, 0);
                    l.setPosition(200, 150);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Vorlesungsbeginn!", 8, 11));
                    l.getTimeline().addEvent(new TimelineEvent("Vorlesungsbeginn!", 13, 16));
                    break;
                case "Disco":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#disco"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#partyLabel"));
                    l.setAttributes(SimElement.ALCOHOL, 100);
                    l.setAttributes(SimElement.LEADERSHIP, 0);
                    l.setAttributes(SimElement.LEARNING, 0);
                    l.setAttributes(SimElement.PARTY, 100);
                    l.setAttributes(SimElement.TEAM, 0);
                    l.setPosition(450, 550);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Party!", 21, 5));
                    break;
                case "Bibliothek":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#bib"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#learningLabel"));
                    l.setAttributes(SimElement.ALCOHOL, 0);
                    l.setAttributes(SimElement.LEADERSHIP, 0);
                    l.setAttributes(SimElement.LEARNING, 100);
                    l.setAttributes(SimElement.PARTY, 0);
                    l.setAttributes(SimElement.TEAM, 100);
                    l.setPosition(860, 150);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Endlich lernen!", 15, 20));
                    break;
                case "Zuhause":
                    l.setImage((ImageView) getScene(MainGameSceneName).lookup("#home"));
                    l.setNotificationlabel((Label) getScene(MainGameSceneName).lookup("#sleepingLabel"));
                    l.setAttributes(SimElement.ALCOHOL, 100);
                    l.setAttributes(SimElement.LEADERSHIP, 0);
                    l.setAttributes(SimElement.LEARNING, 100);
                    l.setAttributes(SimElement.PARTY, 0);
                    l.setAttributes(SimElement.TEAM, 0);
                    l.setPosition(1030, 600);
                    l.calcDanger();
                    l.getTimeline().addEvent(new TimelineEvent("Schlafenszeit!", 22, 6));
                    break;
            }

            //add student to list
            m_locations.add(l);
        }
    }

    private void createStudents(int cnt) {
        //create new list
        m_students = new LinkedList<>();
        Circle c;
        Student s;
        ImageView img;
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
            for (int p = 0; p < SimElement.ATTR_COUNT; p++) {
                s.setAttributes(p, Math.random() * 100);
            }

            //adjust attributes according to reference attributes
            double dif;
            for (int p = 0; p < SimElement.ATTR_COUNT; p++) {
                dif = referenceattributes[p] - s.getAttribute(p);
                s.setAttributes(p, s.getAttribute(p) + dif * Math.random() * adjustingToReference);
            }

            //add student to list
            m_students.add(s);
        }
        for (int i = cnt; i < MaxStudentCount; i++) {
            c = (Circle) getScene(MainGameSceneName).lookup("#student" + i);
            c.setVisible(false);
        }
        updateStudentsLabel();
    }

    private void getConstants() {
        m_Simulation.daysPerSemester = ((Slider) getScene("config").lookup("#onesemesterisxdaysSlider")).getValue();
        m_Simulation.healthdecreaseondanger = ((Slider) getScene("config").lookup("#healthdecreaseondangerSlider")).getValue();
        m_Simulation.adjustattributesInfluenceByStudents = ((Slider) getScene("config").lookup("#adjustattributesInfluenceByStudentsSlider")).getValue();
        m_Simulation.adjustattributesInfluenceByLocations = ((Slider) getScene("config").lookup("#adjustattributesInfluenceByLocationsSlider")).getValue();
        m_Simulation.directionInfluenceByStudents = ((Slider) getScene("config").lookup("#directionInfluenceByStudentsSlider")).getValue();
        m_Simulation.directionInfluenceByLocations = ((Slider) getScene("config").lookup("#directionInfluenceByLocationsSlider")).getValue();
        m_Simulation.timelineInfluence = ((Slider) getScene("config").lookup("#timelineInfluenceSlider")).getValue();
        m_Simulation.distanceStudentInfluence = ((Slider) getScene("config").lookup("#distanceStudentInfluenceSlider")).getValue();
        m_Simulation.distanceLocationInfluence = ((Slider) getScene("config").lookup("#distanceLocationInfluenceSlider")).getValue();
    }

    private void handleZwischenstand(boolean b) {
        klausurenpane.setVisible(b);
        zwischenstand.setVisible(b);
        lernenBar.setVisible(b);
        studentenBar.setVisible(b);
        partyBar.setVisible(b);
        alkoholBar.setVisible(b);
        fuhrungBar.setVisible(b);
        teamBar.setVisible(b);
    }

    private void calcAttributesTotal(double[] a) {
        for (int i = 0; i < Student.ATTR_COUNT; i++) {
            a[i] = 0;
        }
        for (Student student : m_students) {
            for (int i = 0; i < Student.ATTR_COUNT; i++) {
                a[i] += student.getAttribute(i);
            }
        }
        for (int i = 0; i < Student.ATTR_COUNT; i++) {
            a[i] /= m_students.size();
        }
    }

    private void updateStudentsLabel() {
        studentslabel.setText("Studenten: " + currentStudentCount + "/" + studentStartCount);
    }

    @Override
    public void stop() {
    }

    public void updateTime(int day, double[] time) {
        timelabel.setText(day + ". Tag" + "  " + stockTime("" + ((int) time[0])) + ":" + stockTime("" + ((int) time[1]))/*+":"+((int)time[2])*/ + "Uhr");
        if (time[0] > 12) {
            darkness.setOpacity(-0.3 + ((time[0] % 12) / 12));
        }
        else {
            darkness.setOpacity(0.7 - ((time[0] / 12)));
        }
    }

    private String stockTime(String part) {
        if (part.length() != 2) {
            part = "0" + part;
        }
        return part;
    }

    public void killStudent(Student s) {
        if (s != null) {
            currentStudentCount--;
            updateStudentsLabel();
            s.die();
        }
        if (currentStudentCount == 0) {
            handleSimulationEnd();
        }
    }

    public void handleSimulationEnd() {
        handlePause(m_pausebutton);
        calcAttributesTotal(currentAttributes);

        studentenBarv.setProgress((double) studentStartCount / (double) studentStartCount);
        teamBarv.setProgress(startAttributes[SimElement.TEAM] / 100);
        partyBarv.setProgress(startAttributes[SimElement.PARTY] / 100);
        lernenBarv.setProgress(startAttributes[SimElement.LEARNING] / 100);
        fuhrungBarv.setProgress(startAttributes[SimElement.LEADERSHIP] / 100);
        alkoholBarv.setProgress(startAttributes[SimElement.ALCOHOL] / 100);

        studentenBarn.setProgress((double) currentStudentCount / (double) studentStartCount);
        teamBarn.setProgress(currentAttributes[SimElement.TEAM] / 100);
        partyBarn.setProgress(currentAttributes[SimElement.PARTY] / 100);
        lernenBarn.setProgress(currentAttributes[SimElement.LEARNING] / 100);
        fuhrungBarn.setProgress(currentAttributes[SimElement.LEADERSHIP] / 100);
        alkoholBarn.setProgress(currentAttributes[SimElement.ALCOHOL] / 100);

        m_PrimaryStage.setScene(getScene("report2"));
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

    private Stage getPrimaryStage() {
        return m_PrimaryStage;
    }

    public List<Student> getStudents() {
        return m_students;
    }

    public List<Location> getLocations() {
        return m_locations;
    }

    public void setSemesterProgress(double p) {
        semesterprogress.setProgress(p);
    }

    public void handleSemesterEnd() {
        handlePause(m_pausebutton);
        klausurenpane.setVisible(false);
        calcAttributesTotal(currentAttributes);

        studentenBar.setProgress((double) currentStudentCount / (double) studentStartCount);
        teamBar.setProgress(currentAttributes[SimElement.TEAM] / 100);
        partyBar.setProgress(currentAttributes[SimElement.PARTY] / 100);
        lernenBar.setProgress(currentAttributes[SimElement.LEARNING] / 100);
        fuhrungBar.setProgress(currentAttributes[SimElement.LEADERSHIP] / 100);
        alkoholBar.setProgress(currentAttributes[SimElement.ALCOHOL] / 100);

        handleZwischenstand(true);
        semesterEnded = true;
        klausurenpane.setVisible(false);
    }

    public void handleKlausuren() {
        klausurenpane.setVisible(true);
    }
}
