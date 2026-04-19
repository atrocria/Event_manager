package model;

import java.sql.Timestamp;

public class UserModel {
    private int id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private Timestamp CreatedAt;

    public boolean canEditEvent() {
        return this.role == UserRole.ADMIN || this.role == UserRole.STAFF;
    }

    public UserModel(int id, String name, String email, UserRole role){
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
