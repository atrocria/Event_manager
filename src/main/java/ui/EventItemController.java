package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.EventModel;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class EventItemController {

    // individual card info for event displaying tab
    public void setData(EventModel event) {
        titleLabel.setText(event.getName());
        typeBadge.setText(event.getClass().getSimpleName().replace("Event", ""));
        
        // Check type to show the "Special" field
        if (event instanceof ConcertEvent c) {
            extraInfoLabel.setText("Performer: " + c.getPerformer());
        } else if (event instanceof ConferenceEvent conf) {
            extraInfoLabel.setText("Topic: " + conf.getResearchTopic());
        } else {
            extraInfoLabel.setText(""); // Hide if not applicable
        }
    }
}