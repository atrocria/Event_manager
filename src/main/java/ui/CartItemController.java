package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.EventModel;
import model.ConcertEvent; // Import your specific subclass

public class CartItemController {

    @FXML private Label lblEventTitle;
    @FXML private Label lblTicketType;
    @FXML private Label lblDate;

    private EventModel event;
    private Runnable removeCallback;

    public void setData(EventModel event, Runnable removeCallback) {
        this.event = event;
        this.removeCallback = removeCallback;

        lblEventTitle.setText(event.getTitle());
        lblTicketType.setText(event.getType());
        lblDate.setText(event.getDate());

        // Check if it's a ConcertEvent to get the Ticket Type
        if (event instanceof ConcertEvent) {
            lblTicketType.setText(((ConcertEvent) event).getTicketType());
        } else {
            lblTicketType.setText("Standard Entry"); // Fallback for general events
        }
    }

    @FXML
    private void onRemoveClicked() {
        if (removeCallback != null) {
            removeCallback.run();
        }
    }
}