package model;

public class AttendeeView {
    private int registrationId;
    private String userName;
    private String eventTitle;
    private String status;
    private String paymentStatus;

    public AttendeeView(int regId, String uName, String eTitle, String status, String paymentStatus) {
        this.registrationId = regId;
        this.userName = uName;
        this.eventTitle = eTitle;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    // Getters (Required for TableView to see the data)
    public int getRegistrationId() {
        return registrationId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getStatus() {
        return status;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
}