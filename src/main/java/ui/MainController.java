package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MainController {

    @FXML
    public void handleClose() {
        Platform.exit();
    }

    @FXML
    private AnchorPane contentArea; // This matches the fx:id you set

    // This method handles the button clicks
    @FXML
    private void handleDashboardButton(ActionEvent event) {
        loadView("Dashboard.fxml");
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        loadView("Settings.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            // Load the new FXML file
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            
            // Clear the current content and add the new one
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
            
            // Optional: Make the new content stretch to fill the area
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}