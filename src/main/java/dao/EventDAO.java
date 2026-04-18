package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.*;
import model.EventModel;

public class EventDAO {
    public void addEvent(EventModel event) {
        String sql = "INSERT INTO events (name, date) VALUES (?, ?)";

        //auto close everything inside
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDate());
            pstmt.executeUpdate();
        
        }catch (SQLException e) {
            // exception handling report to ui via controller or console
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Database Connection Failed!", e);
            
        }
    }

    public List<EventModel> getAllEvents() {
        // JDBC code: "SELECT * FROM events"
        return List.of(new EventModel("Concert"), new EventModel("Tech Talk")); 
    }
}