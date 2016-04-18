package Dhimulate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.Simulation;
import sun.plugin2.message.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mlg on 18.04.2016.
 */
public class Dhimulate extends Application {
    private static final Map<String, Scene> m_ScenesMap = new HashMap<>();
    private static Stage m_PrimaryStage;
    private static Simulation m_Simulation;
    private String m_OS;

    @Override
    public void start(Stage primaryStage) throws Exception {
        m_PrimaryStage = primaryStage;


        loadScenes();
        primaryStage.setScene(getScene("MainMenu"));
        primaryStage.setTitle("Magikarp DHBW Simulation");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    //load all scenes from the view folder
    private void loadScenes() {
        String path = "";
        try {
            path = new File(".").getCanonicalPath();
            System.out.println(path);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        List<String> files = getFilesOsDependent(path);
        fillScenesMap(files);
    }
    //to fix issues between the way linux stores files and the way windows does
    private List<String> getFilesOsDependent(final String path) {
        if (isWindows()) {
            return getAllFilesOfFolder(new File(path + "\\Quellcode\\src\\sample\\view"));
        }
        else {
            return getAllFilesOfFolder(new File(path + "/src/sample/view"));
        }
    }
    //check if the os is windows
    private  boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    private  String getOsName() {
        if (m_OS == null) {
            m_OS = System.getProperty("os.name");
        }
        return m_OS;
    }
    //load all files from the specified folder
    private List<String> getAllFilesOfFolder(final File folder) {
        List<String> files = new LinkedList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
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
                m_ScenesMap.put(name, loadSceneFromFXML(filename));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //load a scene from XML
    private Scene loadSceneFromFXML(String name) throws IOException {
        return new Scene(FXMLLoader.load(getClass().getResource("view/" + name)));
    }
    //switch to the scene with the title (name)
    public static boolean setScene(String name) {
        Scene scene = getScene(name);
        if (scene == null) {
            return false;
        }
        m_PrimaryStage.setScene(scene);
        return true;
    }

    public static Scene getScene(String name) { //unsauber!!!
        return m_ScenesMap.get(name);
    }

    public static void setScene(Scene scene) {
        m_PrimaryStage.setScene(scene);
    }

    public static void mapScene(String name, Scene scene) {
        m_ScenesMap.put(name, scene);
    }

    public static void killScene(String name) {
        m_ScenesMap.remove(name);
    }

    public static Stage getPrimaryStage() {
        return m_PrimaryStage;
    }

    @Override
    public void stop() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
