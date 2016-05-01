package Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;

/**
 * Created by mlg on 24.04.2016.
 */

public class configController implements Initializable {
    @FXML
    private Slider countSlider;

    @FXML
    private Slider teamSlider;

    @FXML
    private Slider learnSlider;

    @FXML
    private Slider partySlider;

    @FXML
    private Slider alcSlider;


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

    }

    @FXML
    protected void startSim(ActionEvent event) {

    }
}