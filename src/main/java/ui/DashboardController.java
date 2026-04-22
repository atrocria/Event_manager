package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.UserModel;
import utils.UserSession;

public class DashboardController {

    @FXML private Label lblTotalEvents;
    @FXML private Label lblMyRegistrations;
    @FXML private Label lblRoleLevel;
    
    private EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        UserModel user = UserSession.getInstance().getUser();
        if (user != null) {
            // Display User Info
            lblRoleLevel.setText(user.getrole().name() + " (" + user.getrole().getLevel() + ")");
            
            // Fetch stats from DAO
            int total = eventDAO.getAllEvents().size();
            int registered = eventDAO.getEventsByUserId(user.getid()).size();
            
            lblTotalEvents.setText(String.valueOf(total));
            lblMyRegistrations.setText(String.valueOf(registered));
        }
        
        // Optional: Populate the TableView with actual registration data
    }
}