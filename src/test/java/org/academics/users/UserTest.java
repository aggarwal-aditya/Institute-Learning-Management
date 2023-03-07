package org.academics.users;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbUser;
import org.academics.utility.MailManagement;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserTest {

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();
    @BeforeEach
    void setUp() {
        Mockito.framework().clearInlineMocks();
    }

    @AfterEach
    void tearDown() {
        Mockito.framework().clearInlineMocks();
    }


    @Test
    void login() throws SQLException {
        // Mock the validateCredentials() method of the dbUser class to return "student" role
        User user = new User();
        MockedStatic<dbUser> mockedDbUser = Mockito.mockStatic(dbUser.class);
        mockedDbUser.when(() -> dbUser.validateCredentials(anyString(), anyString())).thenReturn("student");

        // Mock the Utils.getInput() method to return "test@example.com"
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getInput("Enter your username(email):")).thenReturn("test@example.com");
        mockedUtils.when(() -> Utils.getInput("Enter your password:")).thenReturn("password");

        // Call the login() method and assert that it returns true and sets the user details
        assertTrue(user.login());
        assertEquals("student", user.userRole);
        assertEquals("test@example.com", user.email_id);

        mockedDbUser.when(() -> dbUser.validateCredentials(anyString(), anyString())).thenReturn(null);
        user = new User();
        assertFalse(user.login());
    }

    @Test
    void resetPassword_nonUser() throws SQLException {
        User user = new User();
        MailManagement mailManagement = mock(MailManagement.class);
        MockedStatic<dbUser> mockedDbUser = Mockito.mockStatic(dbUser.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getInput("Enter your username(email):")).thenReturn("aditya@yopmail.com");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        boolean result=user.resetPassword();
        assertFalse(result);
        assert (outContent.toString().contains("Username not registered with ILM. Contact Admin for new Account Creation."));
    }

    @Test
    void resetPassword_user() throws SQLException {
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
        User user = new User();
        when(Utils.getInput("Enter your username(email):")).thenReturn("2020csb1066@iitrpr.ac.in");
        when(Utils.generateOTP()).thenReturn(123456);
        when(Utils.getInput("Enter the OTP sent on your email to reset your password :")).thenReturn("123456");
        when(Utils.getInput("Enter your new password:")).thenReturn("pass");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        boolean result=user.resetPassword();
        assertTrue(result);
        callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }
    @Test
    void resetPassword_wrongOTP() throws SQLException {
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
        User user = new User();
        when(Utils.getInput("Enter your username(email):")).thenReturn("2020csb1066@iitrpr.ac.in");
        when(Utils.generateOTP()).thenReturn(123456);
        when(Utils.getInput("Enter the OTP sent on your email to reset your password :")).thenReturn("123457");
        when(Utils.getInput("Enter your new password:")).thenReturn("pass");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        boolean result=user.resetPassword();
        assertFalse(result);
        assert (outContent.toString().contains("Invalid OTP. Redirecting to Main Menu"));
        callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }





    @Test
    void viewProfile() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
        User user=new User("student","2020csb1066@iitrpr.ac.in");
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        when(Utils.getUserChoice(2)).thenReturn(1);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        user.viewProfile();
        assert (outContent.toString().contains("Batch:"));
        outContent.reset();
        user=new User("instructor","mudgal@yopmail.com");
        user.viewProfile();
        assert (outContent.toString().contains("Date of Joining:"));
        callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }

    @Test
    void editProfile() throws SQLException {
        // Create mock objects
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<dbUser>dbUserMockedStatic = Mockito.mockStatic(dbUser.class);
        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        when(Utils.getUserChoice(3)).thenReturn(1);
        when(Utils.getInput("Enter your new phone number:")).thenReturn("1234567890");
        when(dbUser.updatePhone(Mockito.any(), anyString())).thenReturn(true);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        user.editProfile();
        assert (outContent.toString().contains("Phone number updated successfully"));
        outContent.reset();
        when(dbUser.updatePhone(Mockito.any(), anyString())).thenReturn(false);
        user.editProfile();
        assert (outContent.toString().contains("Unable to update phone number. Please try again later."));

        when(Utils.getUserChoice(3)).thenReturn(2);
        when(Utils.getInput("Enter your new password:")).thenReturn("pass");
        when(dbUser.changePassword(Mockito.any(), anyString())).thenReturn(true);
        outContent.reset();
        user.editProfile();
        assert (outContent.toString().contains("Password updated successfully"));
        outContent.reset();
        when(dbUser.changePassword(Mockito.any(), anyString())).thenReturn(false);
        user.editProfile();
        assert (outContent.toString().contains("Unable to update password. Please try again later."));






    }
}