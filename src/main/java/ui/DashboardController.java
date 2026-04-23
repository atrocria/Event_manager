package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.EventModel;
import model.UserModel;
import utils.UserSession;
import java.util.List;

public class DashboardController {

    @FXML private Label lblTotalEvents;
    @FXML private Label lblMyRegistrations;
    @FXML private Label lblRoleLevel;
    @FXML private FlowPane eventGrid;

    // --- Styling Constants ---
    private static final String IDLE_CARD = 
        "-fx-background-color: #2d2d2d; -fx-background-radius: 12; -fx-padding: 15; " +
        "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);";
    
    private static final String HOVER_CARD = 
        "-fx-background-color: #383838; -fx-background-radius: 12; -fx-padding: 15; " +
        "-fx-cursor: hand; -fx-border-color: #3498db; -fx-border-radius: 12; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(52, 152, 219, 0.3), 10, 0, 0, 0);";

    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        UserModel currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) return;

        // 1. Setup Stats Header
        lblRoleLevel.setText(currentUser.getrole().name());
        
        List<EventModel> allEvents = eventDAO.getAllEvents();
        List<EventModel> myEvents = eventDAO.getEventsByUserId(currentUser.getid());

        lblTotalEvents.setText(String.valueOf(allEvents.size()));
        lblMyRegistrations.setText(String.valueOf(myEvents.size()));

        // 2. Load the Cards
        populateEventGrid(allEvents);
    }

    private void populateEventGrid(List<EventModel> events) {
        eventGrid.getChildren().clear();

        if (events.isEmpty()) {
            Label noEvents = new Label("No upcoming events found.");
            noEvents.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
            eventGrid.getChildren().add(noEvents);
            return;
        }

        for (EventModel event : events) {
            VBox card = createEventCard(event);
            eventGrid.getChildren().add(card);
        }
    }

    private VBox createEventCard(EventModel event) {
        // Main Container
        VBox card = new VBox(10); // Spacing 10
        card.setPrefSize(230, 150);
        card.setMaxWidth(230);
        card.setStyle(IDLE_CARD);

        // Header: Event Type
        Label typeLabel = new Label(event.getType().toUpperCase());
        typeLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        // Body: Title
        Label titleLabel = new Label(event.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        titleLabel.setWrapText(true);
        titleLabel.setPrefHeight(50); // Give title fixed room

        // Footer: Date
        Label dateLabel = new Label("📅 " + event.getDate());
        dateLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 12px;");

        card.getChildren().addAll(typeLabel, titleLabel, dateLabel);

        // --- Interaction Logic ---

        // 1. Hover Effects
        card.setOnMouseEntered(e -> card.setStyle(HOVER_CARD));
        card.setOnMouseExited(e -> card.setStyle(IDLE_CARD));

        // 2. Click Logic: Route to MainController Detailed View
        card.setOnMouseClicked(e -> {
            if (card.getScene() != null && card.getScene().getWindow() != null) {
                Stage stage = (Stage) card.getScene().getWindow();
                MainController mainController = (MainController) stage.getUserData();
                
                if (mainController != null) {
                    mainController.showDetailedView(event);
                } else {
                    System.err.println("❌ ERROR: MainController reference missing in Stage UserData.");
                }
            }
        });

        return card;
    }
}