package ui;

import java.util.List;
import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.EventModel;
import utils.UserSession;

public class CheckoutController {

    @FXML private TextField txtName;
    @FXML private TextField txtCardNumber;
    @FXML private TextField txtExpiry;
    @FXML private PasswordField txtCVV;
    @FXML private Label lblFinalTotal;

    private final EventDAO eventDAO = new EventDAO();
    private double pendingTotal = 0.0;

    @FXML
    public void initialize() {
        if (pendingTotal > 0) {
            updateTotalDisplay();
        }

        // Constraints for inputs
        txtCardNumber.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) txtCardNumber.setText(newVal.replaceAll("[^\\d]", ""));
            if (newVal.length() > 16) txtCardNumber.setText(oldVal);
        });

        txtCVV.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) txtCVV.setText(newVal.replaceAll("[^\\d]", ""));
            if (newVal.length() > 3) txtCVV.setText(oldVal);
        });
    }

    public void prepareCheckout(List<EventModel> items) {
        if (items != null) {
            this.pendingTotal = items.stream().mapToDouble(event -> event.calculateTicketPrice(event.getType())).sum();
            if (lblFinalTotal != null) {
                updateTotalDisplay();
            }
        }
    }

    private void updateTotalDisplay() {
        lblFinalTotal.setText(String.format("Total to Pay: $%.2f", pendingTotal));
    }

    @FXML
    private void handleProcessPayment() {
        if (txtName.getText().isEmpty() || txtCardNumber.getText().length() < 16) {
            showAlert(Alert.AlertType.WARNING, "Form Incomplete", "Please enter valid payment details.");
            return;
        }

        int userId = UserSession.getInstance().getUser().getid();
        if (eventDAO.completeUserPayment(userId)) {
            navigateToView("/PostPaymentDetails.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Payment failed to process.");
        }
    }
    
    @FXML
    private void handleBackToCart() {
        navigateToView("/Cart.fxml");
    }

    private void navigateToView(String fxmlPath) {
        Stage stage = (Stage) txtName.getScene().getWindow();
        MainController main = (MainController) stage.getUserData();
        if (main != null) main.loadView(fxmlPath);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}