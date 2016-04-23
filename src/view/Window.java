package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Window {
	
private BorderPane root;
	
	public Window(String title, boolean topButtons, boolean leftButtons, String fxmlRessource) {
		root = new BorderPane();

		try {
			GridPane center = new GridPane();
			center = (GridPane)FXMLLoader.load(getClass().getResource(fxmlRessource));
			center.getStyleClass().add("center");
			center.setPadding(new Insets(10, 10, 10, 10));
			center.setVgap(4);
			center.setHgap(4);
			center.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
			
			ImageView img = new ImageView("bg.png");
			img.fitWidthProperty().bind(center.widthProperty()); //muss so eingebaut werden, dass das bild an richtiger Stelle (unter Timeline) erscheint.
			
			root.setCenter(center);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
