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

    private boolean isFormInvalid() {
        String errorMsg = "";

        if (txtName.getText().isEmpty()) errorMsg += "Cardholder name is required.\n";
        if (txtCardNumber.getText().length() < 16) errorMsg += "Card number must be 16 digits.\n";
        if (!txtExpiry.getText().matches("(0[1-9]|1[0-2])/[0-9]{2}")) errorMsg += "Expiry must be MM/YY.\n";
        if (txtCVV.getText().length() < 3) errorMsg += "CVV must be 3 digits.\n";

        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Payment Error");
            alert.setHeaderText("Please correct the following fields:");
            alert.setContentText(errorMsg);
            alert.showAndWait();
            return true;
        }
        return false;
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