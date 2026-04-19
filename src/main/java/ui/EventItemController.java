package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.EventModel;
import model.WorkshopEvent;

public class EventItemController {

    @FXML private Label titleLabel;
    @FXML private Label extraInfoLabel;
    // @FXML private Label typeBadge;

    //! translation layer set data inside
    public void setData(EventModel event) {
        titleLabel.setText(event.getTitle());
        // typeBadge.setText(event.getClass().getSimpleName().replace("Event", ""));
        
        // Check type to show the "Special" field
        if (event instanceof ConcertEvent c) {
            extraInfoLabel.setText("🎸Performer: " + c.getArtistName());
        } else if (event instanceof ConferenceEvent conf) {
            extraInfoLabel.setText("🎤Topic: " + conf.getResearchTopic());
        } else if (event instanceof WorkshopEvent ws) {
            extraInfoLabel.setText("🔨Topic: " + ws.getDiscussionTopics());
        } else {
            extraInfoLabel.setText(""); // Hide if not applicable
        }
    }
}