package app.helpers.database;

import java.sql.*;

public class DatabaseHandler {
    private static DatabaseHandler dh = null;
    private static final String url = "jdbc:sqlserver://localhost:1433; databaseName=SEnglish";
    private static final String user = "sa";
    private static final String pass = "123";

    private static Connection conn = null;

    public DatabaseHandler() {
        createConnection();
    }

    void createConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        if (conn == null) {
            createConnection();
        }
        return conn;
    }

    public static DatabaseHandler getInstance() {
        if (dh == null) {
            dh = new DatabaseHandler();
        }
        return dh;
    }
}
