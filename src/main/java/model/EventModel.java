package model;

import java.time.LocalDateTime;
import java.util.List;

// base event
public abstract class EventModel {
    private int id;
    private String title;
    private String description;
    private String date;
    private String type;
    private int venue;
    private String status;
    private String creationTime;
    private String registrationDeadLine;
    private String organizer;
    private LocalDateTime startTime;
    private int durationMin;
    private int max_attendees;
    private List<UserModel> attendees;

    public EventModel() {}

    public EventModel(int id, String title, String description, int venue, String date, LocalDateTime startTime, String organizer, int durationMin, String registrationDeadLine, int max_attendees, String status, String creationTime, 
            String type, List<UserModel> attendees) {
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
        this.attendees = attendees;
    }
    
    // Getters
    public int getID() {return id;}
    public String getTitle() {return title;}
    public String getDate() {return date;}
    public int getVenue() {return venue;}
    public LocalDateTime getStartTime() {return startTime;}
    public String getOrganizer() {return organizer;}
    public int getDurationMin() {return durationMin;}
    public int getMax_attendees() {return max_attendees;}
    public List<UserModel> getAttendees() {return attendees;}
    public String getDescription() {return description;}
    public String getRegistrationDeadLine() {return registrationDeadLine;}
    public String getStatus() {return status;}
    public String getType() {return type;}
    public String getCreationTime() {return creationTime;}
    
    // setters
    public void setID(int var1) {this.id = var1;}
    public void setTitle(String var1) {this.title = var1;}
    public void setDate(String var1) {this.date = var1;}
    public void setVenue(int var1) {this.venue = var1;}
    public void setStartTime(LocalDateTime var1) {this.startTime = var1;}
    public void setOrganizer(String var1) {this.organizer = var1;}
    public void setDurationMin(int var1) {this.durationMin = var1;}
    public void setMax_attendees(int var1) {this.max_attendees = var1;}
    public void setAttendees(List<UserModel> var1) {this.attendees = var1;}
    public void setDescription(String var1) {this.description = var1;}
    public void setRegistrationDeadLine(String var1) {this.registrationDeadLine = var1;}
    public void setStatus(String var1) {this.status = var1;}
    public void setType(String var1) {this.type = var1;}

    public void setCreationTime(String var1) {this.creationTime = var1;}
    public LocalDateTime getEndTime(int durationMin) {
        if (startTime == null)
            return null;
        return startTime.plusMinutes(durationMin);
    }

    public double calculateTicketPrice() {
        //! calculate real price
        return 100;
    }
}