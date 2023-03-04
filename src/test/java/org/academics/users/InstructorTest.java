package org.academics.users;

import org.academics.dal.dbInstructor;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
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


    @Test
    void floatCourse() {
    }

    @Test
    void viewCourses() {
    }

    @Test
    void downloadAndExportStudentList() {
    }

    @Test
    void testUploadGrades() throws Exception {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
             MockedStatic<dbInstructor> mockedDbInstructor = Mockito.mockStatic(dbInstructor.class);) {
            mockedUtils.when(() -> Utils.getInput("Enter the course code")).thenReturn("CSCI1234");
            mockedUtils.when(() -> Utils.getInput("Enter the session (YYYY-Semester)")).thenReturn("2022-1");
            mockedUtils.when(() -> Utils.getInput("Enter the path to the CSV file")).thenReturn(Mockito.anyString());

            mockedUtils.when(() -> Utils.validateEventTime(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.isCourseInstructor(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                    .thenReturn(true);
            mockedDbInstructor.when(() -> dbInstructor.uploadGrades(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()));
            Instructor instructor = new Instructor(new User("instructor", "test@yopmail.com"));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            instructor.uploadGrades();


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