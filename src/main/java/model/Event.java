package model;

import java.time.LocalDateTime;
import java.util.List;

// base event
public abstract class Event {
    private final int id;
    private final String name;
    private final String venue;
    private final String date;
    private final LocalDateTime startTime;
    private final int durationMin;
    private final int ticketCapacity;

    private final List<User> attendees;

    public Event(int id, String name, String venue, String date, LocalDateTime startTime, int durationMin, int ticketCapacity, List<User> attendees) {

        //! use uml g-dom-001?
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.date = date;
        this.startTime = startTime;
        this.durationMin = durationMin;
        this.ticketCapacity = ticketCapacity;
        this.attendees = attendees;
    }
    
    // Standard Getters and Setters
    public int getID() {return id;}
    public String getName() {return name;}
    public String getDate() {return date;}
    public String getVenue() {return venue;}
    public LocalDateTime getStartTime() {return startTime;}
    public int getDurationMin() {return durationMin;}
    public int getTicketCapacity() {return ticketCapacity;}
    public List<User> getAttendees() {return attendees;}
    
    public LocalDateTime getEndTime(int durationMin) {
        if (startTime == null)
            return null;
        return startTime.plusMinutes(durationMin);
    }

    // public double calculateTicketPrice(){
    //     return
    // }
}
