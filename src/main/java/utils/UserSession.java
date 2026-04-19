package utils;

import model.UserModel;

public class UserSession {
    private static UserSession instance;
    private UserModel loggedInUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void login(UserModel user) { this.loggedInUser = user; }
    public UserModel getUser() { return loggedInUser; }
    public void logout() { loggedInUser = null; }
}