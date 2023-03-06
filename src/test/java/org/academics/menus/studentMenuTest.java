package org.academics.menus;

import org.academics.users.Student;
import org.academics.utility.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class studentMenuTest {
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
    public void studentMenuOPT1() {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(1).thenReturn(7);
        Mockito.doNothing().when(student).registerCourse();
        StudentMenu.studentMenu(student);
        verify(student).registerCourse();
    }

    @Test
    public void studentMenuOPT2() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(2).thenReturn(7);
        Mockito.doNothing().when(student).dropCourse();
        StudentMenu.studentMenu(student);
        verify(student).dropCourse();
    }

    @Test
    public void studentMenuOPT2Ex() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(2).thenReturn(7);
        Mockito.doThrow(new SQLException()).when(student).dropCourse();
        StudentMenu.studentMenu(student);
        verify(student).dropCourse();
        assert(outputStream.toString().contains("Unable to drop course at the moment. Please try again later."));
    }

    @Test
    public void studentMenuOPT3() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(3).thenReturn(7);
        Mockito.doNothing().when(student).viewCourses();
        StudentMenu.studentMenu(student);
        verify(student).viewCourses();
    }

    @Test
    public void studentMenuOPT3Ex() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(3).thenReturn(7);
        Mockito.doThrow(new SQLException()).when(student).viewCourses();
        StudentMenu.studentMenu(student);
        verify(student).viewCourses();
        assert(outputStream.toString().contains("Unable to fetch courses at the moment. Please try again later."));
    }

    @Test
    public void studentMenuOPT4() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(4).thenReturn(7);
        Mockito.doNothing().when(student).viewGrades();
        StudentMenu.studentMenu(student);
        verify(student).viewGrades();
    }

    @Test
    public void studentMenuOPT4Ex() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(4).thenReturn(7);
        Mockito.doThrow(new SQLException()).when(student).viewGrades();
        StudentMenu.studentMenu(student);
        verify(student).viewGrades();
        assert(outputStream.toString().contains("Unable to fetch grades at the moment. Please try again later."));
    }

    @Test
    public void studentMenuOPT5() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(5).thenReturn(7);
        Mockito.doNothing().when(student).printGPA();
        StudentMenu.studentMenu(student);
        verify(student).printGPA();
    }

    @Test
    public void studentMenuOPT5Ex() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(5).thenReturn(7);
        Mockito.doThrow(new SQLException()).when(student).printGPA();
        StudentMenu.studentMenu(student);
        verify(student).printGPA();
        assert(outputStream.toString().contains("Unable to compute GPA at the moment. Please try again later."));
    }

    @Test
    public void studentMenuOPT6() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(6).thenReturn(7);
        Mockito.doNothing().when(student).viewProfile();
        StudentMenu.studentMenu(student);
        verify(student).viewProfile();
    }

    @Test
    public void studentMenuOPT6Ex() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(6).thenReturn(7);
        Mockito.doThrow(new SQLException()).when(student).viewProfile();
        StudentMenu.studentMenu(student);
        verify(student).viewProfile();
        assert(outputStream.toString().contains("Unable to fetch profile at the moment. Please try again later."));
    }

    @Test
    public void studentMenuOPT7() throws SQLException {
        Student student = Mockito.mock(Student.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(7);
        StudentMenu.studentMenu(student);
        assert (outputStream.toString().contains("Logging out..."));
    }
}
