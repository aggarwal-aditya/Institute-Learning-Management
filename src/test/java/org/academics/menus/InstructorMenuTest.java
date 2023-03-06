package org.academics.menus;

import org.academics.users.Admin;
import org.academics.users.Instructor;
import org.academics.users.specialPrivileges;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InstructorMenuTest {


    ByteArrayOutputStream outputStream;
    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void instructorMenuOPT1() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(1).thenReturn(9);
        when(instructor.viewCourses()).thenReturn(true);
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).viewCourses();
    }

    @Test
    void instructorMenuOPT1Ex() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(1).thenReturn(9);
        doThrow(new SQLException()).when(instructor).viewCourses();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).viewCourses();
        assert (outputStream.toString().contains("Unable to fetch courses at the moment. Please try again later."));
    }


    @Test
    void instructorMenuOPT2() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(2).thenReturn(9);
        Mockito.doNothing().when(instructor).floatCourse();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).floatCourse();
    }

    @Test
    void instructorMenuOPT2Ex() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(2).thenReturn(9);
        doThrow(new SQLException()).when(instructor).floatCourse();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).floatCourse();
        assert (outputStream.toString().contains("Unable to float course at the moment. Please try again later."));
    }

    @Test
    void instructorMenuOPT3() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(3).thenReturn(9);
        Mockito.doNothing().when(instructor).delistCourse();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).delistCourse();
    }

    @Test
    void instructorMenuOPT3Ex() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(3).thenReturn(9);
        doThrow(new SQLException()).when(instructor).delistCourse();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).delistCourse();
        assert (outputStream.toString().contains("Unable to delist course at the moment. Please try again later."));
    }

    @Test
    void instructorMenuOPT4() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(4).thenReturn(9);
        Mockito.doNothing().when(instructor).downloadAndExportStudentList();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).downloadAndExportStudentList();
    }

    @Test
    void instructorMenuOPT4Ex() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(4).thenReturn(9);
        doThrow(new SQLException()).when(instructor).downloadAndExportStudentList();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).downloadAndExportStudentList();
        assert (outputStream.toString().contains("Unable to download student list at the moment. Please try again later."));
    }

    @Test
    void instructorMenuOPT5() throws SQLException, IOException {
        Instructor instructor = mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(5).thenReturn(9);
        doNothing().when(instructor).uploadGrades();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).uploadGrades();
    }

    @Test
    void instructorMenuOPT5Ex() throws SQLException, IOException {
        Instructor instructor = mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(5).thenReturn(9);
        doThrow(new SQLException()).when(instructor).uploadGrades();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).uploadGrades();
        assert (outputStream.toString().contains("Unable to upload grades at the moment. Please try again later."));
    }

    @Test
    void instructorMenuOPT6(){
        Instructor instructor = mock(Instructor.class);
        MockedStatic<specialPrivileges>specialPrivilegesMockedStatic = Mockito.mockStatic(specialPrivileges.class);
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(6).thenReturn(9);
        InstructorMenu.instructorMenu(instructor);
        specialPrivilegesMockedStatic.verify(specialPrivileges::viewStudentGrades);
    }

    @Test
    void instructorMenuOPT6Ex(){
        Instructor instructor = mock(Instructor.class);
        MockedStatic<specialPrivileges>specialPrivilegesMockedStatic = Mockito.mockStatic(specialPrivileges.class);
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(6).thenReturn(9);
        specialPrivilegesMockedStatic.when(specialPrivileges::viewStudentGrades).thenThrow(new SQLException());
        InstructorMenu.instructorMenu(instructor);
        specialPrivilegesMockedStatic.verify(specialPrivileges::viewStudentGrades);
    }

    @Test
    void instructorMenuOPT7() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(7).thenReturn(9);
        doNothing().when(instructor).viewProfile();
        InstructorMenu.instructorMenu(instructor);
    }

    @Test
    void instructorMenuOPT7Ex() throws SQLException {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(7).thenReturn(9);
        doThrow(new SQLException()).when(instructor).viewProfile();
        InstructorMenu.instructorMenu(instructor);
        verify(instructor).viewProfile();
        assert (outputStream.toString().contains("Unable to fetch profile at the moment. Please try again later."));
    }


    @Test
    void instructorMenuOPT8() {
        Instructor instructor = Mockito.mock(Instructor.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(8)).thenReturn(8).thenReturn(9);
        InstructorMenu.instructorMenu(instructor);
        assert (outputStream.toString().contains("Logging out..."));

    }
}