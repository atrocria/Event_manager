package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import atlantafx.base.theme.Styles;

import model.EventModel;
import model.UserModel;
import utils.UserSession;
import dao.EventDAO;


public class EventPageController {
    
    // reference to parent
    private MainController mainController;

    @FXML
    private VBox eventContainer;
    
    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        // Search as the user types (Dynamic)
        searchField.getStyleClass().addAll(Styles.LEFT_PILL); //! search the database every time a new key is added or removed
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadEventsFromDatabase(newValue);
        });

        UserModel user = UserSession.getInstance().getUser();
        if (user != null) {
            System.out.println("Currently inside EventsPage, logged in as: " + user.getName());
            // Use user.getRole() here to hide/show buttons
        }

        loadEventsFromDatabase("");
    }
    
    // this is how to call the main controller for callback
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Modify your existing method to handle the EventModel list
    public void loadEventsFromDatabase(String searchQuery) {
        eventContainer.setFillWidth(true);
        eventContainer.getChildren().clear(); 
        eventContainer.setSpacing(8); 
        eventContainer.setPadding(new javafx.geometry.Insets(10));
        
        EventDAO dao = new EventDAO();
        List<EventModel> events = (searchQuery == null || searchQuery.isEmpty()) 
                                    ? dao.getAllEvents() 
                                    : dao.getEvents(searchQuery);
        
        try {
            for (EventModel event : events) {
                // 1. Load the FXML for the individual card
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventItem.fxml"));
                VBox card = loader.load();
                card.setMaxWidth(Double.MAX_VALUE);

                // 2. Get the controller attached to that card
                EventItemController controller = loader.getController();
                
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
}