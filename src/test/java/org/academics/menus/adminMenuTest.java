package org.academics.menus;

import org.academics.users.Admin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.academics.utility.Utils;
import org.academics.users.specialPrivileges;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class adminMenuTest {

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
    void adminMenuOPT1() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(1).thenReturn(8);
        Mockito.doNothing().when(admin).updateCourseCatalog();
        AdminMenu.adminMenu(admin);
        verify(admin).updateCourseCatalog();
    }
    @Test
    void adminMenuOPT1Ex() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(1).thenReturn(8);
        doThrow(new SQLException()).when(admin).updateCourseCatalog();
        AdminMenu.adminMenu(admin);
        verify(admin).updateCourseCatalog();
        assert(outputStream.toString().contains("Unable to add course at the moment. Please try again later."));
    }
    @Test
    void adminMenuOPT2() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(2).thenReturn(8);
        Mockito.doNothing().when(admin).addSemesterTimeline();
        AdminMenu.adminMenu(admin);
        verify(admin).addSemesterTimeline();
    }

    @Test
    void adminMenuOPT2Ex() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(2).thenReturn(8);
        doThrow(new SQLException()).when(admin).addSemesterTimeline();
        AdminMenu.adminMenu(admin);
        verify(admin).addSemesterTimeline();
        assert(outputStream.toString().contains("Unable to add semester at the moment. Please try again later."));
    }

    @Test
    void adminMenuOPT3() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<specialPrivileges>specialPrivilegesMockedStatic = Mockito.mockStatic(specialPrivileges.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(3).thenReturn(8);
        AdminMenu.adminMenu(admin);
        specialPrivilegesMockedStatic.verify(specialPrivileges::viewStudentGrades);
    }

    @Test
    void adminMenuOPT3Ex() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<specialPrivileges>specialPrivilegesMockedStatic = Mockito.mockStatic(specialPrivileges.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(3).thenReturn(8);
        specialPrivilegesMockedStatic.when(specialPrivileges::viewStudentGrades).thenThrow(new SQLException());
        AdminMenu.adminMenu(admin);
        assert(outputStream.toString().contains("Unable to view student grades at the moment. Please try again later."));

    }

    @Test
    void adminMenuOPT4() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(4).thenReturn(8);
        Mockito.doNothing().when(admin).generateTranscript();
        AdminMenu.adminMenu(admin);
        verify(admin).generateTranscript();
    }

    @Test
    void adminMenuOPT4Ex() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(4).thenReturn(8);
        doThrow(new SQLException()).when(admin).generateTranscript();
        AdminMenu.adminMenu(admin);
        verify(admin).generateTranscript();
        assert(outputStream.toString().contains("Unable to generate transcript at the moment. Please try again later."));
    }

    @Test
    void adminMenuOPT5() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(5).thenReturn(8);
        when(admin.checkGraduationStatus()).thenReturn(true);
        AdminMenu.adminMenu(admin);
        verify(admin).checkGraduationStatus();
    }

    @Test
    void adminMenuOPT5Ex() throws SQLException {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(5).thenReturn(8);
        doThrow(new SQLException()).when(admin).checkGraduationStatus();
        AdminMenu.adminMenu(admin);
        verify(admin).checkGraduationStatus();
        assert(outputStream.toString().contains("Unable to check graduation status at the moment. Please try again later."));
    }

    @Test
    void adminMenuOPT6() {
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(6).thenReturn(8);
        Mockito.doNothing().when(admin).changeSystemSettings();
        AdminMenu.adminMenu(admin);
        verify(admin).changeSystemSettings();
    }

    @Test
    void  adminMenuOPT7(){
        Admin admin = Mockito.mock(Admin.class);
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getUserChoice(7)).thenReturn(7).thenReturn(8);
        AdminMenu.adminMenu(admin);
        assert (outputStream.toString().contains("Logging out..."));

    }









}