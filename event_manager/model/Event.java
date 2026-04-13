package model;

import java.util.List;

// base event
public abstract class Event {
    private int id;
    private String name;
    private String venue;
    private String date;
    private int ticketCapacity;

    private List<User> attendees;
    
    // Standard Getters and Setters
}
