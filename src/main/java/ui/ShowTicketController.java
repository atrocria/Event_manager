package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.UserModel;
import utils.UserSession;
import java.util.List;

public class ShowTicketController {

    @FXML private TableView<TicketRow> tblTickets;
    @FXML private TableColumn<TicketRow, String> colEvent;
    @FXML private TableColumn<TicketRow, Integer> colBookingId;
    @FXML private TableColumn<TicketRow, String> colSeat;
    @FXML private TableColumn<TicketRow, Double> colPaid;
    @FXML private TableColumn<TicketRow, String> colStatus;

    private final EventDAO eventDAO = new EventDAO();
    private final UserModel currentUser = UserSession.getInstance().getUser();

    @FXML
    public void initialize() {
        // Properties MUST match the variable names in TicketRow below
        colEvent.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadUserTickets();
    }

    public static class TicketRow {
        private final int id;
        private final String eventTitle;
        private final String seatNumber;
        private final double amountPaid;
        private final String status;

        public TicketRow(int id, String eventTitle, String seatNumber, double amountPaid, String status) {
            this.id = id;
            this.eventTitle = eventTitle;
            this.seatNumber = seatNumber;
            this.amountPaid = amountPaid;
            this.status = status;
        }

        // Getters used by PropertyValueFactory
        public int getId() { return id; }
        public String getEventTitle() { return eventTitle; }
        public String getSeatNumber() { return seatNumber; }
        public double getAmountPaid() { return amountPaid; }
        public String getStatus() { return status; }
    }

    private void loadUserTickets() {
        if (currentUser == null) return;
        tblTickets.getItems().clear();
        
        // Fetch rows directly from the DAO
        List<TicketRow> tickets = eventDAO.getDetailedTicketsByUserId(currentUser.getId());
        tblTickets.getItems().addAll(tickets);
    }
}