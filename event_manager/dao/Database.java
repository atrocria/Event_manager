package dao;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.*;

//! make a registrations join table to solve many to many relations

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/event_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (SQLException e) {
            out.println("❌ Database Connection Failed!");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Database Connection Failed!", e);
            return null;
        }
    }
}
