package main;

import java.io.IOException;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    public static final int                MaxStudentCount = 200;
    private final       Map<String, Scene> m_ScenesMap     = new HashMap<>();
    private Stage      m_PrimaryStage;
    private SimTimer   m_Timer;
    private Simulation m_Simulation;
    private Label      timeLabel, studentsLabel;
    private Rectangle      darkness;
    private List<Student>  m_students;
    private List<Location> m_locations;
    private int            studentStartCount;
    private int            currentStudentCount;
    private final double[] referenceAttributes  = new double[SimElement.ATTR_COUNT];
    private final double[] startAttributes      = new double[SimElement.ATTR_COUNT];
    private final double[] currentAttributes    = new double[SimElement.ATTR_COUNT];
    private final double   adjustingToReference = 0.5;
    private       boolean  semesterEnded        = false;
    private boolean     saved;
    private ProgressBar semesterProgressBar;
    private Button      m_pauseButton;
    private Pane        klausurenPane;
    private Label       semesterCntLabel;
    private TitledPane  intermediateResults;
    private ProgressBar studentenBar, teamBar, partyBar, lernenBar, fuhrungBar, alkoholBar;
    private ProgressBar studentenBarv, teamBarv, partyBarv, lernenBarv, fuhrungBarv, alkoholBarv;
    private ProgressBar studentenBarn, teamBarn, partyBarn, lernenBarn, fuhrungBarn, alkoholBarn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        m_PrimaryStage = primaryStage;
        loadScenes();
        primaryStage.setScene(getScene("config"));

        primaryStage.setTitle("Magikarp Dhimulate");
        primaryStage.setResizable(true);
        primaryStage.show();

        lookupNodes();

        ((Button) getScene("config").lookup("#startButton")).setOnAction(event -> {
            final Scene configScene = getScene("config");
            referenceAttributes[SimElement.ALCOHOL] = ((Slider) configScene.lookup("#alcSlider")).getValue();
            referenceAttributes[SimElement.PARTY] = ((Slider) configScene.lookup("#partySlider")).getValue();
            referenceAttributes[SimElement.LEADERSHIP] = ((Slider) configScene.lookup("#leaderSlider")).getValue();
            referenceAttributes[SimElement.TEAM] = ((Slider) configScene.lookup("#teamSlider")).getValue();
            referenceAttributes[SimElement.LEARNING] = ((Slider) configScene.lookup("#learnSlider")).getValue();
            studentStartCount = (int) ((Slider) configScene.lookup("#countSlider")).getValue();
            currentStudentCount = studentStartCount;
            configAndStartSim();
        });

        ((Button) getScene("sim3").lookup("#pauseButton")).setOnAction(event -> handlePause((Button) event.getSource()));

        ((Button) getScene("report2").lookup("#saveBtn")).setOnAction(event -> {
            if (!saved) {
                save();
                saved = true;
            }
        });
        ((Button) getScene("report2").lookup("#closeBtn")).setOnAction(event -> m_PrimaryStage.close());
        ((Button) getScene("report2").lookup("#restartBtn")).setOnAction(event -> {
            semesterCntLabel.setText("1. Sem.");
            primaryStage.setScene(getScene("config"));
        });
    }

    //load all scenes from the view folder
    private void loadScenes() {
        final List<String> files = new LinkedList<>();
        files.add("config.fxml");
        files.add("sim3.fxml");
        files.add("report2.fxml");
        fillScenesMap(files);
    }

    private Scene getScene(String name) {
        return m_ScenesMap.get(name);
    }

    private void configAndStartSim() {
        initGame();

        klausurenPane.toFront();
        intermediateResults.toFront();
        lernenBar.toFront();
        studentenBar.toFront();
        partyBar.toFront();
        alkoholBar.toFront();
        fuhrungBar.toFront();
        teamBar.toFront();

        final Scene mainScene = getScene("sim3");
        m_Timer = new SimTimer(m_Simulation);
        m_PrimaryStage.setScene(mainScene);
        toggleStageIfMaximized();
        m_Timer.start();
    }

    private void toggleStageIfMaximized() {
        if (m_PrimaryStage.isMaximized()) {
            m_PrimaryStage.setMaximized(false);
            m_PrimaryStage.setMaximized(true);
        }
    }

    private void handlePause(Button b) {
        if (m_Timer.isRunning()) {
            m_Timer.stop();
            b.setText("Weiter");
        }
        else {
            if (semesterEnded) {
                startNextSemester();
            }
            m_Timer.start();
            b.setText("Pause");
        }
    }

    private void save() {
        final double[] before = new double[SimElement.ATTR_COUNT + 1];
        final double[] after = new double[SimElement.ATTR_COUNT + 1];
        for (int i = 0; i < SimElement.ATTR_COUNT; ++i) {
            before[i + 1] = startAttributes[i];
            after[i + 1] = currentAttributes[i];
        }
        before[0] = studentStartCount;
        after[0] = currentStudentCount;
        saved = true;
        Save.save(before, after);
    }

    private void lookupNodes() {
        final Scene mainScene = getScene("sim3");
        final Scene reportScene = getScene("report2");

        timeLabel = ((Label) mainScene.lookup("#timeLabel"));
        studentsLabel = ((Label) mainScene.lookup("#studentenLabel"));
        darkness = ((Rectangle) mainScene.lookup("#darkness"));
        semesterProgressBar = ((ProgressBar) mainScene.lookup("#semesterprogress"));
        m_pauseButton = (Button) mainScene.lookup("#pauseButton");
        klausurenPane = (Pane) mainScene.lookup("#klausurenpane");
        intermediateResults = (TitledPane) mainScene.lookup("#semesterbericht");
        semesterCntLabel = ((Label) mainScene.lookup("#semestercnt"));

        studentenBar = ((ProgressBar) mainScene.lookup("#studentenBar"));
        teamBar = ((ProgressBar) mainScene.lookup("#teamBar"));
        partyBar = ((ProgressBar) mainScene.lookup("#partyBar"));
        lernenBar = ((ProgressBar) mainScene.lookup("#lernenBar"));
        fuhrungBar = ((ProgressBar) mainScene.lookup("#fuhrungBar"));
        alkoholBar = ((ProgressBar) mainScene.lookup("#alkoholBar"));

        studentenBarv = ((ProgressBar) reportScene.lookup("#studentenBarv"));
        teamBarv = ((ProgressBar) reportScene.lookup("#teamBarv"));
        partyBarv = ((ProgressBar) reportScene.lookup("#partyBarv"));
        lernenBarv = ((ProgressBar) reportScene.lookup("#lernenBarv"));
        fuhrungBarv = ((ProgressBar) reportScene.lookup("#fuhrungBarv"));
        alkoholBarv = ((ProgressBar) reportScene.lookup("#alkoholBarv"));

        studentenBarn = ((ProgressBar) reportScene.lookup("#studentenBarn"));
        teamBarn = ((ProgressBar) reportScene.lookup("#teamBarn"));
        partyBarn = ((ProgressBar) reportScene.lookup("#partyBarn"));
        lernenBarn = ((ProgressBar) reportScene.lookup("#lernenBarn"));
        fuhrungBarn = ((ProgressBar) reportScene.lookup("#fuhrungBarn"));
        alkoholBarn = ((ProgressBar) reportScene.lookup("#alkoholBarn"));
    }

    //Fill the different levels with objects etc.
    private void fillScenesMap(List<String> files) {
        String name;
        for (final String filename : files) {
            try {
                name = filename.split("\\.")[0];
                final Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("view/" + filename)));
                m_ScenesMap.put(name, scene);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initGame() {
        createLocations();
        createStudents(studentStartCount);
        m_Simulation = new Simulation(m_students, m_locations);
        m_Simulation.minutePassedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                final int day = m_Simulation.getDay();
                final double[] time = m_Simulation.getTime();
                if (day <= m_Simulation.daysPerSemester) {
                    updateTime(day, time);
                }
            }
        });
        m_Simulation.semesterProgressProperty().addListener(((observable, oldValue, newValue) -> {
            final double semesterProgress = newValue.doubleValue();
            semesterProgressBar.setProgress(semesterProgress);
            if (semesterProgress >= 0.9) {
                klausurenPane.setVisible(true);
            }
        }));
        m_Simulation.dayProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.intValue() > m_Simulation.daysPerSemester) {
                if (m_Simulation.getSemesterCount() == 6) {
                    handleSimulationEnd();
                }
                else {
                    handleSemesterEnd();
                }
            }
        }));

        getConstants();
        showIntermediateResults(false);
        calcAttributesTotal(startAttributes);
    }

    private void startNextSemester() {
        m_Simulation.setDay(1);
        semesterCntLabel.setText(m_Simulation.getSemesterCount() + ". Sem.");
        m_students.stream().filter(student -> !student.isDisabled()).forEach(student -> {
            if (student.getDeathAnimCnt() > 0) {
                student.vanish();
            }
            student.setPosition(Math.random() * 1280, 53 + Math.random() * 720);
            student.setHealth(100);
        });
        showIntermediateResults(false);
        semesterEnded = false;
    }

    private void createLocations() {
        //create new list
        m_locations = new LinkedList<>();
        Location l;
        final String[] names = {"Disco", "Universität", "Bibliothek", "Zuhause"};

        for (int i = 0; i < names.length; i++) {
            //create new location object
            l = new Location(i, names[i]);

            switch (l.getName()) {
                case "Universität":
                    l.setImage((ImageView) getScene("sim3").lookup("#uni"));
                    l.setNotificationLabel((Label) getScene("sim3").lookup("#lectureLabel"));
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
                    l.setImage((ImageView) getScene("sim3").lookup("#disco"));
                    l.setNotificationLabel((Label) getScene("sim3").lookup("#partyLabel"));
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
                    l.setImage((ImageView) getScene("sim3").lookup("#bib"));
                    l.setNotificationLabel((Label) getScene("sim3").lookup("#learningLabel"));
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
                    l.setImage((ImageView) getScene("sim3").lookup("#home"));
                    l.setNotificationLabel((Label) getScene("sim3").lookup("#sleepingLabel"));
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
        ImageView img;
        for (int i = 0; i < cnt; i++) {
            //create new student object
            final Student s = new Student(i);
            s.failedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    killStudent(s);
                }
            });

            //get circle object
            c = (Circle) getScene("sim3").lookup("#student" + i);

            //get death object
            img = (ImageView) getScene("sim3").lookup("#death" + i);

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
                dif = referenceAttributes[p] - s.getAttribute(p);
                s.setAttributes(p, s.getAttribute(p) + dif * Math.random() * adjustingToReference);
            }

            //add student to list
            m_students.add(s);
        }
        for (int i = cnt; i < MaxStudentCount; i++) {
            c = (Circle) getScene("sim3").lookup("#student" + i);
            c.setVisible(false);
        }
        updateStudentsLabel();
    }

    private void getConstants() {
        m_Simulation.daysPerSemester = (int) ((Slider) getScene("config").lookup("#onesemesterisxdaysSlider")).getValue();
        m_Simulation.healthDecreaseOnDanger = ((Slider) getScene("config").lookup("#healthdecreaseondangerSlider")).getValue();
        m_Simulation.adjustAttributesInfluenceByStudents = ((Slider) getScene("config").lookup("#adjustattributesInfluenceByStudentsSlider")).getValue();
        m_Simulation.adjustAttributesInfluenceByLocations = ((Slider) getScene("config").lookup("#adjustattributesInfluenceByLocationsSlider")).getValue();
        m_Simulation.directionInfluenceByStudents = ((Slider) getScene("config").lookup("#directionInfluenceByStudentsSlider")).getValue();
        m_Simulation.directionInfluenceByLocations = ((Slider) getScene("config").lookup("#directionInfluenceByLocationsSlider")).getValue();
        m_Simulation.timelineInfluence = ((Slider) getScene("config").lookup("#timelineInfluenceSlider")).getValue();
        m_Simulation.distanceStudentInfluence = ((Slider) getScene("config").lookup("#distanceStudentInfluenceSlider")).getValue();
        m_Simulation.distanceLocationInfluence = ((Slider) getScene("config").lookup("#distanceLocationInfluenceSlider")).getValue();
    }

    private void showIntermediateResults(boolean b) {
        klausurenPane.setVisible(b);
        intermediateResults.setVisible(b);
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
        for (final Student student : m_students) {
            for (int i = 0; i < Student.ATTR_COUNT; i++) {
                a[i] += student.getAttribute(i);
            }
        }
        for (int i = 0; i < Student.ATTR_COUNT; i++) {
            a[i] /= m_students.size();
        }
    }

    private void updateStudentsLabel() {
        studentsLabel.setText("Studenten: " + currentStudentCount + "/" + studentStartCount);
    }

    private void updateTime(int day, double[] time) {
        timeLabel.setText(day + ". Tag" + "  " + padTime(time[0]) + ":" + padTime(time[1]) + "Uhr");
        if (time[0] > 12) {
            darkness.setOpacity(-0.3 + ((time[0] % 12) / 12));
        }
        else {
            darkness.setOpacity(0.7 - ((time[0] / 12)));
        }
    }

    private String padTime(double part) {
        final int time = (int) part;
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    private void killStudent(Student s) {
        if (s != null) {
            currentStudentCount--;
            updateStudentsLabel();
            s.die();
        }
        if (currentStudentCount == 0) {
            handleSimulationEnd();
        }
    }

    private void handleSimulationEnd() {
        handlePause(m_pauseButton);
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

        saved = false;
        m_PrimaryStage.setScene(getScene("report2"));
        toggleStageIfMaximized();
    }

    private void handleSemesterEnd() {
        handlePause(m_pauseButton);
        klausurenPane.setVisible(false);
        calcAttributesTotal(currentAttributes);

        studentenBar.setProgress((double) currentStudentCount / (double) studentStartCount);
        teamBar.setProgress(currentAttributes[SimElement.TEAM] / 100);
        partyBar.setProgress(currentAttributes[SimElement.PARTY] / 100);
        lernenBar.setProgress(currentAttributes[SimElement.LEARNING] / 100);
        fuhrungBar.setProgress(currentAttributes[SimElement.LEADERSHIP] / 100);
        alkoholBar.setProgress(currentAttributes[SimElement.ALCOHOL] / 100);

        showIntermediateResults(true);
        semesterEnded = true;
        klausurenPane.setVisible(false);
    }
}
