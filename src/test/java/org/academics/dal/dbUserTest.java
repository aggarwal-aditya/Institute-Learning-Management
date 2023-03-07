package org.academics.dal;

import org.academics.users.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


class dbUserTest {

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();

    @BeforeEach
    void setUp() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }

    @Test
    public void testValidateCredentials() throws SQLException {
        // Test valid credentials
        String email_id = "2020csb1066@iitrpr.ac.in";
        String password = "aditya";
        String role = dbUser.validateCredentials(email_id, password);
        assertEquals("student", role);

        // Test invalid credentials
        email_id = "jane.doe@example.com";
        password = "incorrect";
        role = dbUser.validateCredentials(email_id, password);
        assertNull(role);
    }

    @Test
    public void testValidateCredentialsByEmail() throws SQLException {
        // Test valid email
        String email_id = "mudgal@yopmail.com";
        boolean exists = dbUser.validateCredentials(email_id);
        assertTrue(exists);

        // Test invalid email
        email_id = "jane.doe@example.com";
        exists = dbUser.validateCredentials(email_id);
        assertFalse(exists);
    }

    @Test
    public void testGetProfileDetails() throws SQLException {
        // Test getting profile details for a student
        User student = new User("student", "2020csb1066@iitpr.ac.in");
        assertNotNull(dbUser.getProfileDetails(student));

        // Test getting profile details for an instructor
        User instructor = new User("instructor", "mudgal@yopmail.com");
        assertNotNull(dbUser.getProfileDetails(instructor));
    }


    @Test
    public void testUpdatePhone() throws SQLException {
        // Test updating phone number for a student
        User student = new User("student", "2020csb1066@iitrpr.ac.in");
        assertTrue(dbUser.updatePhone(student, "123-456-7890"));

        // Test updating phone number for an instructor
        User instructor = new User("instructor", "mudgal@yopmail.com");
        assertTrue(dbUser.updatePhone(instructor, "555-555-5555"));
    }

    @Test
    public void testChangePassword() throws SQLException {
        // Test changing password for a user
        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        assertTrue(dbUser.changePassword(user, "newpassword"));
    }

}