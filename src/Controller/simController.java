package Controller;

import Dhimulate.Dhimulate;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Created by mlg on 24.04.2016.
 */

public class simController implements Initializable {
    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView bib;

    @FXML
    private ImageView disco;

    @FXML
    private ImageView uni;

    @FXML
    private ImageView home;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        Circle c;
        ImageView image;
        for (int i = 0; i < Dhimulate.MaxStudentCount; i++) {
            //create new circle object
            c = new Circle();
            c.setLayoutX(Math.random() * 1280);
            c.setLayoutY(50 + Math.random() * 700);
            c.setRadius(4.0f);
            c.setId("student" + Integer.toString(i));
            c.setFill(Paint.valueOf("lightblue"));
            pane.getChildren().add(c);

            image = new ImageView("view/death.png");
            image.setScaleX(0.1);
            image.setScaleY(0.1);
            image.setVisible(false);
            image.setId("death" + Integer.toString(i));
            pane.getChildren().add(image);
        }
    }
}