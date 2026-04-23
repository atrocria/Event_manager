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
    @FXML
    private TextField txtSearchAttendee;
    @FXML
    private TableView<AttendeeView> tblAttendance;
    @FXML
    private TableColumn<AttendeeView, String> colUser;
    @FXML
    private TableColumn<AttendeeView, String> colEvent;
    @FXML
    private TableColumn<AttendeeView, String> colStatus;
    @FXML
    private TableColumn<AttendeeView, Void> colAction;

    // Permissions Tab
    @FXML private TableView<UserModel> tblUsers;
    @FXML private TableColumn<UserModel, String> colUserName;
    @FXML private TableColumn<UserModel, UserRole> colCurrentRole;
    @FXML private TableColumn<UserModel, Void> colRoleAction;
    @FXML private TableColumn<AttendeeView, String> colPaymentStatus;

    private final EventDAO eventDAO = new EventDAO();
    private ObservableList<AttendeeView> attendeeData = FXCollections.observableArrayList();
    private ObservableList<UserModel> userData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupAttendanceTable();
        setupPermissionsTable();
        loadAttendeeData();
        loadUserData(); // You'll need a method in DAO to get all users
    }

    private void setupAttendanceTable() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colEvent.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPaymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // Create the "Check-In" Button in the column
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
                    UserModel user = getTableView().getItems().get(getIndex());
                    // This ensures the ComboBox shows the user's current role
                    roleCombo.setValue(user.getRole()); 
                    setGraphic(container);
                }
            }
        });

        
        // Search Filter Logic
        FilteredList<AttendeeView> filteredData = new FilteredList<>(attendeeData, p -> true);
        txtSearchAttendee.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(attendee -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return attendee.getUserName().toLowerCase().contains(lowerCaseFilter) ||
                        attendee.getEventTitle().toLowerCase().contains(lowerCaseFilter);
            });
        });
        tblAttendance.setItems(filteredData);
    }
    
    private void loadUserData() {
        userData.clear();
        // Assuming your eventDAO has a method to get all users
        userData.addAll(eventDAO.getAllUsers()); 
        tblUsers.setItems(userData);
    }

    @FXML
    private void loadAttendeeData() {
        attendeeData.clear();
        attendeeData.addAll(eventDAO.getPendingCheckIns());
    }

    private void handleCheckIn(AttendeeView attendee) {
        boolean success = eventDAO.updateAttendance(attendee.getRegistrationId(), "CHECKED_IN");
        if (success) {
            loadAttendeeData(); // Refresh table
            System.out.println("Check-in successful for: " + attendee.getUserName());
        }
    }

    private void setupPermissionsTable() {
        colUserName.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCurrentRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colRoleAction.setCellFactory(param -> new TableCell<UserModel, Void>() {
            private final ComboBox<UserRole> roleCombo = new ComboBox<>(
                    FXCollections.observableArrayList(UserRole.values()));
            private final Button updateBtn = new Button("Update");
            private final HBox container = new HBox(5, roleCombo, updateBtn);

            {
                updateBtn.setOnAction(event -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    UserRole newRole = roleCombo.getValue();
                    if (newRole != null) {
                        eventDAO.updateUserRole(user.getid(), newRole);
                        // Refresh data here
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }
}