package ui;

import dao.EventDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.EventModel;
import utils.UserSession;
import java.util.List;

public class CartController {

    @FXML private VBox cartItemsContainer; // The VBox from your screenshot

    private EventDAO eventDAO = new EventDAO();

    @FXML
    public void initialize() {
        loadCartItems();
    }

    private void loadCartItems() {
        cartItemsContainer.getChildren().clear();
        int userId = UserSession.getInstance().getUser().getid();
        
        // You'll need a method in EventDAO that returns events for a specific user
        List<EventModel> paymentPendingEvents = eventDAO.getPaymentPendingUserID(userId);

        for (EventModel event : paymentPendingEvents) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CartItem.fxml"));
                Node itemNode = loader.load();

                // Get labels from the template and set text
                Label title = (Label) itemNode.lookup("#lblEventTitle");
                Label date = (Label) itemNode.lookup("#lblDate");
                
                title.setText(event.getTitle());
                date.setText(event.getDate());

                cartItemsContainer.getChildren().add(itemNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCheckout() {
        // We get a reference to the MainController to use its loadView method
        try {
            // Find the MainController (assuming it's the parent of this view)
            // Or simpler: Use the loadView logic you already have in MainController
            Stage stage = (Stage) cartItemsContainer.getScene().getWindow();
            MainController main = (MainController) stage.getUserData(); 
            // Note: If you haven't set UserData, you can also use your existing
            // MainController instance if you passed it in.
            
            if(main != null) {
                main.loadView("/Checkout.fxml");
            } else {
                System.out.println("MainController reference none. Cannot load checkout view.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}