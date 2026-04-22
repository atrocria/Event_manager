package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.EventModel;
import model.UserModel;
import model.UserRole;
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
    @FXML private Button btnEventCreationPanel;
    
    @FXML private Label lblUserStatus;
    
    @FXML private ComboBox<UserRole> roleSelector;
    
    public void initialize() {
        // make the window draggble
        setupDraggable();
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

        UserRole userActualRank = currentUser.getrole();
        roleSelector.getItems().clear(); // clear existing items

        // Fill the selector only with roles EQUAL or LOWER than their actual rank
        for (UserRole role : UserRole.values()) {
            if (userActualRank.getLevel() >= role.getLevel()) {
                roleSelector.getItems().add(role);
            }
        }

        // Default the dropdown to highest available role
        roleSelector.setValue(userActualRank);

        //! add this into event creator tab
        // Use the Service to check permission
        // btnCreateEvent.setVisible(PermissionService.canCreateEvent(currentRole));
        applyPermissionsBttn(userActualRank); //whether need show admin controls

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

    // Add these variables at the top of your class
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private Region titleBarSpacer; // The Region in your FXML

    private void setupDraggable() {
        // We target the Region (spacer) or the HBox itself
        titleBarSpacer.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBarSpacer.setOnMouseDragged(event -> {
            Stage stage = (Stage) titleBarSpacer.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
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
    private AnchorPane contentArea;

    @FXML
    private void handleCloseButton(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void handleMinimizeButton(ActionEvent event) {
        // finds the stage and sets iconified to true (Minimizes to taskbar)
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleDashboardButton(ActionEvent event) {
        loadView("/Dashboard.fxml");
    }
    @FXML
    private void handleEventPageButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventPage.fxml"));
            
            // 1. Load the FXML first (this instantiates the controller)
            Parent root = loader.load(); 
            
            // 2. NOW get the controller
            EventPageController pageController = loader.getController();
            
            // 3. Set the reference
            if (pageController != null) {
                pageController.setMainController(this); 
            }

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace(); // Use printStackTrace to see exactly what failed
        }
    }
    @FXML
    private void handleCartButton(ActionEvent event) {
        loadView("/Cart.fxml");
    }
    @FXML
    private void handleAdminButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin.fxml"));
            
            // 1. Load the FXML first (this instantiates the controller)
            Parent root = loader.load(); 
            
            // 2. NOW get the controller
            AdminPageController pageController = loader.getController();
            
            // 3. Set the reference
            if (pageController != null) {
                pageController.setMainController(this); 
            }

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace(); // Use printStackTrace to see exactly what failed
        }
    }
    @FXML
    private void handleCreateEventButton(ActionEvent event) {
        loadView("/CreateEvent.fxml");
    }

    // bottom
    @FXML
    private void handleProfileButton(ActionEvent event) {
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
            loadView("/Dashboard.fxml"); // Redirect to safety
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

    public void showDetailedView(EventModel event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetail.fxml"));
            Parent detailView = loader.load();

            // Pass the event data to the Detail Controller
            EventDetailController controller = loader.getController();
            controller.initializeDetails(event);

            // Clear area and show the page
            contentArea.getChildren().clear();
            contentArea.getChildren().add(detailView);

            // set fill all area
            AnchorPane.setTopAnchor(detailView, 0.0);
            AnchorPane.setBottomAnchor(detailView, 0.0);
            AnchorPane.setLeftAnchor(detailView, 0.0);
            AnchorPane.setRightAnchor(detailView, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // only admins and staff can view these buttons >=80 permission level
    private void applyPermissionsBttn(UserRole selectedRole) {
        btnAdminPanel.setVisible(selectedRole == UserRole.ADMIN);
        btnEventCreationPanel.setVisible(selectedRole.getLevel() >= 60);

        // If a user was on the Admin Panel but switched to "Attendee" view,
        // kick them back to the Dashboard
        if (currentViewPath.equals("/AdminPanel.fxml") && selectedRole != UserRole.ADMIN) {
            loadView("/Dashboard.fxml");
        }
    }

}