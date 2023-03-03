/**
 * This class provides a singleton JDBC connection to the PostgreSQL database.
 */
package org.academics.dal;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class provides a singleton JDBC connection to the PostgreSQL database.
 */
public class JDBCPostgreSQLConnection {
    // Singleton instance of the class.
    private static JDBCPostgreSQLConnection instance = null;

    // JDBC Connection object.
    private Connection conn = null;

    /**
     * Private constructor to prevent multiple instances of the class.
     * Reads the database properties from the appropriate properties file depending on the environment.
     * Establishes a JDBC connection to the database.
     */
    private JDBCPostgreSQLConnection() {
        try {
            // Load the environment variables from .env file.
            Dotenv dotenv = Dotenv.load();

            // Load the database properties based on the environment.
            Properties props = new Properties();
            if("test".equals(dotenv.get("ENVIRONMENT"))) {
                props.load(new FileReader("src/main/resources/testdatabase.properties"));
            } else {
                props.load(new FileReader("src/main/resources/database.properties"));
            }

            // Get the database URL, username and password from the properties.
            String url =props.getProperty("db.url");
            String user= props.getProperty("db.user");
            String password =props.getProperty("db.password");

            // Establish the JDBC connection to the database.
            conn = DriverManager.getConnection(url, user, password);

            // Print error message if the connection could not be established.
            if (conn == null) {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            // Catch any SQL Exceptions and print the error message.
            System.out.println(e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions and print the stack trace.
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of the class.
     * @return Singleton instance of the class.
     */
    public static JDBCPostgreSQLConnection getInstance() {
        if (instance == null) {
            instance = new JDBCPostgreSQLConnection();
        }
        return instance;
    }

    /**
     * Returns the JDBC connection object to the database.
     * @return JDBC Connection object.
     */
    public Connection getConnection() {
        return conn;
    }
}
