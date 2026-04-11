package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static java.lang.System.out;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/event_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (SQLException e) {
            out.println("❌ Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}
