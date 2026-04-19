package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.*;

import model.ConcertEvent;
import model.ConferenceEvent;
import model.WorkshopEvent;
import utils.UserSession;
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
        UserModel user = UserSession.getInstance().getUser();

        // Security Check
        if (user == null || user.getrole().getLevel() < 60) {
            System.err.println("SECURITY ALERT: Unauthorized attempt to add event.");
            return;
        }

        // This SQL covers the core columns from your DB image
        String sql = "INSERT INTO event (title, description, event_date, start_time, venue_id, " +
                "organizer_id, max_attendees, registration_deadline, status, type, " +
                "performer, research_topic, keynote_speaker, material_list, duration_min) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. Common Fields
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getDate()); // event_date

            // Convert LocalDateTime to SQL Time for the 'start_time' column
            if (event.getStartTime() != null) {
                pstmt.setTime(4, java.sql.Time.valueOf(event.getStartTime().toLocalTime()));
            } else {
                pstmt.setNull(4, java.sql.Types.TIME);
            }

            pstmt.setInt(5, event.getVenue()); // venue_id
            pstmt.setInt(6, Integer.parseInt(event.getOrganizer())); // organizer_id
            pstmt.setInt(7, event.getMax_attendees());
            pstmt.setString(8, event.getRegistrationDeadLine());
            pstmt.setString(9, event.getStatus());
            pstmt.setString(10, event.getType());

            // 2. Specific Subclass Fields (The tricky part!)
            // Set everything to null by default
            pstmt.setNull(11, java.sql.Types.VARCHAR); // performer
            pstmt.setNull(12, java.sql.Types.VARCHAR); // research_topic
            pstmt.setNull(13, java.sql.Types.VARCHAR); // keynote_speaker

            // Fill in based on instance type
            if (event instanceof ConcertEvent) {
                pstmt.setString(11, ((ConcertEvent) event).getArtistName());
            } else if (event instanceof ConferenceEvent) {
                pstmt.setString(12, ((ConferenceEvent) event).getResearchTopic());
                pstmt.setString(13, ((ConferenceEvent) event).getKeynoteSpeaker());
            } else if (event instanceof WorkshopEvent) {
                // Assuming you add this field to your Workshop model later
                pstmt.setString(14, ((WorkshopEvent) event).getMaterialList());
            }

            pstmt.executeUpdate();
            System.out.println("Event successfully saved: " + event.getTitle());

        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "Failed to save event!", e);
        }
    }

    // to get all info from database, for eventpage
    public List<EventModel> getAllEvents() {
        List<EventModel> events = new ArrayList<>();
        String sql = "SELECT * FROM event";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "Fetch Failed", e);
        }
        return events;
    }

    private EventModel mapResultSetToEvent(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        EventModel event;

        // 1. Initialize the correct Subclass
        if ("CONCERT".equalsIgnoreCase(type)) {
            ConcertEvent ce = new ConcertEvent();
            ce.setArtistName(rs.getString("performer"));
            // Assuming Genre is stored as a String in DB, convert to GenreModel
            //! ce.setGenre(GenreModel.valueOf(rs.getString("genre")));
            event = ce;
        } else if ("CONFERENCE".equalsIgnoreCase(type)) {
            ConferenceEvent cfe = new ConferenceEvent();
            cfe.setResearchTopic(rs.getString("research_topic"));
            cfe.setKeynoteSpeaker(rs.getString("keynote_speaker"));
            event = cfe; // Don't forget this assignment!
        } else if ("WORKSHOP".equalsIgnoreCase(type)) {
            WorkshopEvent ws = new WorkshopEvent();
            ws.setDiscussionTopics(rs.getString("discussion_topics"));
            ws.setMaterialList(rs.getString("material_list"));
            event = ws;
        } else {
            // illegal ahh
            event = new WorkshopEvent();
            System.out.println("LOG: Found unknown type [" + type + "], treating as Workshop for now.");
        }

        // Set Common Fields (Mapping DB columns to Model setters)
        event.setID(rs.getInt("event_id"));
        event.setTitle(rs.getString("title"));
        event.setDate(rs.getString("event_date"));
        event.setVenue(rs.getInt("venue_id"));

        if (rs.getTimestamp("start_time") != null) {
            LocalDate datePart = rs.getDate("event_date").toLocalDate();
            LocalTime timePart = rs.getTime("start_time").toLocalTime();
            event.setStartTime(LocalDateTime.of(datePart, timePart));
        }

        event.setOrganizer(rs.getString("organizer_id"));
        event.setMax_attendees(rs.getInt("max_attendees"));
        event.setRegistrationDeadLine(rs.getString("registration_deadline"));
        event.setStatus(rs.getString("status"));
        event.setDurationMin(rs.getInt("duration_min")); // Fixed
        event.setDescription(rs.getString("description")); // Fixed
        event.setType(type);

        int eventId = rs.getInt("event_id");
        event.setAttendees(getAttendeesForEvent(eventId));

        // Note: Attendees are usually fetched via a separate JOIN query
        // or by calling your getAttendeesForEvent(event.getID()) method.

        return event;
    }

    public List<UserModel> getAttendeesForEvent(int eventId) {
        List<UserModel> attendees = new ArrayList<>();
        // Join the users table with the registrations table
        String sql = "SELECT u.* FROM user u " +
                "JOIN registration r ON u.user_id = r.user_id " +
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
                    UserRole.valueOf(rs.getString("role").toUpperCase()) // Convert DB string to your Enum
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