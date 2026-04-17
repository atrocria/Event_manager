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

    public User(int id, String name, String email, UserRole role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getid() {return id;}
    public String getname() {return name;}
    public String getemail() {return email;}
    public UserRole getrole() {return role;}
}
