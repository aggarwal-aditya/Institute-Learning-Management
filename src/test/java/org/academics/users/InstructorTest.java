package org.academics.users;

import org.academics.dal.dbInstructor;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class InstructorTest {

    @BeforeEach
    void setUp() {
        //clear all static mocks
        Mockito.framework().clearInlineMocks();

    }

    @AfterEach
    void tearDown() {
    }


//    @Test
//    void floatCourse() throws SQLException {
//        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
//             MockedStatic<dbInstructor> mockedDbInstructor = Mockito.mockStatic(dbInstructor.class);) {
//            ResultSet resultSet=Mockito.mock(ResultSet.class);
//            when(resultSet.next()).thenReturn(true).thenReturn(false);
//            when(resultSet.getString("course_code")).thenReturn("CSCI1234");
//            when(resultSet.getString("course_name")).thenReturn("Test Course");
//            doNothing().when(mockedUtils).when(() -> Utils.printTable(Mockito.any(ResultSet.class), Mockito.any(String[].class), Mockito.anyString(), Mockito.anyString()));
//            mockedDbInstructor.when(() -> dbInstructor.checkCourseApproval(Mockito.anyString()))
//                    .thenReturn(true);
//            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
//                    .thenReturn(true);


//        }
//    }


    @Test
    void viewCourses() throws SQLException {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
             MockedStatic<dbInstructor> mockedDbInstructor = Mockito.mockStatic(dbInstructor.class);) {
            ResultSet resultSet = Mockito.mock(ResultSet.class);
            mockedDbInstructor.when(() -> dbInstructor.fetchCourses(Mockito.anyInt()))
                    .thenReturn(resultSet);
            //doNothing when Utils.printTable is called
            Instructor instructor = new Instructor(new User("instructor", "test@yopmail.com"));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            boolean returnValue = instructor.viewCourses();
            assertFalse(returnValue);

            resultSet = Mockito.mock(ResultSet.class);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("course_code")).thenReturn("CSCI1234");
            when(resultSet.getString("semester")).thenReturn("2022-1");
            when(resultSet.getString("qualify")).thenReturn("7");
            when(resultSet.getInt("enrollment_count")).thenReturn(10);
            returnValue = resultSet.next();
            assertTrue(returnValue);

        }

    }

    @Test
    void downloadAndExportStudentList() throws SQLException, IOException {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
             MockedStatic<dbInstructor> mockedDbInstructor = Mockito.mockStatic(dbInstructor.class);) {

            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.fetchStudentsList(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Mockito.mock(ResultSet.class));


            mockedUtils.when(() -> Utils.getInput(eq("Enter the course code"))).thenReturn("CSCI1234");
            mockedUtils.when(() -> Utils.getInput(eq("Enter the session (YYYY-Semester)"))).thenReturn("2022-1");


            Instructor instructor = new Instructor(new User("instructor", "test@yopmail.com"));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.downloadAndExportStudentList();
            String expectedOutput = "Student list exported successfully";
            assertEquals(expectedOutput, outContent.toString().trim());

            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(false);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.downloadAndExportStudentList();
            expectedOutput = "You are not authorized to get student list for this course";
            assertEquals(expectedOutput, outContent.toString().trim());
        }

    }

    @Test
    void testUploadGrades() throws SQLException, IOException {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
             MockedStatic<dbInstructor> mockedDbInstructor = Mockito.mockStatic(dbInstructor.class);) {


            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.uploadGrades(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(10);

            mockedUtils.when(() -> Utils.getInput(eq("Enter the course code"))).thenReturn("CSCI1234");
            mockedUtils.when(() -> Utils.getInput(eq("Enter the session (YYYY-Semester)"))).thenReturn("2022-1");
            mockedUtils.when(() -> Utils.getInput(eq("Enter the path to the CSV file"))).thenReturn(Mockito.anyString());


            Instructor instructor = new Instructor(new User("instructor", "test@yopmail.com"));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.uploadGrades();
            String expectedOutput = "Grades uploaded successfully for 10 students";
            assertEquals(expectedOutput, outContent.toString().trim());

            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(false);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.uploadGrades();
            expectedOutput = "Grades submission is not allowed at this time";
            assertEquals(expectedOutput, outContent.toString().trim());

            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(false);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.uploadGrades();
            expectedOutput = "You are not authorized to add grades for this course";
            assertEquals(expectedOutput, outContent.toString().trim());


        }
    }


    @Test
    public void testDelistCourse() throws SQLException {
        // Set up mocked static methods for the external dependencies
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
             MockedStatic<dbInstructor> mockedDbInstructor = Mockito.mockStatic(dbInstructor.class);) {
            mockedUtils.when(() -> Utils.getInput("Enter the course code")).thenReturn("CSCI1234");
            mockedUtils.when(() -> Utils.getInput("Enter the session (YYYY-Semester)")).thenReturn("2022-1");

            // Mock the dbInstructor.isCourseInstructor() method to return true
            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);

            mockedDbInstructor.when(() -> dbInstructor.delistCourse(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);

            ResultSet resultSet = Mockito.mock(ResultSet.class);
            mockedDbInstructor.when(() -> dbInstructor.fetchCourses(Mockito.anyInt()))
                    .thenReturn(resultSet);

            // Mock the Utils.validateEventTime() method to return true
            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true);
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            Instructor instructor = new Instructor(new User("instructor", "test@yopmail.com"));
            //mock instructor view_courses

            instructor.delistCourse();
            assertEquals(outContent.toString(), "");

            resultSet = Mockito.mock(ResultSet.class);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("course_code")).thenReturn("CSCI1234");
            when(resultSet.getString("semester")).thenReturn("2022-1");
            when(resultSet.getString("qualify")).thenReturn("7");
            when(resultSet.getInt("enrollment_count")).thenReturn(10);
            mockedDbInstructor.when(() -> dbInstructor.fetchCourses(Mockito.anyInt()))
                    .thenReturn(resultSet);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.delistCourse();
            String output = outContent.toString();
            assert (output.contains("Course delisted successfully"));

            resultSet = Mockito.mock(ResultSet.class);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("course_code")).thenReturn("CSCI1234");
            when(resultSet.getString("semester")).thenReturn("2022-1");
            when(resultSet.getString("qualify")).thenReturn("7");
            when(resultSet.getInt("enrollment_count")).thenReturn(10);
            mockedDbInstructor.when(() -> dbInstructor.fetchCourses(Mockito.anyInt()))
                    .thenReturn(resultSet);
            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(false);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.delistCourse();
            output = outContent.toString();
            assert (output.contains("You are not authorized to delist this course"));


            resultSet = Mockito.mock(ResultSet.class);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("course_code")).thenReturn("CSCI1234");
            when(resultSet.getString("semester")).thenReturn("2022-1");
            when(resultSet.getString("qualify")).thenReturn("7");
            when(resultSet.getInt("enrollment_count")).thenReturn(10);
            mockedDbInstructor.when(() -> dbInstructor.fetchCourses(Mockito.anyInt()))
                    .thenReturn(resultSet);
            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.delistCourse(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(false);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.delistCourse();
            output = outContent.toString();
            assert (output.contains("Course delisting failed"));

            resultSet = Mockito.mock(ResultSet.class);
            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getString("course_code")).thenReturn("CSCI1234");
            when(resultSet.getString("semester")).thenReturn("2022-1");
            when(resultSet.getString("qualify")).thenReturn("7");
            when(resultSet.getInt("enrollment_count")).thenReturn(10);
            mockedDbInstructor.when(() -> dbInstructor.fetchCourses(Mockito.anyInt()))
                    .thenReturn(resultSet);
            mockedDbInstructor.when(() -> dbInstructor.delistCourse(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);
            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(false);
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.delistCourse();
            output = outContent.toString();
            assert (output.contains("Course delisting for the specified semester is not allowed at this time"));
        }
    }

}