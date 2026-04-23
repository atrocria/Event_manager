package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.EventModel;
import utils.UserSession;
import java.util.List;

public class CartController {

    @FXML private VBox cartItemsContainer;
    @FXML private VBox summaryItemsList;
    @FXML private Label lblItemCount;
    @FXML private Label lblSubtotal;

    private EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        refreshCart();
    }

    private void refreshCart() {
        cartItemsContainer.getChildren().clear();
        summaryItemsList.getChildren().clear();
        
        int userId = UserSession.getInstance().getUser().getid();
        List<EventModel> events = eventDAO.getEventsWithPendingPayment(userId);

        if (lblItemCount != null) {
            lblItemCount.setText("You have " + events.size() + " items in your cart");
        }

        double total = 0;

        for (EventModel event : events) {
            addEventToMainList(event);
            addEventToSummary(event);
            total += event.calculateTicketPrice();
        }

        lblSubtotal.setText(String.format("$%.2f", total));
    }

    private void addEventToMainList(EventModel event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CartItem.fxml"));
            Node itemNode = loader.load();

            CartItemController itemController = loader.getController();
            // Using a lambda to refresh the whole cart if an item is removed
            itemController.setData(event, () -> refreshCart());

            cartItemsContainer.getChildren().add(itemNode);
        } catch (Exception e) {
            System.err.println("Error loading CartItem.fxml: " + e.getMessage());
        }
    }

    private void addEventToSummary(EventModel event) {
        HBox row = new HBox();
        row.setSpacing(10);
        
        Label name = new Label(event.getTitle());
        name.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        name.setMaxWidth(160);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label price = new Label(String.format("$%.2f", event.calculateTicketPrice()));
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        row.getChildren().addAll(name, spacer, price);
        summaryItemsList.getChildren().add(row);
    }

    @FXML
    private void handleCheckout() {
        try {
            Stage stage = (Stage) cartItemsContainer.getScene().getWindow();
            MainController main = (MainController) stage.getUserData(); 
            
            if (main != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Checkout.fxml"));
                Parent checkoutView = loader.load();
                
                CheckoutController checkoutCtrl = loader.getController();
                
                // Fetch items and pass them to the checkout controller
                int userId = UserSession.getInstance().getUser().getid();
                List<EventModel> events = eventDAO.getEventsWithPendingPayment(userId);
                checkoutCtrl.prepareCheckout(events);
                
                main.setCenterView(checkoutView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}