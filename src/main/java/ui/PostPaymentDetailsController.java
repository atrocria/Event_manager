package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PostPaymentDetailsController {

    @FXML private Button btnDone;

    @FXML
    private void handleReturnHome() {
        Stage stage = (Stage) btnDone.getScene().getWindow();
        MainController main = (MainController) stage.getUserData();

        if (main != null) {
            main.loadView("/Dashboard.fxml");
        }
    }
}