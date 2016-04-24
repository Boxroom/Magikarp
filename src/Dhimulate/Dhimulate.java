package Dhimulate;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Location;
import model.Student;
import model.Vector2D;
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
    public   static int MAXStudentCNT = 100;
    private    int StudentCNT = 50;
    private List<Student> m_students;
    private List<Location> m_locations;
    private String MainGameSceneName = "sim2";

    @Override
    public void start(Stage primaryStage) throws Exception {
        m_PrimaryStage = primaryStage;
        loadScenes();
        primaryStage.setScene(getScene(MainGameSceneName));
        //Window w = new Window();
        primaryStage.setTitle("Magikarp DHBW Simulation");
        primaryStage.setResizable(false);
        primaryStage.show();

        initGame();
        m_Simulation.start();
    }

    private void initGame(){
        createLocations();
        createStudents(StudentCNT);
        m_Simulation = new Simulation(this);
    }

    private void createStudents(int cnt){
        //create new list
        m_students = new LinkedList<>();
        Circle c;
        Student s;

        for (int i=0;i<cnt;i++){
            //create new student object
            s = new Student(i);

            //get circle object
            c = (Circle)getScene(MainGameSceneName).lookup("#student"+i);

            //add circle to student
            s.setCircle(c);

            //setAttributes




            //add student to list
            m_students.add(s);
        }
        for (int i=cnt;i<MAXStudentCNT;i++){
            c = (Circle)getScene(MainGameSceneName).lookup("#student"+i);
            c.setVisible(false);
        }
    }

    private void createLocations(){
        //create new list
        m_locations = new LinkedList<>();
        Location l;

        for (int i=0;i<4;i++){
            //create new location object
            l = new Location(i);

            //setAttributes
            l.setName(Location.names[i]);
            switch(l.getName()){
                case "Universität":
                    l.setPosition(797,345);


                    break;
                case "Disco":
                    l.setPosition(268,824);


                    break;
                case  "Bibliothek":
                    l.setPosition(1521,870);


                    break;
                case "Zuhause":
                    l.setPosition(1487,167);


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

    private Scene getScene(String name) { //unsauber!!!
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
}
