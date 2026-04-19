package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;
import atlantafx.base.theme.Styles;

import model.EventModel;
import dao.EventDAO;

public class EventPageController {
    
    @FXML
    private VBox eventContainer; // The VBox inside your ScrollPane
    
    @FXML
    private TextField searchField; // Link this to your FXML TextField
    searchField.getStyleClass().add(Styles.SEARCH_BOX);
    searchField.getStyleClass().add(Styles.LEFT_PILL);

    @FXML
    public void initialize() {
        // Option A: Search as the user types (Dynamic)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadEventsFromDatabase(newValue);
        });
    }

    // Modify your existing method to handle the EventModel list
    public void loadEventsFromDatabase(String searchQuery) {
        eventContainer.getChildren().clear(); 
        
        EventDAO dao = new EventDAO();
        List<EventModel> events = (searchQuery == null || searchQuery.isEmpty()) 
                                    ? dao.getAllEvents() 
                                    : dao.getEvents(searchQuery);
        
        for (EventModel event : events) {
            // Create a nicer UI element than just a label
            Label titleLabel = new Label(event.getTitle());
            titleLabel.getStyleClass().add("h4"); // AtlantaFX style
            
            VBox card = new VBox(titleLabel, new Label(event.getDate()));
            card.getStyleClass().add("card"); 
            
            eventContainer.getChildren().add(card);
        }
    }
}