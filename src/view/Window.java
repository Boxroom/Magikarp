package view;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Window {

    private final BorderPane root;

    public Window(String title, boolean topButtons, boolean leftButtons, String fxmlRessource) {
        root = new BorderPane();

        try {
            final GridPane center;
            center = (GridPane) FXMLLoader.load(getClass().getResource(fxmlRessource));
            center.getStyleClass().add("center");
            center.setPadding(new Insets(10, 10, 10, 10));
            center.setVgap(4);
            center.setHgap(4);
            center.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);

            final ImageView img = new ImageView("bg.png");
            img.fitWidthProperty().bind(center.widthProperty());

            root.setCenter(center);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
