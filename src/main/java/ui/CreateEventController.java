package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.EventModel;
import model.WorkshopEvent;
import utils.UserSession;
import java.time.LocalTime;

public class CreateEventController {

    @FXML private TextField txtTitle;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtVenueId;
    @FXML private TextField txtMaxAttendees;
    @FXML private TextField txtStartTime;
    @FXML private ComboBox<String> cbEventType;
    @FXML private TextField txtSpecificField;
    @FXML private Label lblSpecificField;

    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        cbEventType.getItems().addAll("CONCERT", "CONFERENCE", "WORKSHOP");
        
        // Dynamic Label update based on type
        cbEventType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            switch (newVal) {
                case "CONCERT" -> lblSpecificField.setText("Artist/Performer Name:");
                case "CONFERENCE" -> lblSpecificField.setText("Research Topic:");
                case "WORKSHOP" -> lblSpecificField.setText("Materials Required:");
            }
        });
    }

    @FXML
    private void handleSaveEvent() {
        // Security check based on your DAO level requirements
        if (UserSession.getInstance().getUser().getrole().getLevel() < 60) {
            new Alert(Alert.AlertType.ERROR, "Unauthorized: Insufficient permissions.").show();
            return;
        }

        try {
            String type = cbEventType.getValue();
            if (type == null) throw new Exception("Please select an event type.");
            if (dpDate.getValue() == null) throw new Exception("Please select a date.");

            EventModel newEvent;
            String specText = txtSpecificField.getText();

            switch (type) {
                case "CONCERT" -> {
                    ConcertEvent ce = new ConcertEvent();
                    ce.setArtistName(specText);
                    newEvent = ce;
                }
                case "CONFERENCE" -> {
                    ConferenceEvent co = new ConferenceEvent();
                    co.setResearchTopic(specText);
                    co.setKeynoteSpeaker("TBD"); // Default or add another field
                    newEvent = co;
                }
                default -> {
                    WorkshopEvent we = new WorkshopEvent();
                    we.setMaterialList(specText);
                    newEvent = we;
                }
            }

            // Standard Fields
            newEvent.setTitle(txtTitle.getText());
            newEvent.setDescription(txtDescription.getText());
            newEvent.setDate(dpDate.getValue().toString());
            newEvent.setVenue(Integer.parseInt(txtVenueId.getText()));
            newEvent.setMax_attendees(Integer.parseInt(txtMaxAttendees.getText()));
            newEvent.setOrganizer(UserSession.getInstance().getUser().getid());
            newEvent.setStatus("UPCOMING");
            newEvent.setType(type);

            // Handle Time
            String timeStr = txtStartTime.getText(); // Expecting "HH:mm"
            if (timeStr == null || timeStr.isBlank()) throw new Exception("Start Time is required!");
            newEvent.setStartTime(LocalTime.parse(timeStr).atDate(dpDate.getValue()));

            // Call your DAO
            eventDAO.addEvent(newEvent);

            clearFields();
            new Alert(Alert.AlertType.INFORMATION, "Event Created Successfully!").show();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Venue ID and Max Attendees must be numbers.").show();
        } catch (java.time.format.DateTimeParseException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid Time Format! Use HH:mm (e.g., 18:30)").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtDescription.clear();
        txtStartTime.clear();
        txtMaxAttendees.clear();
        txtVenueId.clear();
        txtSpecificField.clear();
        dpDate.setValue(null);
        cbEventType.setValue(null);
    }
}