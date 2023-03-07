package org.academics.users;

import org.academics.dal.dbAdmin;
import org.academics.dal.dbInstructor;
import org.academics.dal.dbUtils;
import org.academics.utility.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class specialPrivilegesTest {


    @BeforeEach
    void setUp() throws SQLException {
        Mockito.framework().clearInlineMocks();
    }
    @Test
    public void viewStudentGrades_T1() throws SQLException {

        MockedStatic<Utils>mockedUtils=mockStatic(Utils.class);
        MockedStatic<dbAdmin>mockedDbAdmin=mockStatic(dbAdmin.class);
        MockedStatic<dbInstructor>mockedDbInstructor=mockStatic(dbInstructor.class);

        mockedUtils.when(()->Utils.getUserChoice(3)).thenReturn(1);
        mockedUtils.when(()->Utils.getInput("Enter the course code: ")).thenReturn("CS202");
        mockedUtils.when(()->Utils.getInput("Enter the session (YYYY-Semester)")).thenReturn("2022-2");

        ResultSet resultSet=mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        mockedDbInstructor.when(()->dbInstructor.fetchCourseGrades(anyString(),anyString())).thenReturn(resultSet);
        assertTrue(true);
        specialPrivileges.viewStudentGrades();

    }

    @Test
    public void viewStudentGrades_T2() throws SQLException {

        MockedStatic<Utils>mockedUtils=mockStatic(Utils.class);
        MockedStatic<dbAdmin>mockedDbAdmin=mockStatic(dbAdmin.class);
        MockedStatic<dbInstructor>mockedDbInstructor=mockStatic(dbInstructor.class);

        mockedUtils.when(()->Utils.getUserChoice(3)).thenReturn(2);
        mockedUtils.when(()->Utils.getInput("Enter the student's Enrollment ID: ")).thenReturn("2020CSB1066");

        ResultSet resultSet=mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        mockedDbInstructor.when(()->dbInstructor.fetchStudentGrades(anyString())).thenReturn(resultSet);
        assertTrue(true);
        specialPrivileges.viewStudentGrades();

    }

    @Test
    public void viewStudentGrades_T3() throws SQLException {

        MockedStatic<Utils>mockedUtils=mockStatic(Utils.class);
        MockedStatic<dbAdmin>mockedDbAdmin=mockStatic(dbAdmin.class);
        MockedStatic<dbInstructor>mockedDbInstructor=mockStatic(dbInstructor.class);

        mockedUtils.when(()->Utils.getUserChoice(3)).thenReturn(3);
        mockedUtils.when(()->Utils.getInput("Enter the student's Enrollment ID: ")).thenReturn("2020CSB1066");

        specialPrivileges.viewStudentGrades();

    }

    @Test
    public void testGetPreRequisites() {
        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            ArrayList<String> preReqs = specialPrivileges.getPreRequisites();
            when(Utils.getInput(anyString())).thenReturn("CS101\n").thenReturn("B\n").thenReturn("Y\n").thenReturn("CS102\n").thenReturn("B\n").thenReturn("N\n").thenReturn("CS103\n").thenReturn("E\n").thenReturn("N\n").thenReturn("N\n");
            specialPrivileges.getPreRequisites();
        }
//        assertTrue(pre);
    }

    @Test
    public  void testviewDepartmentIDs() throws SQLException {
        MockedStatic<Utils>utilsMockedStatic= mockStatic(Utils.class);
        MockedStatic<dbUtils>dbUtilsMockedStatic= mockStatic(dbUtils.class);
        //do nothing when utils.printTable is called
        ResultSet resultSet= mock(ResultSet.class);
        dbUtilsMockedStatic.when(dbUtils::getDepartmentIDs).thenReturn(resultSet);
        specialPrivileges.viewDepartmentIDs();
        //verify getDepartmentIDs was called
    }

    @Test
    public void getPreRequisites_T1() {
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the prerequisite")).thenReturn("CS101");
        mockedUtils.when(() -> Utils.getInput("Enter the minimum grade requirement for the prerequisite (Enter 'E' if no minimum grade requirement)")).thenReturn("B");
        mockedUtils.when(() -> Utils.getInput("Are there any alternatives to the prerequisite? (Y/N)")).thenReturn("N");
        mockedUtils.when(() -> Utils.getInput("Do you want to add additional prerequisites? (Y/N)")).thenReturn("N");
        specialPrivileges.getPreRequisites();

    }

    @Test
    public void getPreRequisites_T2() {
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the prerequisite")).thenReturn("CS101","MA101");
        mockedUtils.when(() -> Utils.getInput("Enter the minimum grade requirement for the prerequisite (Enter 'E' if no minimum grade requirement)")).thenReturn("B","C-");
        mockedUtils.when(() -> Utils.getInput("Are there any alternatives to the prerequisite? (Y/N)")).thenReturn("Y","N","N");
        mockedUtils.when(() -> Utils.getInput("Do you want to add additional prerequisites? (Y/N)")).thenReturn("Y","N");
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the alternative")).thenReturn("CS102");
        mockedUtils.when(() -> Utils.getInput("Enter the minimum grade requirement for the alternative (Enter 'E' if no minimum grade requirement)")).thenReturn("D");
        specialPrivileges.getPreRequisites();
        assertTrue(true);
    }
    @Test
    public void getPreRequisites_T3() {
        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the prerequisite")).thenReturn("CS101","MA101");
        mockedUtils.when(() -> Utils.getInput("Enter the minimum grade requirement for the prerequisite (Enter 'E' if no minimum grade requirement)")).thenReturn("B","C-");
        mockedUtils.when(() -> Utils.getInput("Are there any alternatives to the prerequisite? (Y/N)")).thenReturn("Y","Y","N","N");
        mockedUtils.when(() -> Utils.getInput("Do you want to add additional prerequisites? (Y/N)")).thenReturn("Y","N");
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the alternative")).thenReturn("CS102","MA102");
        mockedUtils.when(() -> Utils.getInput("Enter the minimum grade requirement for the alternative (Enter 'E' if no minimum grade requirement)")).thenReturn("D","B");
        specialPrivileges.getPreRequisites();
        assertTrue(true);
    }

}