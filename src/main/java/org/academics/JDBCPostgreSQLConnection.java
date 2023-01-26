package org.academics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCPostgreSQLConnection {
    private static JDBCPostgreSQLConnection instance = null;
    private Connection conn = null;

    private JDBCPostgreSQLConnection() {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException ex) {
//            System.out.println("Error: unable to load driver class!");
//            System.exit(1);
//        }
        try {
            String url = "jdbc:postgresql://localhost:5432/ilm";
            String user = "postgres";
            String password = "password";
            conn = DriverManager.getConnection(url, user, password);

            if (conn == null) {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static JDBCPostgreSQLConnection getInstance() {
        if (instance == null) {
            instance = new JDBCPostgreSQLConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }
}
