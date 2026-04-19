package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.UserModel;
import model.UserRole;
import service.PermissionService;
import utils.UserSession;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MainController {
    
    @FXML private Button btnCreateEvent;
    @FXML private Button btnAdminPanel;
    @FXML private Label lblUserStatus;
    
    @FXML private ComboBox<UserRole> roleSelector;
    
    public void initialize() {
        UserModel currentUser = UserSession.getInstance().getUser();
        UserRole currentRole = UserSession.getInstance().getUser().getrole();

        // Use the Service to check permission
        btnCreateEvent.setVisible(PermissionService.canCreateEvent(currentRole));
        btnAdminPanel.setVisible(PermissionService.canDeleteUser(currentRole));
        
        // Only show roles EQUAL TO or LOWER than the user's actual rank
        for (UserRole role : UserRole.values()) {
            if (currentUser.getrole().getLevel() >= role.getLevel()) {
                roleSelector.getItems().add(role);
            }
        }
        
        // Default the dropdown to their highest role
        roleSelector.setValue(currentUser.getrole());
    
        // Listener: When they change the dropdown, update the UI permissions
        roleSelector.setOnAction(e -> {
            applyPermissions(roleSelector.getValue());
        });
    }
    
    private void applyPermissions(UserRole selectedRole) {
        // Example: Hide the "Create Event" button if they switch to 'Attendee' view
        btnCreateEvent.setVisible(selectedRole.getLevel() >= 80);
        
        // Example: Only show "Speaker Bio" button if in Speaker view
        btnMySessions.setVisible(selectedRole == UserRole.SPEAKER);
    }


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