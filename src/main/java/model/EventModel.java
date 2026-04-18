package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

// base event
public abstract class EventModel {
    private final int id;
    private final String name;
    private final String description;
    private final Date date;
    private final String venue; //todo make a venue class to change?
    private final LocalDateTime startTime;
    private final int durationMin;
    private final Date registrationDeadLine;
    // add organizer typer user somehow, and translate that into organizer id to the database

    //! add durationmin to database table
    private final int ticketCapacity;
    private final String status;
    private final Timestamp creationTime;

    private final List<UserModel> attendees;

    public EventModel(int id, String name, String description, String venue, Date date, LocalDateTime startTime, int durationMin, Date registrationDeadLine, int ticketCapacity, String status, Timestamp creationTime, List<UserModel> attendees) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.date = date;
        this.startTime = startTime;
        this.durationMin = durationMin;
        this.registrationDeadLine = registrationDeadLine;
        this.ticketCapacity = ticketCapacity;
        this.status = status;
        this.creationTime = creationTime;
        this.attendees = attendees;
    }
    
    // Standard Getters and Setters
    public int getID() {return id;}
    public String getName() {return name;}
    public Date getDate() {return date;}
    public String getVenue() {return venue;}
    public LocalDateTime getStartTime() {return startTime;}
    public int getDurationMin() {return durationMin;}
    public int getTicketCapacity() {return ticketCapacity;}
    public List<UserModel> getAttendees() {return attendees;}
    
    public LocalDateTime getEndTime(int durationMin) {
        if (startTime == null)
            return null;
        return startTime.plusMinutes(durationMin);
    }

    // public double calculateTicketPrice(){
    //     return
    // }
}
