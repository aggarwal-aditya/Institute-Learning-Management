package org.academics.dal;

import org.academics.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class to handle database operations related to users
 */
public class dbUser {

    private static final JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    private static final Connection conn = jdbc.getConnection();

    /**
     * This method validates user's email_id and password against the records in the database.
     *
     * @param email_id the user's email_id to be validated
     * @param password the user's password to be validated
     * @return String the user's role if credentials are valid, else null
     * @throws SQLException if a database access error occurs
     */
    public static String validateCredentials(String email_id, String password) throws SQLException {
        // Here, a prepared statement is created to retrieve the user's details from the database
        PreparedStatement userDetails = conn.prepareStatement("SELECT * FROM users WHERE email_id = ? AND password = ?");
        userDetails.setString(1, email_id);
        userDetails.setString(2, password);

        // If the retrieved details exist, the user's role is returned
        if (userDetails.executeQuery().next()) {
            // Here, a prepared statement is created to retrieve the user's role from the database
            PreparedStatement statementRole = conn.prepareStatement("SELECT role FROM users WHERE email_id = ?");
            statementRole.setString(1, email_id);
            ResultSet resultSet = statementRole.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        }
        // If the retrieved details don't exist, null is returned
        return null;
    }

    /**
     Validates user credentials based on the email id provided.
     @param email_id The email id of the user.
     @return true if the user's details are found in the database, false otherwise.
     @throws SQLException if an error occurs while accessing the database.
     */
    public static boolean validateCredentials(String email_id) throws SQLException{
        // Here, a prepared statement is created to retrieve the user's details from the database
        PreparedStatement userDetails = conn.prepareStatement("SELECT * FROM users WHERE email_id = ?");
        userDetails.setString(1, email_id);
        return userDetails.executeQuery().next();
    }

    /**
     * Retrieves user's profile details from the database.
     *
     * @param user a User object representing the user whose profile details are to be retrieved
     * @return a ResultSet object containing the user's profile details
     * @throws SQLException if there is an error while accessing the database
     */
    public static ResultSet getProfileDetails(User user) throws SQLException {
        PreparedStatement userDetails = null;
        // Depending on the user's role, different SQL queries are executed
        if (user.userRole.equals("student")) {
            userDetails = conn.prepareStatement("SELECT student_id,students.name,phone_number,d.name,batch FROM students JOIN departments d on students.department_id = d.id WHERE email_id = ?");
        } else if (user.userRole.equals("instructor")) {
            userDetails = conn.prepareStatement("SELECT instructor_id,instructors.name,phone_number,d.name,date_of_joining FROM instructors JOIN departments d on instructors.department_id = d.id WHERE email_id = ?");
        }
        // Setting the email_id parameter in the PreparedStatement
        assert userDetails != null;
        userDetails.setString(1, user.email_id);
        // Executing the query and returning the ResultSet
        return userDetails.executeQuery();
    }

    /**
     * Updates the phone number of the given user in the database
     * depending on the user's role.
     *
     * @param user The User object representing the user whose phone number is to be updated.
     * @param phone The new phone number to be set.
     * @return true if the phone number was updated successfully, false otherwise.
     * @throws SQLException if there is an error executing the SQL query.
     */
    public static boolean updatePhone(User user, String phone) throws SQLException {
        PreparedStatement userDetails = null;
        // Depending on the user's role, different SQL queries are executed
        if (user.userRole.equals("student")) {
            userDetails = conn.prepareStatement("UPDATE students SET phone_number = ? WHERE email_id = ?");
        } else if (user.userRole.equals("instructor")) {
            userDetails = conn.prepareStatement("UPDATE instructors SET phone_number = ? WHERE email_id = ?");
        }
        // Setting the email_id parameter in the PreparedStatement
        assert userDetails != null;
        userDetails.setString(1, phone);
        userDetails.setString(2, user.email_id);
        // Executing the query and returning the ResultSet
        userDetails.execute();
        return userDetails.getUpdateCount() > 0;
    }

    /**
     * Changes the password of the given user in the database.
     * @param user the User whose password will be changed.
     * @param password the new password to be set.
     * @return a boolean indicating whether the password was successfully changed or not.
     * @throws SQLException if there is an error while updating the user's password in the database.
     */
    public static boolean changePassword(User user, String password) throws SQLException {
        PreparedStatement userDetails = conn.prepareStatement("UPDATE users SET password = ? WHERE email_id = ?");
        userDetails.setString(1, password);
        userDetails.setString(2, user.email_id);
        userDetails.executeUpdate();
        return userDetails.getUpdateCount() > 0;
    }

}
