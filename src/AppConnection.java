import java.sql.Connection;
import java.sql.DriverManager;

public class AppConnection {
    public static Connection getConn() {
        Connection conn = null;
        String usr = "root";
        String pss = "MySQL_219";
        String url = "jdbc:mysql://localhost:3307/test";        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, usr, pss);
                return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
