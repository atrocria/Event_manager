package ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.UserSession;

public class ProfileController {
    
    @FXML // logout button clicked
    private void handleLogout(ActionEvent event) {
        UserSession.getInstance().logout();

        // Wipe the window and show Login
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/Login.fxml"));

            // get the current window (Stage) through the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(loginRoot));
            stage.centerOnScreen(); // Nice touch for user experience
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
