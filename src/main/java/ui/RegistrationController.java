package ui;

import java.io.IOException;

import dao.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.UserRole;

public class RegistrationController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerBttn;

    @FXML
    public void handleRegistration(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Pass UserRole.ATTENDEE here to set the default
        boolean success = UserDao.register(name, email, password, UserRole.ATTENDEE);

        if (success) {
            System.out.println("Registration successful for: " + name);
            // Redirect back to login screen
            switchScene("/Login.fxml", event);
        } else {
            System.err.println("Registration failed.");
            // Optional: show an alert to the user here
        }
    }
    
    private void switchScene(String fxmlPath, javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            // Use the event source (the button clicked) to find the stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
