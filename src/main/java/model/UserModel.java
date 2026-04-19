package model;

import java.sql.Timestamp;

public class UserModel {
    private int id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private Timestamp createdAt;

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
    public String password() {return password;}
    public Timestamp getCreatedAt() {return createdAt;}
    
    public void setId(int var1) {this.id = var1;}
    public void setName(String var1) {this.name = var1;}
    public void setEmail(String var1) {this.email = var1;}
    public void setRole(UserRole var1) {this.role = var1;}
    public void setPassword(String var1) {this.password = var1;}
    public void setCreatedAt(Timestamp var1) {this.createdAt = var1;}
}
