package ui;

import java.util.function.Consumer;

import dao.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.UserModel;

public class AdminUserItemController {

    @FXML
    private Label username;
    @FXML
    private Label useremail; // Renamed from bio for clarity
    @FXML
    private Label roleLabel;

    private UserModel user;

    public void setData(UserModel user, Consumer<UserModel> deleteCallback) {
        this.user = user;

        // Use user methods, not event methods!
        username.setText(user.getname());
        useremail.setText(user.getemail());

        // Handle the Enum switch
        switch (user.getrole()) {
            case ADMIN:
                roleLabel.setText("🛡️ Admin");
                roleLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red for admin
                break;
            case SPEAKER:
                roleLabel.setText("🎤 Speaker");
                roleLabel.setStyle("-fx-text-fill: #3498db;"); // Blue for speaker
                break;
            case ATTENDEE:
                roleLabel.setText("👤 Attendee");
                roleLabel.setStyle("-fx-text-fill: #2ecc71;"); // Green for attendee
                break;
            default:
                roleLabel.setText("User");
                break;
        }

        // Apply our black text fix for light-colored cards
        username.setStyle("-fx-text-fill: black;");
        useremail.setStyle("-fx-text-fill: black;");
    }

    @FXML
    private void handleDeleteUserButton(ActionEvent event) {
        // 1. Create a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete " + user.getname() + "?");
        alert.setContentText("This action cannot be undone.");

        // 2. If the user clicks "OK", proceed with deletion
        if (alert.showAndWait().get() == ButtonType.OK) {
            UserDao dao = new UserDao();
            // Convert the int user_id to String for your method
            boolean success = dao.deleteUser(user.getid());

            if (success) {
                System.out.println("User deleted successfully.");
                // Important: You should trigger a refresh of the list here
            } else {
                // Show an error if it failed (e.g., database constraint)
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText("Could not delete user. They might be linked to existing events.");
                error.show();
            }
        }
    }
}