package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    
    @FXML private Button btnDashboardPanel;
    @FXML private Button btnEventPanel;
    @FXML private Button btnCartPanel;
    @FXML private Button btnMySessions;
    @FXML private Button btnMyConcert;
    @FXML private Button btnMyKeynote;
    @FXML private Button btnAdminPanel;
    @FXML private Button btnStaffPanel;
    @FXML private Button btnEventCreationPanel;
    @FXML private Button btnShowTicketPanel;
    @FXML private Button btnArtistSpeakerPanel;
    @FXML private Button btnLogoutPanel;
    
    @FXML private Label lblUserStatus;
    
    @FXML private ComboBox<UserRole> roleSelector;
    private UserRole userActualRank;
    
    public void initialize() {
        setupDraggable();

        // 2. Security Check: Redirect to login if session is empty
        UserModel currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            Platform.runLater(() -> {
                try {
                    Stage stage = (Stage) contentArea.getScene().getWindow();
                    Parent loginRoot = FXMLLoader.load(getClass().getResource("/Login.fxml"));
                    stage.setScene(new Scene(loginRoot));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        // 3. THE "MAGIC" PART: Store this controller in the Stage's UserData.
        // This allows Cart/Checkout controllers to call main.loadView() via a lambda lookup.
        Platform.runLater(() -> {
            if (contentArea.getScene() != null && contentArea.getScene().getWindow() != null) {
                contentArea.getScene().getWindow().setUserData(this);
                System.out.println("MainController successfully stored in Stage UserData.");
            }
        });

        // 4. Role & Permissions Logic
        userActualRank = currentUser.getrole(); 
        roleSelector.getItems().clear(); 

        // Fill the selector with allowed roles based on rank
        for (UserRole role : UserRole.values()) {
            if (userActualRank.getLevel() >= role.getLevel()) {
                roleSelector.getItems().add(role);
            }
        }

        // Set the initial value and apply initial UI permissions
        roleSelector.setValue(userActualRank);
        if (btnAdminPanel != null) {
            applyPermissions(userActualRank);
        }

        // Handle Role Switching via ComboBox
        roleSelector.setOnAction(e -> {
            UserRole selectedRole = roleSelector.getValue();
            if (selectedRole != null) {
                UserSession.getInstance().getUser().setRole(selectedRole);
                applyPermissions(selectedRole);
            }
        });

        setIcon(btnDashboardPanel, "/icons/dashboard_icon.png");
        setIcon(btnAdminPanel, "/icons/admin_icon.png");
        setIcon(btnStaffPanel, "/icons/staff_icon.png");
        setIcon(btnEventCreationPanel, "/icons/plus_icon.png");
        setIcon(btnArtistSpeakerPanel, "/icons/artist_icon.png");
        setIcon(btnCartPanel, "/icons/cart_icon.png");
        setIcon(btnEventPanel, "/icons/event_icon.png");
        setIcon(btnShowTicketPanel, "/icons/ticket_icon.png");
        setIcon(btnLogoutPanel, "/icons/logout_icon.png");
        handleDashboardButton(null);
    }

    private void setIcon(Button button, String path) {
        try {
            // Try to get the resource from the class loader
            var resource = getClass().getResource(path);
            if (resource == null) {
                System.out.println("Could not find icon at: " + path);
                return;
            }
            
            Image icon = new Image(resource.toExternalForm());
            ImageView imageView = new ImageView(icon);
            
            imageView.setFitWidth(20); 
            imageView.setFitHeight(20);
            imageView.setPreserveRatio(true);

            javafx.scene.effect.ColorAdjust whiteEffect = new javafx.scene.effect.ColorAdjust();
            whiteEffect.setBrightness(1.0); // Pushes all colors toward white
            imageView.setEffect(whiteEffect);
            
            button.setGraphic(imageView);
            // This line removes the text so it fits your thin sidebar
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        currentViewPath = "/EventPage.fxml";
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
    private void handleShowTicketButton(ActionEvent event) {
        loadView("/ShowTicket.fxml");
    }
    @FXML
    private void handleAdminButton(ActionEvent event) {
        currentViewPath = "/AdminPanel.fxml";
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
    private void handleStaffButton(ActionEvent event) {
        currentViewPath = "/StaffPanel.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Staff.fxml"));
            
            Parent root = loader.load(); 

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace(); // Use printStackTrace to see exactly what failed
        }
    }
    @FXML
    private void handleCreateEventButton(ActionEvent event) {
        loadView("/CreateEvent.fxml");
    }
    @FXML
    private void handleArtistSpeakerButton(ActionEvent event) {
        currentViewPath = "/ArtistSpeaker.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArtistSpeaker.fxml"));
            Parent root = loader.load(); 

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace(); // Use printStackTrace to see exactly what failed
        }
    }

    // bottom
    @FXML
    private void handleLogoutButton(ActionEvent event) {
        loadView("/Profile.fxml");
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
            UserModel currentUser = UserSession.getInstance().getUser();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetail.fxml"));
            Parent detailView = loader.load();

            // Pass the event data to the Detail Controller
            EventDetailController controller = loader.getController();
            controller.initializeDetails(event, currentUser.getrole());

            this.currentViewPath = "/EventDetail.fxml";

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
    private void applyPermissions(UserRole selectedRole) {
        btnAdminPanel.setVisible(selectedRole == UserRole.ADMIN);
        btnStaffPanel.setVisible(selectedRole.getLevel() >= 80);
        btnEventCreationPanel.setVisible(selectedRole.getLevel() >= 60);
        btnArtistSpeakerPanel.setVisible(selectedRole.getLevel() >= 40);

        // If a user was on the Admin Panel but switched to "Attendee" view,
        // kick them back to the Dashboard
        if (currentViewPath.equals("/AdminPanel.fxml") && selectedRole != UserRole.ADMIN) {
            loadView("/Dashboard.fxml");
        }
    }

    public void setCenterView(Parent view) {
        contentArea.getChildren().setAll(view);
        
        // Ensure it stretches to fill the whole space
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
    }
}