package org.academics.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.*;
import java.sql.*;

class UtilsTest {

    private Connection connection;
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ilmtest", "postgres", "password");
        System.setIn(System.in);
        System.setOut(System.out);
        try {
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException ignored) {
        }
        System.out.flush();
        System.err.flush();

    }
    @AfterEach
    void tearDown() throws SQLException {
        System.setIn(System.in);
        System.setOut(System.out);
        connection.close();
    }

    @Test
    void generateOTP() {
        int otp = Utils.generateOTP();
        assertTrue(otp >= 100000 && otp <= 999999, "OTP is not a 6-digit number");
    }

    @Test
    void testUserChoiceValidInput() {
        String input = "2\n"; // Set the input to be the number 2 followed by a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int choice = Utils.getUserChoice(3);

        assertEquals(2, choice);
    }

    @Test
    void testUserChoiceOutOfRangeInput() {
        String input = "10\nqwerty\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int choice = Utils.getUserChoice(5);

        assertEquals(3, choice);
    }

    @Test
    void testUserChoiceInvalidInput() {
        String input = "qwerty\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int choice = Utils.getUserChoice(5);

        assertEquals(3, choice);
    }

    @Test
    public void testGetInput() {
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
    void getCurrentSession() {
        try {
            CallableStatement populateDatabase = connection.prepareCall("CALL populate_database()");
            populateDatabase.execute();
            CurrentDate.getInstance().overwriteCurrentDate(2022, 9, 11);
            assertEquals("2022-1", Utils.getCurrentSession());
            CurrentDate.getInstance().overwriteCurrentDate(2050, 1, 11);
            assertNull(Utils.getCurrentSession());
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                CallableStatement clearDatabase = connection.prepareCall("CALL clear_database()");
                clearDatabase.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void validateEventTime() {
    }

    @Test
    void exportCSV() {

    }

    @Test
    void testFormatOutput() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        // Simulate the behavior of the result set
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getString(1)).thenReturn("CSE101", "CSE102", "CSE103");
        when(resultSet.getString(2)).thenReturn("Introduction to Programming", "Data Structures", "Algorithms");
        when(resultSet.getString(3)).thenReturn("Fall 2022", "Spring 2023", "Fall 2023");
        when(resultSet.getString(4)).thenReturn("1001", "1002", "1003");
        when(resultSet.getString(5)).thenReturn("Alice", "Bob", "Charlie");
        when(resultSet.getString(6)).thenReturn("A", "B", "C");

        // Call the method and capture its output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Utils.printTable(resultSet,new String[]{"Course Code", "Course Name", "Semester", "Student ID", "Student Name", "Grade"},"Please find the details below:");
        String output = outputStream.toString();
        assertTrue(output.contains("Course Code"));
        assertTrue(output.contains("Course Name"));
        assertTrue(output.contains("Semester"));
        assertTrue(output.contains("Student ID"));
        assertTrue(output.contains("Student Name"));
        assertTrue(output.contains("Grade"));
    }
}
