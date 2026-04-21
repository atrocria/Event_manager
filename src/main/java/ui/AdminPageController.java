package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.EventModel;
import model.UserModel;
import utils.UserSession;

import java.io.IOException;
import java.util.List;

import atlantafx.base.theme.Styles;
import dao.EventDAO;
import dao.UserDao;

public class AdminPageController {

    @FXML private Button ManageEventsBttn;
    @FXML private Button ManageUsersBttn;

    // This is the container you added in Scene Builder
    @FXML private ScrollPane contentArea;
    private MainController mainController;

    @FXML
    private VBox eventContainer; // The VBox inside your ScrollPane
    
    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        // Search as the user types (Dynamic)
        searchField.getStyleClass().addAll(Styles.LEFT_PILL); //! search the database every time a new key is added or removed
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // 1. Check if the contentArea is empty
            if (eventContainer.getChildren().isEmpty()) {
                System.out.println("Content area is empty.");
                return;
            }

            // 2. Get the current root node loaded in the contentArea
            Node currentView = eventContainer.getChildren().get(0);
            String viewId = currentView.getId();

            if ("manageEventsRoot".equals(viewId)) {
                // Logic specific to event searching
                loadEventsFromDatabase(newValue);
            } else if ("manageUsersRoot".equals(viewId)) {
                // Logic specific to user searching
                loadUsersFromDatabase(newValue);
            }
        });

        UserModel user = UserSession.getInstance().getUser();
        if (user != null) {
            System.out.println("Currently inside AdminEventsManager, logged in as: " + user.getname());
            // Use user.getrole() here to hide/show buttons
        }

        loadEventsFromDatabase("");
    }

    @FXML
    public void DisplayManageEvents() {
        loadControls("/ManageEvents.fxml");
    }

    @FXML
    public void DisplayManageUser() {
        loadControls("/ManageUsers.fxml");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void loadControls(String fxmlPath) {
        try {
            // Load new FXML file
            Parent node = FXMLLoader.load(getClass().getResource(fxmlPath));

            // Clear old content and set to new page called
            eventContainer.getChildren().setAll(node);

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEventsFromDatabase(String searchQuery) {
        eventContainer.getChildren().clear();

        EventDAO dao = new EventDAO();
        List<EventModel> events = (searchQuery == null || searchQuery.isEmpty())
                ? dao.getAllEvents()
                : dao.getEvents(searchQuery);

        try {
            for (EventModel event : events) {
                // Load the FXML for the individual card
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminEventItem.fxml"));
                VBox card = loader.load();

                // Get the controller attached to that card
                AdminEventItemController controller = loader.getController();

                // lambda, express way to MainController.java -> EventDetail.fxml
                controller.setData(event, (selectedEvent) -> {
                    if (mainController != null) {
                        mainController.showDetailedView(selectedEvent);
                    } else {
                        System.out.println("Error: MainController not linked to AdminPageController!");
                    }
                });

                // Add the card to main container
                eventContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace(); // log FXML errors
        }
    }
    
    public void loadUsersFromDatabase(String searchQuery){
        eventContainer.getChildren().clear();

        UserDao dao = new UserDao();
        List<UserModel> users = (searchQuery == null || searchQuery.isEmpty())
                ? dao.getAllUsers()
                : dao.getUsers(searchQuery);

        try {
            for (UserModel user : users) {
                // Load the FXML for the individual card
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUserItem.fxml"));
                VBox card = loader.load();

                AdminUserItemController controller = loader.getController();

                controller.setData(user, (selectedUser) -> {
                    // Logic to delete the user
                    boolean success = new UserDao().deleteUser(selectedUser.getid());
                    if (success) {
                        loadUsersFromDatabase(""); // Refresh the list
                    } else {
                        System.err.println("unable to delete user");
                    }
                });

                // Add the card to main container
                eventContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace(); // log FXML errors
        }
    }
}