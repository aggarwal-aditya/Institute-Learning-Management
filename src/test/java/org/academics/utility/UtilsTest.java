package org.academics.utility;

import org.academics.users.Instructor;
import org.academics.users.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class UtilsTest {

    @AfterEach
    void tearDown() {
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    void generateOTP() {
        int otp = Utils.generateOTP();
        assertTrue(otp >= 100000 && otp <= 999999, "OTP is not a 6-digit number");
    }

    @Test
    void testValidInput() {
        String input = "2\n"; // Set the input to be the number 2 followed by a newline
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int choice = Utils.getUserChoice(3);

        assertEquals(2, choice);
    }

    @Test
    void testOutOfRangeInput() {
        String input = "10\nqwerty\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int choice = Utils.getUserChoice(5);

        assertEquals(3, choice);
    }

    @Test
    void testInvalidInput() {
        String input = "qwerty\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int choice = Utils.getUserChoice(5);

        assertEquals(3, choice);
    }

    @Test
    void getCurrentSession() {
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
