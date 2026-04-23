package model;

import java.time.LocalDateTime;

public class TicketModel {
    private int registrationId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String ticketType;
    private String paymentStatus;

    public TicketModel(int registrationId, String eventTitle, LocalDateTime eventDate, String ticketType, String paymentStatus) {
        this.registrationId = registrationId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.ticketType = ticketType;
        this.paymentStatus = paymentStatus;
    }

    // Getters
    public int getRegistrationId() { return registrationId; }
    public String getEventTitle() { return eventTitle; }
    public LocalDateTime getEventDate() { return eventDate; }
    public String getTicketType() { return ticketType; }
    public String getPaymentStatus() { return paymentStatus; }
}