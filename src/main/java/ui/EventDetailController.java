package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
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
    
    @FXML private ComboBox<String> ticketTypeComboBox;
    @FXML private ComboBox<String> numberOfTicketsComboBox;
    @FXML private Button deleteEvent;

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
            eventImageView.setImage(new Image(getClass().getResourceAsStream("/concert.jpg")));
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
            eventImageView.setImage(new Image(getClass().getResourceAsStream("/conference.jpg")));
            specialInfoLabel.setText("Keynote Speaker & Topic");
            ConferenceEvent conf = (ConferenceEvent) currentEvent;
            dynamicDetailsLabel.setText(conf.getKeynoteSpeaker() + " - " + conf.getResearchTopic());
            eligibilityLabel.setText("");
            eligibilityLabel.setVisible(false);;

            ticketTypeComboBox.setVisible(false);
            numberOfTicketsComboBox.setVisible(false);
            
        } else if (currentEvent instanceof WorkshopEvent) {
            eventImageView.setImage(new Image(getClass().getResourceAsStream("/workshop.jpg")));
            specialInfoLabel.setText("Materials Needed");
            dynamicDetailsLabel.setText(((WorkshopEvent) currentEvent).getMaterialList());
            eligibilityLabel.setText("");
            eligibilityLabel.setVisible(false);;
            
            ticketTypeComboBox.setVisible(false);
            numberOfTicketsComboBox.setVisible(false);
        } else {
            eventImageView.setImage(new Image(getClass().getResourceAsStream("/workshop.jpg")));
            specialInfoLabel.setText("Additional Info");
            dynamicDetailsLabel.setText("No specific details available.");
            eligibilityLabel.setText("");
            eligibilityLabel.setVisible(false);;

            ticketTypeComboBox.setVisible(false);
            numberOfTicketsComboBox.setVisible(false);
        }
    }

    // set by level 60+ (organizer) can see manage attendees and ticketing
    @FXML
    private void adminControlsVisibility(UserRole role) {
        UserModel currentUser = UserSession.getInstance().getUser();
        boolean isAdmin = currentUser != null && currentUser.getrole() == UserRole.ADMIN;

        // Only show admin controls if user is an admin
        deleteEvent.setVisible(isAdmin);
        deleteEvent.setManaged(isAdmin);
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

    @FXML
    private void handleDeleteEvent() {
        // 1. Double-check Authorization (Internal Guard)
        UserModel currentUser = UserSession.getInstance().getUser();
        if (currentUser == null || currentUser.getrole() != UserRole.ADMIN) {
            showError("Unauthorized", "Only administrators can delete events.");
            return;
        }

        // 2. Confirmation Dialog (UX Safety)
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Delete Event");
        confirm.setHeaderText("Permanently delete: " + currentEvent.getTitle() + "?");
        confirm.setContentText("This action cannot be undone and will remove all registrations.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                executeDeletion();
            }
        });
    }

    private void executeDeletion() {
        try {
            // 3. Call DAO to delete
            boolean success = eventDAO.deleteEvent(currentEvent.getID());

            if (success) {
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Event Deleted");
                successAlert.setHeaderText(null);
                successAlert.setContentText("The event has been successfully removed.");
                successAlert.showAndWait();

                // 4. Redirect user back to the main list
                returnToMainView();
            } else {
                showError("Error", "Could not delete event from the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error", "An error occurred while deleting the event.");
        }
    }

    private void returnToMainView() {
        try {
            // Get the current stage
            Stage stage = (Stage) deleteEvent.getScene().getWindow();
            
            // Retrieve the MainController reference we stored in UserData
            MainController main = (MainController) stage.getUserData();
            
            if (main != null) {
                // This swaps the center view to the list, keeping the sidebar/header intact
                main.loadView("/EventsList.fxml"); 
            } else {
                // If we can't find the MainController, just alert the user 
                // but keep the current screen visible.
                showError("Navigation Error", "Event deleted, but could not return to list automatically. Please use the menu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error", "Failed to refresh the view.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}