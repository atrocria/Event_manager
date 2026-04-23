package ui;

import dao.EventDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.EventModel;
import model.UserModel;
import utils.UserSession;
import java.util.List;

public class ShowTicketController {

    @FXML private TableView<TicketRow> tblTickets;
    @FXML private TableColumn<TicketRow, String> colEvent;
    @FXML private TableColumn<TicketRow, String> colBookingId;
    @FXML private TableColumn<TicketRow, String> colSeat;
    @FXML private TableColumn<TicketRow, String> colStatus;
    @FXML private TableColumn<TicketRow, Double> colPaid;

    private final EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        // Link columns to TicketRow properties
        colEvent.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));

        loadUserTickets();
    }

    private void loadUserTickets() {
        UserModel currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) return;

        // Fetching data to satisfy the "ticket tab" and "count money" cards
        List<TicketRow> tickets = eventDAO.getDetailedTicketsByUserId(currentUser.getid());
        tblTickets.setItems(FXCollections.observableArrayList(tickets));
    }

    // Helper class for the TableView rows
    public static class TicketRow {
        private final String eventName;
        private final String bookingId;
        private final String seatNumber;
        private final String paymentStatus;
        private final double amountPaid;

        public TicketRow(String eventName, String bookingId, String seatNumber, String paymentStatus, double amountPaid) {
            this.eventName = eventName;
            this.bookingId = bookingId;
            this.seatNumber = seatNumber;
            this.paymentStatus = paymentStatus;
            this.amountPaid = amountPaid;
        }

        public String getEventName() { return eventName; }
        public String getBookingId() { return bookingId; }
        public String getSeatNumber() { return seatNumber; }
        public String getPaymentStatus() { return paymentStatus; }
        public double getAmountPaid() { return amountPaid; }
    }
}