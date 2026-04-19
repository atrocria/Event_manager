package model;

import java.time.LocalDateTime;
import java.util.List;

// base event
public abstract class EventModel {
    private int id;
    private String title;
    private String description;
    private String date;
    private String venue; //todo make a venue class to change?
    private String status;
    private String creationTime;
    private String registrationDeadLine;
    private LocalDateTime startTime;
    private String organizer;
    private int durationMin;
    private int max_attendees;
    private List<UserModel> attendees;

    public EventModel(int id, String title, String description, String venue, String date, LocalDateTime startTime, String organizer, int durationMin, String registrationDeadLine, int max_attendees, String status, String creationTime, List<UserModel> attendees) {
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
        this.attendees = attendees;
    }
    
    // Standard Getters and Setters
    public int getID() {return id;}
    public void setID(int var1) {this.id = var1;}
    public String getTitle() {return title;}
    public void setTitle(String var1) {this.title = var1;}
    public String getDate() {return date;}
    public void setDate(String var1) {this.date = var1;}
    public String getVenue() {return venue;}
    public void setVenue(String var1) {this.venue = var1;}
    public LocalDateTime getStartTime() {return startTime;}
    public void setStartTime(LocalDateTime var1) {this.startTime = var1;}
    public String getOrganizer() {return organizer;}
    public void setOrganizer(String var1) {this.organizer = var1;}
    public int getDurationMin() {return durationMin;}
    public void setDurationMin(int var1) {this.durationMin = var1;}
    public int getMax_attendees() {return max_attendees;}
    public void setMax_attendees(int var1) {this.max_attendees = var1;}
    public List<UserModel> getAttendees() {return attendees;}
    public void setAttendees(List<UserModel> var1) {this.attendees = var1;}
    public String getDescription() {return description;}
    public void setDescription(String var1) {this.description = var1;}
    public String getRegistrationDeadLine() {return registrationDeadLine;}
    public void setRegistrationDeadLine(String var1) {this.registrationDeadLine = var1;}
    public String getStatus() {return status;}
    public void setStatus(String var1) {this.status = var1;}
    public String getCreationTime() {return creationTime;}
    public void setCreationTime(String var1) {this.creationTime = var1;}
    
    public LocalDateTime getEndTime(int durationMin) {
        if (startTime == null)
            return null;
        return startTime.plusMinutes(durationMin);
    }

    // public double calculateTicketPrice(){
    //     return
    // }
}
