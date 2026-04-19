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
    
    @FXML
    private VBox eventContainer; // The VBox inside your ScrollPane
    
    @FXML
    private TextField searchField; // Link this to your FXML TextField

    @FXML
    public void initialize() {
        // Search as the user types (Dynamic)
        searchField.getStyleClass().addAll(Styles.LEFT_PILL); //! any issue is prob here
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadEventsFromDatabase(newValue);
        });

        UserModel user = UserSession.getInstance().getUser();
        if (user != null) {
            System.out.println("Currently logged in as: " + user.getname());
            // Use user.getrole() here to hide/show buttons
        }
        
        // Your existing search logic...
        loadEventsFromDatabase("");
    }

    // Modify your existing method to handle the EventModel list
   public void loadEventsFromDatabase(String searchQuery) {
        eventContainer.getChildren().clear(); 
        
        EventDAO dao = new EventDAO();
        List<EventModel> events = (searchQuery == null || searchQuery.isEmpty()) 
                                    ? dao.getAllEvents() 
                                    : dao.getEvents(searchQuery);
        
        try {
            for (EventModel event : events) {
                // 1. Load the FXML for the individual card
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventItem.fxml"));
                VBox card = loader.load(); //! Assuming the root of event_item.fxml is a VBox

                // 2. Get the controller attached to that card
                EventItemController controller = loader.getController();
                
                //! not yet innitialized
                controller.setData(event);

                // 4. Add the card to your main container
                eventContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Always log FXML loading errors!
        }
    }
}