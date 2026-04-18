package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MainController {

    // dashboard, cart, etc..
    @FXML
    private AnchorPane contentArea; // This matches the fx:id you set

    @FXML
    private void handleCloseButton(ActionEvent event) {
        //todo check if already in the same view before loading in
        Platform.exit();
    }
    // @FXML
    // private void handleDashboardButton(ActionEvent event) {
    //     //todo check if already in the same view before loading in
    //     loadView("/Dashboard.fxml");
    // }
    // @FXML
    // private void handleDashboardButton(ActionEvent event) {
    //     //todo check if already in the same view before loading in
    //     loadView("/Dashboard.fxml");
    // }

    // This method handles the button clicks
    @FXML
    private void handleDashboardButton(ActionEvent event) {
        //todo check if already in the same view before loading in
        loadView("/Dashboard.fxml");
    }
    @FXML
    private void handleEventPageButton(ActionEvent event) {
        //todo check if already in the same view before loading in
        loadView("/EventPage.fxml");
    }
    @FXML
    private void handleCartButton(ActionEvent event) {
        //todo check if already in the same view before loading in
        loadView("/Cart.fxml");
    }
    @FXML
    private void handleButton(ActionEvent event) {
        //todo check if already in the same view before loading in
        loadView("/Dashboard.fxml");
    }

    // bottom
    @FXML
    private void handleProfileButton(ActionEvent event) {
        //todo check if already in the same view before loading in
        loadView("/Profile.fxml");
    }
    @FXML
    private void handleSettingsButton(ActionEvent event) {
        loadView("/Settings.fxml");
    }

    // put new frame into the anchorpane and delete the current one
    private void loadView(String fxmlFile) {
        try {
            // 1. Create a loader instance
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            
            // 2. Load the FXML and get the root (the Dashboard AnchorPane)
            Parent root = loader.load();
            
            // 3. Clear and add to main area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
            
            // 4. Force the new Dashboard to stretch and fill the space
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}