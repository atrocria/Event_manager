package ui;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.EventModel;
import model.WorkshopEvent;

public class AdminEventItemController {

    // to callback to parent and switch anchorpane page
    private EventModel event;
    private Consumer<EventModel> onDetailsRequested;

    @FXML private Label titleLabel;
    @FXML private Label extraInfoLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private Label deadlineLabel;
    @FXML private Button ManageEventBttn;
    // @FXML private Label typeBadge;

    //! translation layer set data inside
    public void setData(EventModel event, Consumer<EventModel> callback) {
        this.event = event;
        this.onDetailsRequested = callback;

        titleLabel.setText(event.getTitle());
        descriptionLabel.setText(event.getDescription());
        dateLabel.setText(event.getDate());
        deadlineLabel.setText(event.getRegistrationDeadLine());
        // typeBadge.setText(event.getClass().getSimpleName().replace("Event", ""));

        if (event instanceof ConcertEvent c) {
            extraInfoLabel.setText("🎸" + c.getArtistName());
        } else if (event instanceof ConferenceEvent conf) {
            extraInfoLabel.setText("🎤 " + conf.getResearchTopic());
        } else if (event instanceof WorkshopEvent ws) {
            extraInfoLabel.setText("🔨" + ws.getDiscussionTopics());
        } else {
            extraInfoLabel.setText(""); // Hide if not applicable
        }
    }

    @FXML
    private void handleManageEventButton(ActionEvent event) {
        // give back the selected event to MainController to switch page
        if (onDetailsRequested != null) {
            onDetailsRequested.accept(this.event);
        }
    }
}