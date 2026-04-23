package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.*;

import model.AttendeeView;
import model.ConcertEvent;
import model.ConferenceEvent;
import model.WorkshopEvent;
import ui.ShowTicketController;
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

        // 1. Security Check
        if (user == null || user.getrole().getLevel() < 60) {
            System.err.println("SECURITY ALERT: Unauthorized attempt to add event.");
            return;
        }

        // 2. Venue Capacity Check (Linking up with Venue table)
        if (!isVenueBigEnough(event.getVenue(), event.getMax_attendees())) {
            System.err.println("❌ Error: Event capacity exceeds venue limit.");
            // Optional: Throw an exception here so the UI can show an alert
            return;
        }

        String sql = "INSERT INTO event (title, description, event_date, start_time, venue_id, " +
                    "organizer_id, max_attendees, registration_deadline, status, type, " +
                    "performer, research_topic, keynote_speaker, material_list, duration_min, base_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Standard Fields
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getDate());
            
            if (event.getStartTime() != null) {
                pstmt.setTime(4, java.sql.Time.valueOf(event.getStartTime().toLocalTime()));
            } else {
                pstmt.setNull(4, java.sql.Types.TIME);
            }

            pstmt.setInt(5, event.getVenue());
            pstmt.setInt(6, event.getOrganizer());
            pstmt.setInt(7, event.getMax_attendees());
            pstmt.setString(8, event.getRegistrationDeadLine() != null ? event.getRegistrationDeadLine() : event.getDate());
            pstmt.setString(9, event.getStatus());
            pstmt.setString(10, event.getType());

            // --- DYNAMIC SUBCLASS MAPPING (FIXED) ---
            // Initialize all as null first
            pstmt.setNull(11, java.sql.Types.VARCHAR); // performer
            pstmt.setNull(12, java.sql.Types.VARCHAR); // research_topic
            pstmt.setNull(13, java.sql.Types.VARCHAR); // keynote_speaker
            pstmt.setNull(14, java.sql.Types.VARCHAR); // material_list

            if (event instanceof ConcertEvent concert) {
                pstmt.setString(11, concert.getArtistName());
            } else if (event instanceof ConferenceEvent conf) {
                pstmt.setString(12, conf.getResearchTopic());
                pstmt.setString(13, conf.getKeynoteSpeaker());
            } else if (event instanceof WorkshopEvent workshop) {
                // Check if research_topic column is used for discussion topics in your DB
                pstmt.setString(12, workshop.getDiscussionTopics()); 
                pstmt.setString(14, workshop.getMaterialList());
            }

            pstmt.setInt(15, event.getDurationMin() > 0 ? event.getDurationMin() : 60);
            pstmt.setDouble(16, event.getBasePrice());

            pstmt.executeUpdate();
            System.out.println("✅ Event successfully saved: " + event.getTitle());

        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "❌ SQL Error: Failed to save event!", e);
        }
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        String query = "SELECT id, email, role FROM users";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new UserModel(
                    rs.getInt("id"),
                    rs.getString("email"), 
                    UserRole.valueOf(rs.getString("role")), null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
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

    public List<EventModel> getEventsByUserId(int userId) {
        List<EventModel> events = new ArrayList<>();
        String sql = "SELECT e.* FROM event e JOIN registration r ON e.event_id = r.event_id WHERE r.user_id = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            // ce.setGenre(GenreModel.valueOf(rs.getString("genre")));
            event = ce;
        } else if ("CONFERENCE".equalsIgnoreCase(type)) {
            ConferenceEvent cfe = new ConferenceEvent();
            cfe.setResearchTopic(rs.getString("research_topic"));
            cfe.setKeynoteSpeaker(rs.getString("keynote_speaker"));
            event = cfe; // Don't forget this assignment!
        } else if ("WORKSHOP".equalsIgnoreCase(type)) {
            WorkshopEvent ws = new WorkshopEvent();
            ws.setDiscussionTopics(rs.getString("research_topic"));
            ws.setMaterialList(rs.getString("material_list"));
            event = ws;
        } else {
            // illegal ahh
            WorkshopEvent ws = new WorkshopEvent();
            event = ws;
        }

        // Set Common Fields (Mapping DB columns to Model setters)
        event.setID(rs.getInt("event_id"));
        event.setTitle(rs.getString("title"));
        event.setDate(rs.getString("event_date"));
        event.setVenue(rs.getInt("venue_id"));

        if (rs.getTime("start_time") != null) {
            LocalDate datePart = rs.getDate("event_date").toLocalDate();
            LocalTime timePart = rs.getTime("start_time").toLocalTime();
            event.setStartTime(LocalDateTime.of(datePart, timePart));
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            // Assuming your setter is named setDatePosted
            event.setCreationTime(createdAt.toLocalDateTime());
        }

        event.setOrganizer(rs.getInt("organizer_id"));
        event.setMax_attendees(rs.getInt("max_attendees"));
        event.setRegistrationDeadLine(rs.getString("registration_deadline"));
        event.setStatus(rs.getString("status"));
        event.setDurationMin(rs.getInt("duration_min")); // Fixed
        event.setDescription(rs.getString("description")); // Fixed
        event.setType(type);
        event.setBasePrice(rs.getDouble("base_price")); // set price

        int eventId = rs.getInt("event_id");
        event.setAttendees(getAttendeesForEvent(eventId));

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
                    UserRole.valueOf(rs.getString("role").toUpperCase()), // Convert DB string to Enum
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                attendees.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendees;
    }

    public List<String> getRegisteredUserNames(int eventId) {
        List<String> names = new ArrayList<>();
        String sql = "SELECT u.name FROM user u JOIN registration r ON u.user_id = r.user_id WHERE r.event_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return names;
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

    // from bookticket button, register user for event, insert into registration table
    public boolean isUserRegistered(int userId, int eventId) {
        // Note: Using 'registration' to match your other method's table name
        String sql = "SELECT COUNT(*) FROM registration WHERE user_id = ? AND event_id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "Check registration failed", e);
        }
        return false;
    }

    // Updated version of your existing method to include a safety check
    public String registerUserForEvent(int userId, EventModel event, String ticketType) {
        // 1. Basic Checks using your existing wrapper methods
        if (isUserRegistered(userId, event.getID())) return "ALREADY_REGISTERED";
        if (event.getAttendees().size() >= event.getMax_attendees()) return "EVENT_FULL";

        // 2. Assign Seat and Generate Booking ID
        String assignedSeat = "S-" + (event.getAttendees().size());
        String bookingCode = "BK-" + event.getID() + "-" + (System.currentTimeMillis() % 10000);
        
        // 3. Calculate Price (Calling the logic inside your ConcertEvent/etc.)
        double finalPrice = event.calculateTicketPrice(ticketType);

        // 4. Final SQL Insert
        String sql = "INSERT INTO registration (user_id, event_id, ticket_type, seat_number, booking_code, amount_paid) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, event.getID());
            pstmt.setString(3, (ticketType != null) ? ticketType : "STANDARD");
            pstmt.setString(4, assignedSeat);
            pstmt.setString(5, bookingCode);
            pstmt.setDouble(6, finalPrice); // Captures the exact money paid!

            pstmt.executeUpdate();
            return "SUCCESS:" + assignedSeat + ":" + bookingCode;
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public List<ShowTicketController.TicketRow> getDetailedTicketsByUserId(int userId) {
        List<ShowTicketController.TicketRow> list = new ArrayList<>();
        String sql = "SELECT e.title, r.booking_id, r.seat_number, r.payment_status, r.amount_paid " +
                    "FROM registration r JOIN event e ON r.event_id = e.event_id " +
                    "WHERE r.user_id = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ShowTicketController.TicketRow(
                    rs.getString("title"),
                    rs.getString("booking_id"),
                    rs.getString("seat_number"),
                    rs.getString("payment_status"),
                    rs.getDouble("amount_paid")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean isVenueBigEnough(int venueId, int requestedMaxAttendees) {
        String sql = "SELECT capacity FROM venue WHERE venue_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, venueId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("capacity") >= requestedMaxAttendees;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public double getTotalRevenueForEvent(int eventId) {
        String sql = "SELECT SUM(amount_paid) FROM registration WHERE event_id = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // Fetch list of people who PAID but haven't CHECKED_IN
    public List<AttendeeView> getPendingCheckIns() {
        List<AttendeeView> list = new ArrayList<>();
        // Added r.payment_status to the SELECT
        String sql = "SELECT r.registration_id, u.name, e.title, r.attendance_status, r.payment_status " +
                "FROM registration r " +
                "JOIN user u ON r.user_id = u.user_id " +
                "JOIN event e ON r.event_id = e.event_id";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new AttendeeView(
                        rs.getInt("registration_id"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getString("attendance_status"),
                        rs.getString("payment_status") // Pass it to constructor
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // THIS UPDATES THE TABLE FROM YOUR IMAGE
    public boolean updateAttendance(int registrationId, String status) {
        String sql = "UPDATE registration SET attendance_status = ? WHERE registration_id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, registrationId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // THIS GETS PEOPLE WHO PAID FOR YOUR STAFF TO SEE
    // We return a List of String arrays since you don't have a RegistrationModel
    public List<String[]> getStaffCheckInList() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT r.registration_id, u.name, e.title, r.attendance_status " +
                "FROM registration r " +
                "JOIN user u ON r.user_id = u.user_id " +
                "JOIN event e ON r.event_id = e.event_id " +
                "WHERE r.payment_status = 'PAID'";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                        String.valueOf(rs.getInt("registration_id")),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getString("attendance_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void updateUserRole(int userId, UserRole newRole) {
        String sql = "UPDATE user SET role = ? WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole.name()); // Sets role to 'STAFF', 'ADMIN', etc.
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EventModel> getEventsWithPendingPayment(int userId) {
        List<EventModel> events = new ArrayList<>();
        // We join the event table with registration to get the event details
        String sql = "SELECT e.* FROM event e " +
                "JOIN registration r ON e.event_id = r.event_id " +
                "WHERE r.user_id = ? AND r.payment_status = 'PENDING'";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Use your existing mapping helper to turn the DB row into an object
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "Failed to fetch cart items", e);
        }
        return events;
    }

    public boolean completeUserPayment(int userId) {
        // Looking at your phpMyAdmin screenshot, we target the 'registration' table
        String sql = "UPDATE registration SET payment_status = 'PAID' WHERE user_id = ? AND payment_status = 'PENDING'";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(int eventId) {
        UserModel user = UserSession.getInstance().getUser();

        // Security Guard: double check role level
        if (user == null || user.getrole().getLevel() < 100) { // Assuming 100 is Admin
            System.err.println("SECURITY ALERT: Unauthorized attempt to delete event.");
            return false;
        }

        String deleteRegistrationsSql = "DELETE FROM registration WHERE event_id = ?";
        String deleteEventSql = "DELETE FROM event WHERE event_id = ?";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // Step 1: Clear out registrations first to satisfy Foreign Key constraints
            try (PreparedStatement pstmtReg = conn.prepareStatement(deleteRegistrationsSql)) {
                pstmtReg.setInt(1, eventId);
                pstmtReg.executeUpdate();
            }

            // Step 2: Delete the actual event
            try (PreparedStatement pstmtEvent = conn.prepareStatement(deleteEventSql)) {
                pstmtEvent.setInt(1, eventId);
                int affectedRows = pstmtEvent.executeUpdate();

                if (affectedRows > 0) {
                    conn.commit(); // Save changes
                    System.out.println("✅ Event " + eventId + " and all related registrations deleted.");
                    return true;
                }
            }

            conn.rollback(); // Rollback if something went wrong
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            Logger.getLogger(EventDAO.class.getName()).log(Level.SEVERE, "❌ Delete Failed!", e);
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}