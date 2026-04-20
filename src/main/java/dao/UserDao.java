package dao;

import model.UserModel;
import model.UserRole;
import java.sql.*;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UserDao {

    public static UserModel authenticate(String email, String password) {
        String sql = "SELECT * FROM user WHERE email = ?"; // Only search by email

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPasswordInDb = rs.getString("password");

                // Check if the plain text input matches the stored hash
                if (BCrypt.checkpw(password, hashedPasswordInDb)) {
                    UserRole role = UserRole.valueOf(rs.getString("role").toUpperCase());

                    return new UserModel(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean register(String name, String email, String password, UserRole role) {
        String sql = "INSERT INTO user (name, email, password, role) VALUES (?, ?, ?, ?)";

        // Use the SAME Database helper your EventDAO uses
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, role.name()); //! one account can have multiple roles

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new java.util.ArrayList<>();
        String sql = "SELECT user_id, name, email, role FROM user";

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserRole role = UserRole.valueOf(rs.getString("role").toUpperCase());
                users.add(new UserModel(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<UserModel> getUsers(String search) {
        List<UserModel> users = new java.util.ArrayList<>();
        // Search by name OR email using LIKE
        String sql = "SELECT user_id, name, email, role FROM user WHERE name LIKE ? OR email LIKE ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                UserRole role = UserRole.valueOf(rs.getString("role").toUpperCase());
                users.add(new UserModel(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Convert the String ID to an Integer for the database
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            
            // If rowsAffected > 0, the user was successfully deleted
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid ID format: " + id);
            return false;
        }
    }
}