package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.UserModel;
import model.UserRole;
// import service.PermissionService;
import utils.UserSession;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MainController {
    
    @FXML private Button btnCreateEvent;
    @FXML private Button btnMySessions;
    @FXML private Button btnMyConcert;
    @FXML private Button btnMyKeynote;
    @FXML private Button btnAdminPanel;
    @FXML private Label lblUserStatus;
    
    @FXML private ComboBox<UserRole> roleSelector;
    
    public void initialize() {
        UserModel currentUser = UserSession.getInstance().getUser();

        if (currentUser == null) {
            // Redirection must happen after the stage is fully initialized
            Platform.runLater(() -> {
                try {
                    // Get the stage from the contentArea
                    Stage stage = (Stage) contentArea.getScene().getWindow();
                    Parent loginRoot = FXMLLoader.load(getClass().getResource("/Login.fxml"));
                    stage.setScene(new Scene(loginRoot));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        // UserRole currentRole = UserSession.getInstance().getUser().getrole();

        //! add this into event creator tab
        // // Use the Service to check permission
        // btnCreateEvent.setVisible(PermissionService.canCreateEvent(currentRole));
        // btnAdminPanel.setVisible(PermissionService.canDeleteUser(currentRole));
        // applyPermissionsBttn(currentRole); //whether need show admin controls

        // // Only show roles EQUAL TO or LOWER than the user's actual rank
        // for (UserRole role : UserRole.values()) {
        //     if (currentUser.getrole().getLevel() >= role.getLevel()) {
        //         roleSelector.getItems().add(role);
        //     }
        // }

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
    private void handleAdminButton(ActionEvent event) {
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
    private String currentViewPath = "";

    void loadView(String fxmlFile) {
        UserModel user = UserSession.getInstance().getUser();
        if (fxmlFile.equals("/AdminPanel.fxml") && user.getrole() != UserRole.ADMIN) {
            System.out.println("Access Denied to Admin Panel");
            loadView("/Dashboard.fxml"); // Redirect them to safety
            return;
        }

        // Prevent reloading the exact same page
        if (fxmlFile.equals(currentViewPath)) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            contentArea.getChildren().setAll(root); // clears all

            // Stretch to fill
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            currentViewPath = fxmlFile; // Update current view
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile);
            e.printStackTrace();
        }
    }
    
    // only admins and staff can view these buttons >=80 permission level
    private void applyPermissionsBttn(UserRole selectedRole) {
        btnCreateEvent.setVisible(selectedRole.getLevel() >= 60);
        btnAdminPanel.setVisible(selectedRole == UserRole.ADMIN);

        // If a user was on the Admin Panel but switched to "Attendee" view,
        // kick them back to the Dashboard
        if (currentViewPath.equals("/AdminPanel.fxml") && selectedRole != UserRole.ADMIN) {
            loadView("/Dashboard.fxml");
        }
    }

}