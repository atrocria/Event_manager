package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class EventPageController {
    
    @FXML
    private VBox eventContainer; // The VBox inside your ScrollPane

    public void loadEventsFromDatabase(String searchQuery) {
        eventContainer.getChildren().clear(); // Clear old list
        
        // 1. Get data from your DB (Mocking it here)
        List<String> events = database.getEvents(searchQuery); 
        
        for (String eventTitle : events) {
            // 2. Create a new UI element for each event
            // You can load your EventItem.fxml here or just create a Label
            Label eventLabel = new Label(eventTitle);
            eventLabel.getStyleClass().add("card"); // AtlantaFX card style
            
            // 3. Add it to the VBox
            eventContainer.getChildren().add(eventLabel);
        }
    }
}