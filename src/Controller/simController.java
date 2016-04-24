package Controller;

import Dhimulate.Dhimulate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mlg on 24.04.2016.
 */

public class simController implements Initializable {
    @FXML
    private AnchorPane pane;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        Circle c;
        for (int i = 0; i<Dhimulate.MAXStudentCNT; i++){
            //create new circle object
            c = new Circle();
            c.setLayoutX(Math.random()*1280);
            c.setLayoutY(50+Math.random()*700);
            c.setRadius(4.0f);
            c.setId("student"+Integer.toString(i));
            c.setFill(Paint.valueOf("white"));
            pane.getChildren().add(c);
        }
    }
}