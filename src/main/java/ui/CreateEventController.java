package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.EventModel;
import model.WorkshopEvent;
import utils.UserSession;

public class CreateEventController {

    @FXML
    private TextField txtTitle;
    @FXML
    private TextArea txtDescription;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField txtVenueId;
    @FXML
    private TextField txtMaxAttendees;
    @FXML
    private TextField txtStartTime;
    @FXML
    private ComboBox<String> cbEventType;
    @FXML
    private TextField txtSpecificField; // A dynamic field for Performer/Topic/Materials
    
    @FXML
    public void initialize() {
        cbEventType.getItems().addAll("CONCERT", "CONFERENCE", "WORKSHOP");
    }

    @FXML
    private void handleSaveEvent() {
        if (UserSession.getInstance().getUser().getrole().getLevel() < 60)
            return;

        try {
            // cb = combo box
            String type = cbEventType.getValue();
            if (type == null)
                throw new Exception("Please select an event type.");

            EventModel newEvent;
            switch (type) {
                case "CONCERT" -> {
                    ConcertEvent ce = new ConcertEvent();
                    ce.setArtistName(txtSpecificField.getText()); // specific field
                    newEvent = ce;
                }
                case "CONFERENCE" -> {
                    ConferenceEvent co = new ConferenceEvent();
                    co.setResearchTopic(txtSpecificField.getText());
                    newEvent = co;
                }
                default -> {
                    WorkshopEvent we = new WorkshopEvent();
                    we.setMaterialList(txtSpecificField.getText());
                    newEvent = we;
                }
            }

            // Set Shared Fields (The "Standard" stuff)
            newEvent.setTitle(txtTitle.getText());
            newEvent.setDescription(txtDescription.getText());
            newEvent.setDate(dpDate.getValue().toString());
            newEvent.setVenue(Integer.parseInt(txtVenueId.getText()));
            newEvent.setMax_attendees(Integer.parseInt(txtMaxAttendees.getText()));
            newEvent.setOrganizer(UserSession.getInstance().getUser().getid());
            newEvent.setStatus("UPCOMING");
            newEvent.setType(type);

            // FIX THE NULL ERROR: Handle the Start Time
            String timeStr = txtStartTime.getText(); // e.g., "14:30"
            if (timeStr == null || timeStr.isBlank()) {
                throw new Exception("Start Time (HH:mm) is required!");
            }
            // Combine DatePicker date with TextField time
            newEvent.setStartTime(java.time.LocalTime.parse(timeStr).atDate(dpDate.getValue()));

            // Save to DB
            clearFields();
            new Alert(Alert.AlertType.INFORMATION, "Event Saved Successfully!").show();

        } catch (java.time.format.DateTimeParseException e) {
            new Alert(Alert.AlertType.ERROR, "Time must be in HH:mm format (e.g. 14:30)").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }
    
    private void clearFields() {
        txtTitle.clear();
        txtDescription.clear();
        txtStartTime.clear();
        txtMaxAttendees.clear();
        txtSpecificField.clear();
        dpDate.setValue(null);
        cbEventType.setValue(null);
    }
}