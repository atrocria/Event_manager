package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.EventModel;
import model.UserModel;
import model.UserRole;
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
    @FXML private Label specialInfoLabel;
    @FXML private Label dynamicDetailsLabel;
    @FXML private Label eligibilityLabel;
    @FXML private Button bookTicketButton;
    @FXML private Button checkoutNowButton;
    
    @FXML private ComboBox<String> ticketTypeComboBox;
    @FXML private ComboBox<String> numberOfTicketsComboBox;

    private EventModel currentEvent;
    private final EventDAO eventDAO = new EventDAO();

    public void initializeDetails(EventModel event, UserRole userRole) {
        this.currentEvent = event;
        populateDetails();
        adminControlsVisibility(userRole);

        UserModel currentUser = UserSession.getInstance().getUser();
        EventDAO eventDAO = new EventDAO();

        // If already registered, disable the button so they can't click it again
        if (eventDAO.isUserRegistered(currentUser.getid(), event.getID())) {
            bookTicketButton.setDisable(true);
            bookTicketButton.setText("Already Registered");

            checkoutNowButton.setDisable(true);
            checkoutNowButton.setText("Already Registered");
        }
    }

    private void populateDetails() {
        if (currentEvent == null) return;

        // common fields
        titleLabel.setText(currentEvent.getTitle());
        descriptionLabel.setText(currentEvent.getDescription());
        venueLabel.setText("Venue ID: " + currentEvent.getVenue());
        registrationDeadlineLabel.setText(currentEvent.getRegistrationDeadLine());

        if (currentEvent.getStartTime() != null) {
            dateTimeLabel.setText(currentEvent.getDate() + " @ " + 
                currentEvent.getStartTime().toLocalTime().toString());
        } else {
            dateTimeLabel.setText(currentEvent.getDate());
        }

        // event specific details
        if (currentEvent instanceof ConcertEvent) {
            specialInfoLabel.setText("Performer / Artist");
            dynamicDetailsLabel.setText(((ConcertEvent) currentEvent).getArtistName());

            ticketTypeComboBox.setVisible(true);
            numberOfTicketsComboBox.setVisible(true);
            
            //eligible for early bird discount or VIP
            eligibilityLabel.setVisible(true);;
            if (((ConcertEvent) currentEvent).isEarlyBirdEligible()) {
                eligibilityLabel.setText("Eligible for Early Bird Discount");
            } else {
                eligibilityLabel.setText("You are eligible for VIP seating");
            }
            
        } else if (currentEvent instanceof ConferenceEvent) {
            specialInfoLabel.setText("Keynote Speaker & Topic");
            ConferenceEvent conf = (ConferenceEvent) currentEvent;
            dynamicDetailsLabel.setText(conf.getKeynoteSpeaker() + " - " + conf.getResearchTopic());
            eligibilityLabel.setText("");
            eligibilityLabel.setVisible(false);;

            ticketTypeComboBox.setVisible(false);
            numberOfTicketsComboBox.setVisible(false);
            
        } else if (currentEvent instanceof WorkshopEvent) {
            specialInfoLabel.setText("Materials Needed");
            dynamicDetailsLabel.setText(((WorkshopEvent) currentEvent).getMaterialList());
            eligibilityLabel.setText("");
            eligibilityLabel.setVisible(false);;
            
            ticketTypeComboBox.setVisible(false);
            numberOfTicketsComboBox.setVisible(false);
        } else {
            specialInfoLabel.setText("Additional Info");
            dynamicDetailsLabel.setText("No specific details available.");
            eligibilityLabel.setText("");
            eligibilityLabel.setVisible(false);;

            ticketTypeComboBox.setVisible(false);
            numberOfTicketsComboBox.setVisible(false);
        }
    }

    // set by level 60+ (organizer) can see manage attendees and ticketing
    private void adminControlsVisibility(UserRole role) {
        UserModel currentUser = UserSession.getInstance().getUser();
        boolean isAdmin = currentUser != null && currentUser.getrole() == UserRole.ADMIN;

        // Only show admin controls if user is an admin
        checkoutNowButton.setVisible(isAdmin);
    }

    @FXML
    private void handleBookTicket() {
        if (currentEvent == null) return;

        UserModel currentUser = UserSession.getInstance().getUser();
        
        if (currentUser != null && currentEvent != null) {
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