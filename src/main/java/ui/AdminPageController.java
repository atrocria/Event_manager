package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
    @FXML private ScrollPane contentArea;
    @FXML private VBox eventContainer; 
    @FXML private TextField searchField;

    private MainController mainController;

    // Track which "tab" we are currently on
    private enum AdminView { EVENTS, USERS }
    private AdminView currentView = AdminView.EVENTS;

    @FXML
    public void initialize() {
        // AtlantaFX styling
        searchField.getStyleClass().addAll(Styles.LEFT_PILL);

        // Dynamic Search Listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (currentView == AdminView.EVENTS) {
                loadEventsFromDatabase(newValue);
            } else {
                loadUsersFromDatabase(newValue);
            }
        });

        // Debugging login session
        UserModel user = UserSession.getInstance().getUser();
        if (user != null) {
            System.out.println("Admin logged in: " + user.getname());
        }

        // Default view on load
        DisplayManageEvents();
    }

    @FXML
    public void DisplayManageEvents() {
        currentView = AdminView.EVENTS;
        searchField.clear(); // Clear search text when switching tabs
        loadEventsFromDatabase("");
    }

    @FXML
    public void DisplayManageUser() {
        currentView = AdminView.USERS;
        searchField.clear(); // Clear search text when switching tabs
        loadUsersFromDatabase("");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Fetches Events and populates the container
     */
    public void loadEventsFromDatabase(String searchQuery) {
        eventContainer.getChildren().clear();

        EventDAO dao = new EventDAO();
        List<EventModel> events = (searchQuery == null || searchQuery.isEmpty())
                ? dao.getAllEvents()
                : dao.getEvents(searchQuery);

        try {
            for (EventModel event : events) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminEventItem.fxml"));
                VBox card = loader.load();

                AdminEventItemController controller = loader.getController();
                controller.setData(event, (selectedEvent) -> {
                    if (mainController != null) {
                        mainController.showDetailedView(selectedEvent);
                    } else {
                        System.err.println("Error: MainController not linked!");
                    }
                });

                eventContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            System.err.println("Error loading AdminEventItem.fxml");
            e.printStackTrace();
        }
    }

    /**
     * Fetches Users and populates the container
     */
    public void loadUsersFromDatabase(String searchQuery) {
        eventContainer.getChildren().clear();

        UserDao dao = new UserDao();
        List<UserModel> users = (searchQuery == null || searchQuery.isEmpty())
                ? dao.getAllUsers()
                : dao.getUsers(searchQuery);

        try {
            for (UserModel user : users) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUserItem.fxml"));
                VBox card = loader.load();

                AdminUserItemController controller = loader.getController();
                controller.setData(user, (selectedUser) -> {
                    // Logic to delete the user
                    boolean success = new UserDao().deleteUser(selectedUser.getid());
                    if (success) {
                        loadUsersFromDatabase(searchField.getText()); // Refresh with current search
                    } else {
                        System.err.println("Unable to delete user");
                    }
                });

                eventContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            System.err.println("Error loading AdminUserItem.fxml");
            e.printStackTrace();
        }
    }
}