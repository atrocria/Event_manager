package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.EventModel;
import model.UserModel;
import model.WorkshopEvent;
import utils.UserSession;
import dao.EventDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class EventDetailController {

    @FXML private ImageView eventImageView;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label venueLabel;
    @FXML private Label registrationDeadlineLabel;
    @FXML private Label specialInfoLabel; // The Label that says "Special Information"
    @FXML private Label dynamicDetailsLabel; // The Label for "Details based on event type"
    @FXML private Button bookTicketButton;

    private EventModel currentEvent;
    private final EventDAO eventDAO = new EventDAO();

    public void initializeDetails(EventModel event) {
        this.currentEvent = event;
        populateDetails();
    }

    private void populateDetails() {
        if (currentEvent == null) return;

        // 1. Set Common Fields
        titleLabel.setText(currentEvent.getTitle());
        descriptionLabel.setText(currentEvent.getDescription());
        venueLabel.setText("Venue ID: " + currentEvent.getVenue());
        registrationDeadlineLabel.setText(currentEvent.getRegistrationDeadLine());

        // Formatting Date & Time
        if (currentEvent.getStartTime() != null) {
            dateTimeLabel.setText(currentEvent.getDate() + " @ " + 
                currentEvent.getStartTime().toLocalTime().toString());
        } else {
            dateTimeLabel.setText(currentEvent.getDate());
        }

        // 2. Set Type-Specific Details (Logic based on your DAO subclasses)
        if (currentEvent instanceof ConcertEvent) {
            specialInfoLabel.setText("Performer / Artist");
            dynamicDetailsLabel.setText(((ConcertEvent) currentEvent).getArtistName());
            
        } else if (currentEvent instanceof ConferenceEvent) {
            specialInfoLabel.setText("Keynote Speaker & Topic");
            ConferenceEvent conf = (ConferenceEvent) currentEvent;
            dynamicDetailsLabel.setText(conf.getKeynoteSpeaker() + " - " + conf.getResearchTopic());
            
        } else if (currentEvent instanceof WorkshopEvent) {
            specialInfoLabel.setText("Materials Needed");
            dynamicDetailsLabel.setText(((WorkshopEvent) currentEvent).getMaterialList());
            
        } else {
            specialInfoLabel.setText("Additional Info");
            dynamicDetailsLabel.setText("No specific details available.");
        }
    }

    @FXML
    private void handleBookTicket() {
        // Basic logic for booking
        if (currentEvent == null) return;

        
        UserModel currentUser = UserSession.getInstance().getUser();
        
        if (currentUser != null && currentEvent != null) {
            // 2. This is where you'd use the DAO (assuming you add a register method)
            eventDAO.registerUserForEvent(currentUser.getid(), currentEvent.getID(), null); //! add a ticket type selection later
            
            // You would typically call a RegistrationDAO here
            // For now, let's show a confirmation alert
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Booking Confirmation");
            alert.setHeaderText("Registering for: " + currentEvent.getTitle());
            alert.setContentText("Your request to book this ticket has been received!");
            alert.showAndWait();
            
            // Disable button after booking to prevent double clicks
            bookTicketButton.setDisable(true);
            bookTicketButton.setText("Registered");
            bookTicketButton.setDisable(true);
        }
    }
}