package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public abstract class EventModel {
    private int id;
    private String title;
    private String description;
    private String date;
    private String type;
    private int venue;
    private String status;
    private LocalDateTime creationTime;
    private String registrationDeadLine;
    private int organizer;
    private LocalDateTime startTime;
    private int durationMin;
    private int max_attendees;
    private List<UserModel> attendees = new ArrayList<>(); // Initialize to avoid NullPointerException
    private double basePrice;

    public EventModel() {}

    public EventModel(int id, String title, String description, int venue, String date, LocalDateTime startTime, int organizer, int durationMin, String registrationDeadLine, int max_attendees, String status, LocalDateTime creationTime, 
            String type, List<UserModel> attendees, double basePrice) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.date = date;
        this.startTime = startTime;
        this.organizer = organizer;
        this.durationMin = durationMin;
        this.registrationDeadLine = registrationDeadLine;
        this.max_attendees = max_attendees;
        this.status = status;
        this.creationTime = creationTime;
        this.type = type;
        this.attendees = (attendees != null) ? attendees : new ArrayList<>();
        this.basePrice = basePrice;
    }
    
    // --- CORE LOGIC METHODS ---

    /**
     * Requirement: Ensure tickets are not oversold.
     * Returns how many tickets are actually left.
     */
    public int getRemainingCapacity() {
        return max_attendees - getRegisteredAttendees();
    }

    /**
     * Requirement: Real-time ticket sales tracking.
     */
    public int getRegisteredAttendees() {
        return (attendees != null) ? attendees.size() : 0;
    }

    /**
     * Checks if the event is full.
     */
    public boolean isFull() {
        return getRegisteredAttendees() >= max_attendees;
    }

    /**
     * Requirement: Ticket Classes (VIP, Early Bird, etc.)
     * This default version handles basic logic; ConcertEvent will override this.
     */
    // In EventModel.java
    public double calculateTicketPrice(String ticketType) {
        return getBasePrice(); // Default behavior: ignore the type, return base
    }

    // --- GETTERS ---
    public int getID() {return id;}
    public String getTitle() {return title;}
    public String getDate() {return date;}
    public int getVenue() {return venue;}
    public LocalDateTime getStartTime() {return startTime;}
    public int getOrganizer() {return organizer;}
    public int getDurationMin() {return durationMin;}
    public int getMax_attendees() {return max_attendees;}
    public List<UserModel> getAttendees() {return attendees;}
    public String getDescription() {return description;}
    public String getRegistrationDeadLine() {return registrationDeadLine;}
    public String getStatus() {return status;}
    public String getType() {return type;}
    public LocalDateTime getCreationTime() {return creationTime;}
    public double getBasePrice() { return basePrice; }

    // --- SETTERS ---
    public void setID(int var1) {this.id = var1;}
    public void setTitle(String var1) {this.title = var1;}
    public void setDate(String var1) {this.date = var1;}
    public void setVenue(int var1) {this.venue = var1;}
    public void setStartTime(LocalDateTime var1) {this.startTime = var1;}
    public void setOrganizer(int var1) {this.organizer = var1;}
    public void setDurationMin(int var1) {this.durationMin = var1;}
    public void setMax_attendees(int var1) {this.max_attendees = var1;}
    public void setAttendees(List<UserModel> var1) {this.attendees = var1;}
    public void setDescription(String var1) {this.description = var1;}
    public void setRegistrationDeadLine(String var1) {this.registrationDeadLine = var1;}
    public void setStatus(String var1) {this.status = var1;}
    public void setType(String var1) {this.type = var1;}
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setCreationTime(LocalDateTime var1) {this.creationTime = var1;}

    public LocalDateTime getEndTime() {
        if (startTime == null) return null;
        return startTime.plusMinutes(durationMin);
    }
}