package Dhimulate;

import java.io.File;
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
    private static final Map<String, Scene> m_ScenesMap   = new HashMap<>();
    public static        int                MAXStudentCNT = 200;
    private static Stage      m_PrimaryStage;
    private static Simulation m_Simulation;
    private static Label      timelabel;
    private static Label      studentslabel;
    private static Label      semesterlabel;
    private static Rectangle  darkness;
    private        String     m_OS;
    private int      StudentCNT          = 100;
    private int      StudentNumberStart  = 0;
    private int      StudentNumber       = 0;
    private double[] referenceAttributes = new double[SimElement.ATTR_COUNT];
    private List<Student>  m_students;
    private List<Location> m_locations;
    private String MainGameSceneName    = "sim3";
    private double adjustingToReference = 0.5;
    private StackPane   topPane;
    private ProgressBar semesterProgress;
    private Button      m_pauseButton;
    private Pane        klausurenPane;
    private Label       semesterCntLabel;
    private int semestercnt = 1;

    public static void main(String[] args) {
        launch(args);
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
            referenceAttributes[SimElement.ALCOHOL] = ((Slider) getScene("config").lookup("#alcSlider")).getValue();

            referenceAttributes[SimElement.PARTY] = ((Slider) getScene("config").lookup("#partySlider")).getValue();
            referenceAttributes[SimElement.LEADERSHIP] = ((Slider) getScene("config").lookup("#leaderSlider")).getValue();
            referenceAttributes[SimElement.TEAM] = ((Slider) getScene("config").lookup("#teamSlider")).getValue();
            referenceAttributes[SimElement.LEARNING] = ((Slider) getScene("config").lookup("#learnSlider")).getValue();
            StudentCNT = (int) ((((Slider) getScene("config").lookup("#countSlider")).getValue() / 100) * MAXStudentCNT);
            StudentNumberStart = StudentCNT;
            StudentNumber = StudentCNT;
            System.out.println(((Slider) getScene("config").lookup("#countSlider")).getValue());
            configAndStartSim();
        });

        ((Button) getScene(MainGameSceneName).lookup("#pauseButton")).setOnAction(event -> handlePause((Button) event.getSource()));

        timelabel = ((Label) getScene(MainGameSceneName).lookup("#timeLabel"));
        semesterlabel = ((Label) getScene(MainGameSceneName).lookup("#semesterLabel"));
        studentslabel = ((Label) getScene(MainGameSceneName).lookup("#studentenLabel"));
        darkness = ((Rectangle) getScene(MainGameSceneName).lookup("#darkness"));
        semesterProgress = ((ProgressBar) getScene(MainGameSceneName).lookup("#semesterprogress"));
        topPane = ((StackPane) getScene(MainGameSceneName).lookup("#stackpane"));
        m_pauseButton = (Button) getScene(getMainGameSceneName()).lookup("#pauseButton");
        klausurenPane = (Pane) getScene(getMainGameSceneName()).lookup("#klausurenpane");
        semesterCntLabel = ((Label) getScene(MainGameSceneName).lookup("#semestercnt"));
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

    public Scene getScene(String name) { //unsauber!!!
        return m_ScenesMap.get(name);
    }

    private void configAndStartSim() {
        initGame();
        m_PrimaryStage.setScene(getScene(MainGameSceneName));
        m_Simulation.start();
    }

    public void handlePause(Button b) {
        if (m_Simulation.isRunning()) {
            m_Simulation.stop();
            b.setText("Weiter");
        }
        else {
            m_Simulation.start();
            b.setText("Stop");
        }
    }

    public String getMainGameSceneName() {
        return MainGameSceneName;
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
        createStudents(StudentCNT);
        m_Simulation = new Simulation(this, m_students, m_locations);
        getConstants();
        topPane.toFront();
        klausurenPane.setVisible(false);
    }

    //check if the os is windows
    private boolean isWindows() {
        return getOsName().startsWith("Windows");
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
        if (cnt <= MAXStudentCNT) {
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

                //setAttributes at random
                for (int p = 0; p < SimElement.ATTR_COUNT; p++) {
                    s.setAttributes(p, Math.random() * 100);
                }

                //adjust attributes according to reference attributes
                double dif;
                for (int p = 0; p < SimElement.ATTR_COUNT; p++) {
                    dif = referenceAttributes[p] - s.getAttribute(p);
                    s.setAttributes(p, s.getAttribute(p) + dif * Math.random() * adjustingToReference);
                }

                //add student to list
                m_students.add(s);
            }
            for (int i = cnt; i < MAXStudentCNT; i++) {
                c = (Circle) getScene(MainGameSceneName).lookup("#student" + i);
                c.setVisible(false);
            }
        }
        updateStudentsLabel();
    }

    private void getConstants() {
        m_Simulation.onesemesterisxdays = ((Slider) getScene("config").lookup("#onesemesterisxdaysSlider")).getValue();
        m_Simulation.healthdecreaseondanger = ((Slider) getScene("config").lookup("#healthdecreaseondangerSlider")).getValue();
        m_Simulation.adjustattributesInfluenceByStudents = ((Slider) getScene("config").lookup("#adjustattributesInfluenceByStudentsSlider")).getValue();
        m_Simulation.adjustattributesInfluenceByLocations = ((Slider) getScene("config").lookup("#adjustattributesInfluenceByLocationsSlider")).getValue();
        m_Simulation.directionInfluenceByStudents = ((Slider) getScene("config").lookup("#directionInfluenceByStudentsSlider")).getValue();
        m_Simulation.directionInfluenceByLocations = ((Slider) getScene("config").lookup("#directionInfluenceByLocationsSlider")).getValue();
        m_Simulation.timelineInfluence = ((Slider) getScene("config").lookup("#timelineInfluenceSlider")).getValue();
        m_Simulation.distanceStudentInfluence = ((Slider) getScene("config").lookup("#distanceStudentInfluenceSlider")).getValue();
        m_Simulation.distanceLocationInfluence = ((Slider) getScene("config").lookup("#distanceLocationInfluenceSlider")).getValue();
    }

    private String getOsName() {
        if (m_OS == null) {
            m_OS = System.getProperty("os.name");
        }
        return m_OS;
    }

    private void updateStudentsLabel() {
        studentslabel.setText("Studenten: " + StudentNumber + "/" + StudentNumberStart);
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
            StudentNumber--;
            updateStudentsLabel();
            s.die();
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
        semesterProgress.setProgress(p);
    }

    public void handleKlausuren() {
        klausurenPane.setVisible(true);
    }

    public void handleSimulationEnd() {
        handleSemesterEnd();
        showReportScreen();
    }

    public void handleSemesterEnd() {
        handlePause(m_pauseButton);
        klausurenPane.setVisible(false);
        semestercnt++;
        semesterCntLabel.setText(semestercnt + ". Semester");
    }

    private void showReportScreen() {
        calcReport();
        m_PrimaryStage.setScene(getScene("report"));
    }

    private void calcReport() {
        double[] statsAfter = new double[SimElement.ATTR_COUNT + 1];
        statsAfter[0] = StudentNumber;
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            statsAfter[i + 1] = getAvgOfAttribute(i);
        }
        Save.save(referenceAttributes, statsAfter);
    }

    private double getAvgOfAttribute(int i) {
        double sum = 0.0;
        for (Student student : m_students) {
            sum += student.getAttribute(i);
        }
        return sum / m_students.size();
    }
}
