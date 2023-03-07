package org.academics.menus;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbStudent;
import org.academics.dal.dbUser;
import org.academics.users.Admin;
import org.academics.users.User;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class mainMenuTest {

    ByteArrayOutputStream outputStream;

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();

    @BeforeEach
    void setUp() throws SQLException {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
    }

    @AfterEach
    void tearDown() {
        Mockito.framework().clearInlineMocks();
    }


    @Test
    void testMainMenuInvalidLogin() throws SQLException {
        MockedStatic<Utils>mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(1).thenReturn(3);
        MainMenu.mainMenu();
        assert (outputStream.toString().contains("Invalid credentials. Redirecting to Main Menu"));
    }

    @Test
    void testMainMenuValidLoginStudent() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(1).thenReturn(3);
        mockedUtils.when(()-> Utils.getInput("Enter your username(email):")).thenReturn("2020csb1066@iitrpr.ac.in");
        mockedUtils.when(()-> Utils.getInput("Enter your password:")).thenReturn("aditya");
        MainMenu.mainMenu();
        assert (outputStream.toString().contains("Welcome 2020csb1066@iitrpr.ac.in"));
    }

    @Test
    void testMainMenuValidLoginTeacher() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(1).thenReturn(3);
        mockedUtils.when(()-> Utils.getInput("Enter your username(email):")).thenReturn("mudgal@yopmail.com");
        mockedUtils.when(()-> Utils.getInput("Enter your password:")).thenReturn("aditya");
        MainMenu.mainMenu();
        assert (outputStream.toString().contains("Welcome mudgal@yopmail.com"));
    }

    @Test
    void testMainMenuValidLoginAdmin() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(1).thenReturn(3);
        mockedUtils.when(()-> Utils.getInput("Enter your username(email):")).thenReturn("admin@yopmail.com");
        mockedUtils.when(()-> Utils.getInput("Enter your password:")).thenReturn("aditya");
        MainMenu.mainMenu();
        assert (outputStream.toString().contains("Welcome admin@yopmail.com"));
    }

    @Test
    void testMainMenuresetPassword() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(2).thenReturn(3);
        mockedUtils.when(Utils::generateOTP).thenReturn(123456);
        mockedUtils.when(()-> Utils.getInput("Enter your username(email):")).thenReturn("admin@yopmail.com");
        mockedUtils.when(()-> Utils.getInput("Enter the OTP sent on your email to reset your password :")).thenReturn("123456");
        mockedUtils.when(()-> Utils.getInput("Enter your new password:")).thenReturn("aditya");
        MainMenu.mainMenu();
        assert (outputStream.toString().contains("Password reset successful. Please login again."));
    }


    @Test
    void mainMenuOPT2() throws SQLException {
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(2).thenReturn(3);
        MainMenu.mainMenu();
        assert (outputStream.toString().contains("Username not registered with ILM. Contact Admin for new Account Creation."));
    }
    @Test
    void mainMenuOPT3() throws SQLException {
       MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
       mockedUtils.when(() -> Utils.getUserChoice(3)).thenReturn(3).thenReturn(4);
       MainMenu.mainMenu();
       assert (outputStream.toString().contains("Thank you for using ILM"));
    }
}