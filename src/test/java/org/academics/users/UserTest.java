package org.academics.users;

import org.academics.utility.MailManagement;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Connection connection;
    private Scanner scanner;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ilmtest", "postgres", "password");
        scanner = new Scanner(System.in);
    }
    @AfterEach
    void teardown() throws SQLException {
        // Close the database connection
        connection.close();
    }

    @Test
    void testSetUserDetails() {
        User user = new User();
        user.setUserDetails("student","2020csb1066");
        assertEquals("student",user.userRole);
        assertEquals("2020csb1066",user.email_id);
        User user1 = new User("instructor","mudgal@yopmail.com");
        assertEquals("instructor",user1.userRole);
        assertEquals("mudgal@yopmail.com",user1.email_id);
    }

    void addTestUser() throws SQLException {
        String email = "test@example.com";
        String password = "password";
        PreparedStatement addUser = connection.prepareStatement("INSERT INTO users (email_id, password,role) VALUES (?, ?,?)");
        addUser.setString(1, email);
        addUser.setString(2, password);
        addUser.setString(3, "student");
        addUser.execute();
    }
    @Test
    void testLogin() throws SQLException {
        try {
            User user = new User();
            String email = "test@example.com";
            String password = "password";
            addTestUser();
            String input = email + "\n" + password + "\n";
            ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            try {
                user.login();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String in_email_id = scanner.next();
            String in_password = scanner.next();
            PreparedStatement userDetails = connection.prepareStatement("SELECT * FROM users WHERE email_id = ? AND password = ?");
            userDetails.setString(1, in_email_id);
            userDetails.setString(2, in_password);
            if (userDetails.executeQuery().next()) {
                PreparedStatement statementRole = connection.prepareStatement("SELECT role FROM users WHERE email_id = ?");
                statementRole.setString(1, in_email_id);
                ResultSet resultSet = statementRole.executeQuery();
                while (resultSet.next()) {
                    user.userRole = resultSet.getString(1);
                }
                user.setUserDetails(user.userRole, in_email_id);
            } else {
                return;
            }
            assertEquals("student", user.userRole);
            assertEquals(email, user.email_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users");
            deleteUser.execute();
        }
    }

    @Test
    void testChangePassword() throws SQLException {
        try {
            User user = new User();
            addTestUser();
            String email = "test@example.com";
            String password = "password";
            String new_password = "new_password";
            user.changePassword(email, new_password);
            PreparedStatement userDetails = connection.prepareStatement("SELECT password FROM users WHERE email_id = ?");
            userDetails.setString(1, email);
            ResultSet resultSet = userDetails.executeQuery();
            while (resultSet.next()) {
                password = resultSet.getString(1);
            }
            assertEquals(new_password, password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users");
            deleteUser.execute();
        }
    }
    @Test
    void testNonExistentUserChangePassword() throws SQLException {
        try {
            User user = new User();
            addTestUser();
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users");
            deleteUser.execute();
            String email = "test@example.com";
            String password = "password";
            String new_password = "new_password";
            user.changePassword(email, new_password);
            PreparedStatement userDetails = connection.prepareStatement("SELECT password FROM users WHERE email_id = ?");
            userDetails.setString(1, email);
            ResultSet resultSet = userDetails.executeQuery();
            while (resultSet.next()) {
                password = resultSet.getString(1);
            }
            assertNotEquals(new_password, password);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users");
            deleteUser.execute();
        }
    }

    @Test
    void testResetPassword() throws SQLException{
        try{
            MailManagement mailManagement = mock(MailManagement.class);
            doNothing().when(mailManagement).sendMail(anyString(), anyString(), any());
            PowerMockito.mockStatic(Utils.class);
            when(Utils.generateOTP()).thenReturn(123456);
            User user = new User();
            addTestUser();
            String email = "test@example.com";
            String password = "password";
//            user.resetPassword(email);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}