package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import 

public class EventDAO {
    public void addEvent(Event event) {
        String sql = "INSERT INTO events (name, date) VALUES (?, ?)";

        //auto close everything inside
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDate());
            pstmt.executeUpdate();
        
        }catch (SQLException e) {
            // exception handling report to ui via controller or console
            
        }
    }
}