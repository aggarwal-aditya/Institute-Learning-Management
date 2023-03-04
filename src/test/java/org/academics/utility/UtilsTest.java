package org.academics.utility;

import org.academics.dal.dbUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.io.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;


class UtilsTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        //close all mockito static mocks
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void generateOTP() {
        int otp = Utils.generateOTP();
        assertTrue(otp >= 100000 && otp <= 999999, "OTP is not a 6-digit number");
    }

    @Test
    void getUserChoice() {
        String input = "2\n"; // Set the input to be the number 2 followed by a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        int choice = Utils.getUserChoice(3);
        assertEquals(2, choice);
        input = "10\nqwerty\n3\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        choice = Utils.getUserChoice(5);
        assertEquals(3, choice);
        input = "qwerty\n3\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        choice = Utils.getUserChoice(5);
        assertEquals(3, choice);
    }

    @Test
    void getInput() {

        String message = "Enter a word:";
        String input = "Hello";
        System.setIn(new ByteArrayInputStream(input.getBytes())); // Set the standard input to a ByteArrayInputStream
        String result = Utils.getInput(message);
        assertEquals(input, result); // Check if the method returns the expected input

        input = "\nHello";
        System.setIn(new ByteArrayInputStream(input.getBytes())); // Set the standard input to an empty ByteArrayInputStream
        result = Utils.getInput(message);
        assertTrue(input.contains(result)); // Check if the method prompts the user to enter valid input when the input is empty
    }

    @Test
    void getCurrentSession() throws SQLException{
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("2023");
        when(resultSet.getString(2)).thenReturn("1");

        // Set up the mock static method call
        try (MockedStatic<dbUtils> mockedDbUtils = Mockito.mockStatic(dbUtils.class)) {
            mockedDbUtils.when(() -> dbUtils.getCurrentSession(Mockito.any(LocalDate.class))).thenReturn(resultSet);

            // Call the method being tested
            String result = Utils.getCurrentSession();


            // Verify the result
            assertEquals("2023-1", result);
        }

        when(resultSet.next()).thenReturn(false);
        try (MockedStatic<dbUtils> mockedDbUtils = Mockito.mockStatic(dbUtils.class)) {
            mockedDbUtils.when(() -> dbUtils.getCurrentSession(Mockito.any(LocalDate.class))).thenReturn(resultSet);

            // Call the method being tested
            String result = Utils.getCurrentSession();

            // Verify the result
            assertNull(result);
        }
    }

    @Test
    void validateEventTime() throws SQLException {
        // Set up the mock ResultSet object
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getDate(1)).thenReturn(Date.valueOf("2022-03-01"));
        when(resultSet.getDate(2)).thenReturn(Date.valueOf("2024-03-03"));


        // Set up the mock static method call
        try (MockedStatic<dbUtils> mockedDbUtils = Mockito.mockStatic(dbUtils.class)) {
            mockedDbUtils.when(() -> dbUtils.validateEventTime(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(LocalDate.class)))
                    .thenReturn(resultSet);

            // Call the method being tested
            boolean result = Utils.validateEventTime("eventType", "2023-1");


            // Verify the result
            assertTrue(result);
        }

        resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);

        try (MockedStatic<dbUtils> mockedDbUtils = Mockito.mockStatic(dbUtils.class)) {
            mockedDbUtils.when(() -> dbUtils.validateEventTime(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(LocalDate.class)))
                    .thenReturn(resultSet);

            // Call the method being tested
            boolean result = Utils.validateEventTime("eventType", "2023-1");
            // Verify the result
            assertFalse(result);

        }

    }

    @Test
    public void exportTxt() throws Exception {
        // Mock the ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString("Course Code")).thenReturn("CS101");
        when(resultSet.getString("Course Name")).thenReturn("Introduction to Computer Science");
        when(resultSet.getString("Semester")).thenReturn("Spring 2022");
        when(resultSet.getString("Grade")).thenReturn("A");

        // Mock the file name and message
        String fileName = "testFile";
        String message = "Test message";

        // Call the method being tested
        Utils.exportTxt(resultSet, fileName, message);

        // Verify that the file was downloaded
        String os = System.getProperty("os.name").toLowerCase();
        String username = System.getProperty("user.name");
        String downloadPath ;
        if (os.contains("win")) {
            downloadPath = "C:\\Users\\" + username + "\\Downloads\\" + fileName + ".txt";
        } else if (os.contains("mac")) {
            downloadPath = "/Users/" + username + "/Downloads/" + fileName + ".txt";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            downloadPath = "/home/" + username + "/Downloads/" + fileName + ".txt";
        } else {
            downloadPath = "";
        }
        File file = new File(downloadPath);
        assertTrue(file.exists());
        assertTrue(file.isFile());

        // Delete the file
        file.delete();
    }


    @Test
    void exportCSV() throws SQLException, IOException {
        // Mock ResultSet object
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        // Set up mock behavior
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("Name");
        when(metaData.getColumnName(2)).thenReturn("Age");

        // Set up mock data
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("John", "Jane");
        when(resultSet.getString(2)).thenReturn("25", "30");

        // Call the method under test
        String[] headers = {"Gender"};
        String fileName = "test";
        Utils.exportCSV(resultSet, fileName, headers);

        // Verify that the CSV file is correctly generated with the expected content
        String downloadPath = getDownloadPath();
        BufferedReader reader = new BufferedReader(new FileReader(downloadPath));
        Assertions.assertEquals("Name,Age,Gender", reader.readLine());
        Assertions.assertEquals("John,25", reader.readLine());
        Assertions.assertEquals("Jane,30", reader.readLine());
        reader.close();
    }

    @Test
    void printTable() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        // Simulate the behavior of the result set
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getString(1)).thenReturn("CSE101");
        when(resultSet.getString(2)).thenReturn("Introduction to Programming");
        when(resultSet.getString(3)).thenReturn("Fall 2022");
        when(resultSet.getString(4)).thenReturn("1001");
        when(resultSet.getString(5)).thenReturn("Alice");
        when(resultSet.getString(6)).thenReturn("A");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String successMessage="Success";
        String failureMessage="Failure";
        Utils.printTable(resultSet,new String[]{"Course Code", "Course Name", "Semester", "Student ID", "Student Name", "Grade"},successMessage,failureMessage);
        String output = outputStream.toString();
        assertTrue(output.contains(successMessage));
        assertTrue(output.contains("Course Code"));
        assertTrue(output.contains("Course Name"));
        assertTrue(output.contains("Semester"));
        assertTrue(output.contains("Student ID"));
        assertTrue(output.contains("Student Name"));
        assertTrue(output.contains("Grade"));
        assertTrue(output.contains("CSE101"));
        assertTrue(output.contains("Introduction to Programming"));
        assertTrue(output.contains("Fall 2022"));
        assertTrue(output.contains("1001"));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("A"));

        when(resultSet.next()).thenReturn(false);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Utils.printTable(resultSet,new String[]{"Course Code", "Course Name", "Semester", "Student ID", "Student Name", "Grade"},successMessage,failureMessage);
        output = outputStream.toString();
        assertTrue(output.contains(failureMessage));

    }


    private String getDownloadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String username = System.getProperty("user.name");
        String downloadPath;
        if (os.contains("win")) {
            downloadPath = "C:\\Users\\" + username + "\\Downloads\\test.csv";
        } else if (os.contains("mac")) {
            downloadPath = "/Users/" + username + "/Downloads/test.csv";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            downloadPath = "/home/" + username + "/Downloads/test.csv";
        } else {
            throw new RuntimeException("Unsupported operating system");
        }
        return downloadPath;
    }

}