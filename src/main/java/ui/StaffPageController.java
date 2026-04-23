package ui;

import dao.EventDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.AttendeeView;
import model.UserModel;
import model.UserRole;

public class StaffPageController {

    // Attendee Check-in Tab
    @FXML private TextField txtSearchAttendee;
    @FXML private TableView<AttendeeView> tblAttendance;
    @FXML private TableColumn<AttendeeView, String> colUser;
    @FXML private TableColumn<AttendeeView, String> colEvent;
    @FXML private TableColumn<AttendeeView, String> colStatus;
    @FXML private TableColumn<AttendeeView, String> colPaymentStatus;
    @FXML private TableColumn<AttendeeView, Void> colAction;

    // Permissions Tab
    @FXML private TableView<UserModel> tblUsers;
    @FXML private TableColumn<UserModel, String> colUserName;
    @FXML private TableColumn<UserModel, UserRole> colCurrentRole;
    @FXML private TableColumn<UserModel, Void> colRoleAction;

    private final EventDAO eventDAO = new EventDAO();
    private ObservableList<AttendeeView> attendeeData = FXCollections.observableArrayList();
    private ObservableList<UserModel> userData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupAttendanceTable();
        setupPermissionsTable();
        loadAttendeeData();
        loadUserData(); 
    }

    private void setupAttendanceTable() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colEvent.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPaymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // ACTION COLUMN: For Check-In Buttons
        colAction.setCellFactory(param -> new TableCell<AttendeeView, Void>() {
            private final Button btn = new Button("Check-In");
            {
                btn.setOnAction(event -> {
                    AttendeeView data = getTableView().getItems().get(getIndex());
                    handleCheckIn(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AttendeeView data = getTableView().getItems().get(getIndex());
                    btn.setDisable("CHECKED_IN".equals(data.getStatus()));
                    setGraphic(btn);
                }
            }
        });

        // Search Filter Logic
        FilteredList<AttendeeView> filteredData = new FilteredList<>(attendeeData, p -> true);
        txtSearchAttendee.textProperty().addListener((obs, old, newValue) -> {
            filteredData.setPredicate(attendee -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String filter = newValue.toLowerCase();
                return attendee.getUserName().toLowerCase().contains(filter) ||
                       attendee.getEventTitle().toLowerCase().contains(filter);
            });
        });
        tblAttendance.setItems(filteredData);
    }

    private void setupPermissionsTable() {
        colUserName.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCurrentRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // ROLE ACTION COLUMN: For ComboBox + Update Button
        colRoleAction.setCellFactory(param -> new TableCell<UserModel, Void>() {
            private final ComboBox<UserRole> roleCombo = new ComboBox<>(
                    FXCollections.observableArrayList(UserRole.values()));
            private final Button updateBtn = new Button("Update");
            private final HBox container = new HBox(5, roleCombo, updateBtn);

            {
                updateBtn.setOnAction(event -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    UserRole newRole = roleCombo.getValue();
                    if (newRole != null && user != null) {
                        if (eventDAO.updateUserRole(user.getId(), newRole)) {
                            loadUserData(); // Refresh to show changes
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    UserModel user = getTableView().getItems().get(getIndex());
                    roleCombo.setValue(user.getRole()); // Set current role
                    setGraphic(container);
                }
            }
        });
        tblUsers.setItems(userData);
    }

    private void loadUserData() {
        userData.clear();
        userData.addAll(eventDAO.getAllUsers());
    }

    @FXML
    private void loadAttendeeData() {
        attendeeData.clear();
        attendeeData.addAll(eventDAO.getPendingCheckIns());
    }

    private void handleCheckIn(AttendeeView attendee) {
        if (eventDAO.updateAttendance(attendee.getRegistrationId(), "CHECKED_IN")) {
            loadAttendeeData();
        }
    }
}