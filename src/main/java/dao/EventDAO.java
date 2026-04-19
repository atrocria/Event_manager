package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.*;

import model.ConcertEvent;
import model.ConferenceEvent;
import model.WorkshopEvent;
import model.EventModel;
import model.UserModel;
import model.UserRole;

public class EventDAO {
    
    // User role
    public UserRole findHighestRole(List<UserRole> roles) {
        return roles.stream()
                    .max(Comparator.comparingInt(UserRole::getLevel))
                    .orElse(UserRole.ATTENDEE);
    }

    public void addEvent(EventModel event) {
        String sql = "INSERT INTO events (name, date) VALUES (?, ?)";

        //auto close everything inside
        try (Connection conn = Database.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDate());
            pstmt.executeUpdate();
        
        }catch (SQLException e) {
            // exception handling report to ui via controller or console
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Database Connection Failed!", e);
        }
    }

    // to get all info from database, for eventpage
   public List<EventModel> getAllEvents() {
        List<EventModel> events = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                // of event database "type" discriminator
                String type = rs.getString("type");
                EventModel event;

                // Logic to handle different event subclasses
                if ("CONCERT".equalsIgnoreCase(type)) {
                    ConcertEvent ce = new ConcertEvent();
                    ce.setPerformer(rs.getString("performer"));
                    event = ce;
                } else if ("CONFERENCE".equalsIgnoreCase(type)) {
                    ConferenceEvent cfe = new ConferenceEvent();
                    cfe.setResearchTopic(rs.getString("research_topic"));
                    cfe.setKeynoteSpeaker(rs.getString("keynote_speaker"));
                } else if ("WORKSHOP".equalsIgnoreCase(type)) {
                    WorkshopEvent ws = new WorkshopEvent();
                    ws.setDiscussionTopics(rs.getString("discussion_topics"));
                    event = ws;
                } else {
                    // fallback
                    event = new EventModel();
                }

                // Set common fields
                event.setID(rs.getInt("event_id")); // int
                event.setTitle(rs.getString("title")); // 'title' in DB, 'name' in Model
                event.setDate(rs.getString("event_date"));
                event.setStartTime(rs.getTimestamp("start_time").toLocalDateTime()); 
                event.setOrganizer(String.valueOf(rs.getInt("organizer_id")));
                event.setMax_attendees(rs.getInt("max_attendees"));
                event.setRegistrationDeadLine(rs.getString("registration_deadline"));
                event.setStatus(rs.getString("status"));
                event.setCreationTime(rs.getTimestamp("created_at").toString());

                // 11. Type (Useful for logic later)
                event.setType(rs.getString("type"));
                event.setVenue(rs.getVenue());
                event.setDurationMin(rs.)
                event.setAttendees(rs.)
                event.setDescription(rs.)
                
                // add everything back into a list to display from the ui
                events.add(event);
            }
        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "Fetch Failed", e);
        }
        return events;
    }

    public List<UserModel> getAttendeesForEvent(int eventId) {
        List<UserModel> attendees = new ArrayList<>();
        // Join the users table with the registrations table
        String sql = "SELECT u.* FROM users u " +
                    "JOIN event_registrations r ON u.user_id = r.user_id " +
                    "WHERE r.event_id = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Create the UserModel using your constructor: (id, name, email, role)
                UserModel user = new UserModel(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    UserRole.valueOf(rs.getString("role")) // Convert DB string to your Enum
                );
                attendees.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendees;
    }
    
    // from searcher inside eventpagecontroller, search via string
    public List<EventModel> getEvents(String search) {
        List<EventModel> events = new ArrayList<>();
        // Search in title OR description
        String sql = "SELECT * FROM event WHERE title LIKE ? OR description LIKE ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // The % is a wildcard for SQL
            String query = "%" + search + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Use the same logic from your getAllEvents() to create 
                // the specific event types (Concert, Workshop, etc.)
                EventModel event = mapResultSetToEvent(rs); 
                events.add(event);
            }
        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "Search Failed", e);
        }
        return events;
    }

    // Pro-tip: Move your rs.get... logic into a private helper method 
    // so both getAllEvents() and getEvents() can use it without repeating code.

}