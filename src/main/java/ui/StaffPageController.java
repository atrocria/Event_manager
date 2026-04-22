package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StaffPageController {

    @FXML
    private ListView<String> staffListView;

    @FXML
    private TextField staffNameField;

    @FXML
    private Button addStaffButton;

    @FXML
    private Button removeStaffButton;

    @FXML
    private void initialize() {
        // Initialize staff list or other components
    }

    public void setMainController(MainController mainController) {
        //! TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMainController'");
    }
}