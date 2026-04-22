package ui;

import dao.EventDAO;
import dao.UserDao; // Assuming you have a UserDAO
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.UserRole;
import utils.UserSession;

public class StaffPageController {

    @FXML private TableView<Object> tblAttendance; // Simplified for example
    @FXML private TableView<Object> tblUsers;
    
    private EventDAO eventDAO = new EventDAO();
    private UserDao userDAO = new UserDao();

    @FXML
    public void initialize() {
        loadAttendeeData();
        loadUserData();
    }

    @FXML
    private void loadAttendeeData() {
        // Here you would fetch registrations where payment_status = 'PAID' 
        // and attendance_status = 'PENDING'
        System.out.println("Loading PAID registrations for check-in...");
    }

    private void loadUserData() {
        System.out.println("Loading users with role levels lower than Staff...");
    }

    // Logic for the 'Check-in' button in the table
    private void handleCheckIn(int registrationId) {
        // String sql = "UPDATE registration SET attendance_status = 'CHECKED_IN' WHERE registration_id = ?"
        boolean success = eventDAO.updateAttendance(registrationId, "CHECKED_IN");
        if (success) loadAttendeeData();
    }

    // Logic for updating roles
    private void handleRoleChange(int userId, UserRole newRole) {
        UserRole staffRole = UserSession.getInstance().getUser().getrole();
        
        // SECURITY: Staff can only promote/demote to roles BELOW Staff level
        if (newRole.getLevel() < staffRole.getLevel()) {
            userDAO.updateUserRole(userId, newRole);
            System.out.println("User role updated successfully.");
        } else {
            System.err.println("Permission Denied: Staff cannot assign Staff or Admin roles.");
        }
    }

    private void setmainController(MainController mainController) {
        // This method can be used to call back to the main controller if needed
        mainController.setStaffController(this);
    }
}