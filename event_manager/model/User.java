package model;

enum UserRole {
    ADMIN, STAFF, ORGANIZER, ATTENDEE, SPEAKER
}

public class User {
    private int id;
    private String name;
    private String email;
    private UserRole role;

    public boolean canEditEvent() {
        return this.role == UserRole.ADMIN || this.role == UserRole.STAFF;
    }
}
