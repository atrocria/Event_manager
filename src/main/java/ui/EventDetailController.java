package ui;

import model.EventModel;

public class EventDetailController {
    public void initializeDetails(EventModel event) {
        titleLabel.setText(event.getTitle());
        descriptionLabel.setText(event.getDescription());
        statusLabel.setText(event.getStatus());
        typeLabel.setText(event.getType().toUpperCase());
        
        // Formatting date/time
        dateTimeLabel.setText(event.getEventDate() + " @ " + event.getStartTime());
        
        // Dynamic logic for the "Extra" fields
        specialInfoBox.setVisible(true);
        switch (event.getType().toUpperCase()) {
            case "CONCERT":
                specialFieldLabel.setText("Performer:");
                specialFieldValue.setText(event.getPerformer());
                break;
            case "WORKSHOP":
                specialFieldLabel.setText("Material List:");
                specialFieldValue.setText(event.getMaterialList());
                break;
            case "KEYNOTE":
                specialFieldLabel.setText("Keynote Speaker:");
                specialFieldValue.setText(event.getKeynoteSpeaker());
                break;
            default:
                specialInfoBox.setVisible(false); // Hide if it's just a general event
                break;
        }
    }
}
