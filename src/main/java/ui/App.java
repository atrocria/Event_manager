package ui; // Make sure this is at the top!

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Set the Theme
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // 2. Load the FXML (Now that it's in the same folder)
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));

        // 3. Setup the Scene
        Scene scene = new Scene(root);
        
        // This makes the window edges smooth and the background blendable
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);

        //! only the anchorpane is set to see through rn, make top and side only see through and glass like
        // root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-background-radius: 10;");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}