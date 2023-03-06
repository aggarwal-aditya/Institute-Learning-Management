package org.academics.users;

import org.academics.dal.dbUser;
import org.academics.utility.MailManagement;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;

class UserTest {

    @BeforeEach
    void setUp() {
        Mockito.framework().clearInlineMocks();
    }

    @AfterEach
    void tearDown() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void setUserDetails() {
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
    void resetPassword() throws SQLException {
        User user = new User("test@example.com", "student");
        MailManagement mailManagement = Mockito.mock(MailManagement.class);
        MockedStatic<dbUser> mockedDbUser = Mockito.mockStatic(dbUser.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);

//        mockedDbUser.when(() -> dbUser.validateCredentials(anyString())).thenReturn(false);
//        assertFalse(user.resetPassword());

        mockedDbUser.when(() -> dbUser.validateCredentials(anyString())).thenReturn(true);
        mockedDbUser.when(() -> dbUser.changePassword(any(), anyString())).thenReturn(true);

        System.out.println(dbUser.validateCredentials("a"));

        mockedUtils.when(Utils::generateOTP).thenReturn(123456);
        doNothing().when(mailManagement).sendMail(anyString(), anyString(), any(String[].class));
        mockedDbUser.when(() -> dbUser.validateCredentials(anyString())).thenReturn(true);
        mockedUtils.when(() -> Utils.getInput(eq("Enter your new password:"))).thenReturn("password");
        mockedUtils.when(() -> Utils.getInput(eq("Enter the OTP sent on your email to reset your password :"))).thenReturn("123456");
        user.resetPassword();
//        assertTrue(user.resetPassword());

//        mockedUtils.when(Utils::generateOTP).thenReturn("123451");
//        assertFalse(user.resetPassword());

    }

    @Test
    void viewProfile() {
    }

    @Test
    void editProfile() {
    }
}