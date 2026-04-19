package dao;

import model.UserModel;
import model.UserRole;
import java.sql.*;

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
}