import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    public Connection db_connection(String dbname, String user, String pass) {
        Connection connect = null;
        try {
            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, user, pass);
            if (connect != null) {
                System.out.println("Connected");
            } else {
                System.out.println("Failed");
            }
        } catch (Exception e) {
            System.out.println("Couldn't connect");
        }
        return connect;
    }
}
