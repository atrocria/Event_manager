package ui;

import model.UserModel;
import utils.UserSession;

public class LoginController {
    // Inside your LoginController.java
    public void handleLogin() {
        UserModel user = userDAO.authenticate(email, password);
        if (user != null) {
            // This is the "link" - storing the user globally
            UserSession.getInstance().login(user); 
            
            // Then switch to the Event Page
            switchScene("/ui/eventpage.fxml");
        }
    }
}
