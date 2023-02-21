package org.academics.dal;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCPostgreSQLConnection {
    private static JDBCPostgreSQLConnection instance = null;
    private Connection conn = null;

    private JDBCPostgreSQLConnection() {
        try {
            Dotenv dotenv = Dotenv.load();
            Properties props = new Properties();
            if("test".equals(dotenv.get("ENVIRONMENT"))) {
                props.load(new FileReader("src/main/resources/testdatabase.properties"));
            } else {
                props.load(new FileReader("src/main/resources/database.properties"));
            }
            String url =props.getProperty("db.url");
            String user= props.getProperty("db.user");
            String password =props.getProperty("db.password");
            conn = DriverManager.getConnection(url, user, password);

            if (conn == null) {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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
