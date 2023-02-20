package org.academics.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class InstructorTest {
    private Connection connection;
    private Scanner scanner;
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ilmtest", "postgres", "password");
        scanner = new Scanner(System.in);
    }
    @AfterEach
    void teardown() throws SQLException {
        connection.close();
    }
    void addTestInstructor() throws SQLException {
        String email = "test@example.com";
        String password = "password";
        String name="test";
        String phone="1234567890";
        int department_id=1;
        Date date=new Date();
        PreparedStatement addUser = connection.prepareStatement("INSERT INTO users (email_id, password,role) VALUES (?, ?,?)");
        addUser.setString(1, email);
        addUser.setString(2, password);
        addUser.setString(3, "instructor");
        addUser.execute();
        addUser=connection.prepareStatement("INSERT INTO departments (id,name) VALUES (?,?)");
        addUser.setInt(1,1);
        addUser.setString(2,"test_dept");
        addUser.execute();
        addUser = connection.prepareStatement("INSERT INTO instructors (instructor_id,email_id, name,phone_number,department_id,date_of_joining) VALUES (?,?, ?,?,?,?)");
        addUser.setInt(1, 1);
        addUser.setString(2, email);
        addUser.setString(3, name);
        addUser.setString(4, phone);
        addUser.setInt(5, department_id);
        addUser.setDate(6, new java.sql.Date(date.getTime()));
        addUser.execute();
    }
    @Test
    void testInstructor() throws SQLException {
        try {
            addTestInstructor();
            User user = new User();
            user.setUserDetails("instructor", "test@example.com");
            Instructor instructor = new Instructor(user);
            PreparedStatement instructorID= connection.prepareStatement("SELECT instructor_id FROM instructors WHERE email_id = ?");
            instructorID.setString(1, "test@example.com");
            instructorID.execute();
            ResultSet rs = instructorID.getResultSet();
            rs.next();
            int instructor_id = rs.getInt("instructor_id");
            assertEquals(instructor_id,instructor.getInstructorId());

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PreparedStatement deleteUser= connection.prepareStatement("DELETE FROM instructors");
            deleteUser.execute();
            deleteUser = connection.prepareStatement("DELETE FROM users");
            deleteUser.execute();
            deleteUser= connection.prepareStatement("DELETE FROM departments");
            deleteUser.execute();
            connection.close();
        }
    }
    @Test
    void testNonExistentInstructor() throws SQLException {
        try {
            User user = new User();
            Instructor instructor = new Instructor(user);
            assertEquals(0,instructor.getInstructorId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void testViewStudentGrades() throws IOException, SQLException {
//        // mock user input
//        String[] userInput = new String[]{"2", "STU01", "3"};
//        ByteArrayInputStream in = new ByteArrayInputStream(String.join(System.lineSeparator(), userInput).getBytes());
//        System.setIn(in);
//
//        // mock the database connection and result set
//        Connection conn = Mockito.mock(Connection.class);
//        PreparedStatement statement = Mockito.mock(PreparedStatement.class);
//        ResultSet resultSet = Mockito.mock(ResultSet.class);
//        Mockito.when(conn.prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(statement);
//        Mockito.when(statement.executeQuery()).thenReturn(resultSet);
//
//        // mock the `getCourseGrades` and `getStudentGrades` methods
//        Mockito.when(this.mockClass.getCourseGrades(Mockito.anyString(), Mockito.anyString())).thenReturn(resultSet);
//        Mockito.when(this.mockClass.getStudentGrades(Mockito.anyString())).thenReturn(resultSet);
//
//        // capture the console output
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(out));
//
//        // call the method to test
//        this.mockClass.viewStudentGrades();
//
//        // check the console output
//        String expectedOutput = "1. Select a course to view grades" + System.lineSeparator() +
//                "2. Search a student to view grades" + System.lineSeparator() +
//                "3. Go back to main menu" + System.lineSeparator() +
//                "Enter the student's Enrollment ID" + System.lineSeparator() +
//                "Course Code   Course Name   Semester   Student ID   Student Name   Grade" + System.lineSeparator();
//        Mockito.verify(conn, Mockito.times(2)).prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
//        Mockito.verify(statement, Mockito.times(2)).setString(Mockito.anyInt(), Mockito.anyString());
//        Mockito.verify(statement, Mockito.times(2)).executeQuery();
//        Mockito.verify(resultSet, Mockito.times(2)).last();
//        Mockito.verify(resultSet, Mockito.times(2)).beforeFirst();
//        assertEquals(expectedOutput, out.toString());
//    }

    @Test
    void testDelistCourse() throws SQLException {
        try {
            PreparedStatement addCourse = connection.prepareStatement("INSERT INTO course_catalog VALUES (?,?,?)");
            addCourse.setString(1, "test");
            addCourse.setString(2, "test");
            addCourse.setArray(3, connection.createArrayOf("integer", new Integer[]{1, 2, 3, 4, 5}));
            addCourse.execute();
            addTestInstructor();
            addCourse = connection.prepareStatement("INSERT INTO course_offerings VALUES (?,?,?)");
            addCourse.setString(1, "test");
            addCourse.setString(2, "test");
            addCourse.setInt(3, 1);
            addCourse.execute();

            User user = new User();
            user.setUserDetails("instructor", "test@example.com");
            Instructor instructor = new Instructor(user);

            String input = "test\ntest\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            instructor.delistCourse();
            PreparedStatement checkCourse = connection.prepareStatement("SELECT * FROM course_offerings WHERE course_code = ? AND semester = ?");
            checkCourse.setString(1, "test");
            checkCourse.setString(2, "test");
            checkCourse.execute();
            ResultSet rs = checkCourse.getResultSet();
            assertFalse(rs.next());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM course_offerings");
            deleteUser.execute();
            deleteUser= connection.prepareStatement("DELETE FROM instructors");
            deleteUser.execute();
            deleteUser = connection.prepareStatement("DELETE FROM users");
            deleteUser.execute();
            deleteUser= connection.prepareStatement("DELETE FROM departments");
            deleteUser.execute();
            deleteUser = connection.prepareStatement("DELETE FROM course_catalog");
            deleteUser.execute();
            connection.close();
        }
    }
    @Test
    void testNoCourseDelistCourse() throws SQLException {
        try{
            User user = new User();
            Instructor instructor = new Instructor(user);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            instructor.delistCourse();
            String output = outputStream.toString();
            assertTrue(output.contains("You have not floated any courses"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}