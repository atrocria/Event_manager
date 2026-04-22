package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.UserSession;

public class CheckoutController {

    @FXML private TextField txtName;
    @FXML private TextField txtCardNumber;
    @FXML private TextField txtExpiry;
    @FXML private PasswordField txtCVV;

    private EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        // Simple input masking using listeners
        txtCardNumber.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) txtCardNumber.setText(newVal.replaceAll("[^\\d]", ""));
            if (newVal.length() > 16) txtCardNumber.setText(oldVal);
        });

        txtCVV.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 3) txtCVV.setText(oldVal);
        });
    }

    @FXML
    private void handleProcessPayment() {
        // 1. Validation
        if (txtCardNumber.getText() == null || txtCardNumber.getText().length() < 16) {
            System.err.println("Payment Failed: Invalid Card Number");
            return;
        }

        // 2. CALL THE DAO - Update database
        int userId = UserSession.getInstance().getUser().getid();
        boolean success = eventDAO.completeUserPayment(userId);

        if (success) {
            System.out.println("Database updated: Registration marked as PAID for user " + userId);
            
            // Navigate to Success Page
            Stage stage = (Stage) txtCardNumber.getScene().getWindow();
            MainController main = (MainController) stage.getUserData();
            
            if (main != null) {
                main.loadView("/PostPaymentDetails.fxml");
            }
        } else {
            System.err.println("Payment processed but failed to update database.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Payment Error");
                alert.setHeaderText("Payment processed but failed to update registration status.");
                alert.showAndWait();
        }
    }
}