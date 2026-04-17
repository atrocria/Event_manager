package ui;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


// stage is the window, scene is the current page;
public class App extends Application {
    public void start(Stage stage) throws Exception {
        // Set theme BEFORE loading the FXML
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // Load Scene Builder file
        Parent root = FXMLLoader.load(getClass().getResource("main_view.fxml"));

        // removes the ugly top bar
        stage.initStyle(StageStyle.TRANSPARENT); 
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Makes the edges smooth
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
