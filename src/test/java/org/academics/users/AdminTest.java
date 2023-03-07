package org.academics.users;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbAdmin;
import org.academics.utility.CurrentDate;
import org.academics.utility.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.postgresql.jdbc.PgResultSet;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminTest {

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();

    @BeforeEach
    void setUp() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    public void testupdateCourseCatalog() throws SQLException {
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<dbAdmin> mockeddbAdmin = Mockito.mockStatic(dbAdmin.class);
        MockedStatic<specialPrivileges>specialPrivilegesMockedStatic = Mockito.mockStatic(specialPrivileges.class);
        Admin admin = new Admin();
        when(Utils.getInput("Enter the course code")).thenReturn("CS101");
        when(Utils.getInput("Enter the course name")).thenReturn("Introduction to Computer Science");
        when(Utils.getInput("Enter the number of lecture hours")).thenReturn("3");
        when(Utils.getInput("Enter the number of tutorial hours")).thenReturn("1");
        when(Utils.getInput("Enter the number of practical hours")).thenReturn("2");
        when(Utils.getInput("Enter the number of self study hours")).thenReturn("4");
        when(Utils.getInput("Enter the number of credits")).thenReturn("4");
        when(specialPrivileges.getPreRequisites()).thenReturn(new ArrayList<>());
        ArrayList<String> preRequisites = new ArrayList<>();
        when(dbAdmin.updateCourseCatalog("CS101", "Introduction to Computer Science", 3, 1, 2, 4, 4, preRequisites)).thenReturn(true);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        admin.updateCourseCatalog();
        assert (outContent.toString().contains("Course added successfully"));
        outContent.reset();
        when(dbAdmin.updateCourseCatalog("CS101", "Introduction to Computer Science", 3, 1, 2, 4, 4, preRequisites)).thenReturn(false);
        admin.updateCourseCatalog();
        assert (outContent.toString().contains("Course could not be added"));
    }

    @Test
    public void testaddSemesterTimeline() throws SQLException {
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<dbAdmin> mockeddbAdmin = Mockito.mockStatic(dbAdmin.class);
        Admin admin = new Admin();
        when(Utils.getInput("Enter the semester:(YYYY-Semester)")).thenReturn("2021-Summer");
        when(Utils.getInput("Enter the start date:(YYYY-MM-DD)")).thenReturn("2021-05-01");
        when(Utils.getInput("Enter the end date:(YYYY-MM-DD)")).thenReturn("2021-08-01");
        when(Utils.getInput("Enter the grade submission date:(YYYY-MM-DD)")).thenReturn("2021-08-01");
        when(Utils.getInput("Enter the grade release/submission end date:(YYYY-MM-DD)")).thenReturn("2021-08-01");
        when(Utils.getInput("Enter the course add/drop start date:(YYYY-MM-DD)")).thenReturn("2021-05-01");
        when(Utils.getInput("Enter the course add/drop end date:(YYYY-MM-DD)")).thenReturn("2021-05-01");
        when(Utils.getInput("Enter the course float start date:(YYYY-MM-DD)")).thenReturn("2021-05-01");
        when(Utils.getInput("Enter the course float end date:(YYYY-MM-DD)")).thenReturn("2021-05-01");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        admin.addSemesterTimeline();
        assert (outContent.toString().contains("Semester timeline added successfully"));

    }

    @Test
    public void testchangeSystemSettings() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<dbAdmin> mockeddbAdmin = Mockito.mockStatic(dbAdmin.class);
        Admin admin = new Admin();
        CurrentDate currentDate=Mockito.mock(CurrentDate.class);
        when(Utils.getUserChoice(2)).thenReturn(2);
        admin.changeSystemSettings();
        when(Utils.getUserChoice(2)).thenReturn(1);
        when(Utils.getInput("Enter the year:")).thenReturn("2020");
        when(Utils.getInput("Enter the month:")).thenReturn("12");
        when(Utils.getInput("Enter the day:")).thenReturn("15");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        admin.changeSystemSettings();
        assert (outContent.toString().contains("System date changed successfully"));
    }

    @Test
    public void testgenerateTranscripts() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        Admin admin = new Admin();
        when(Utils.getInput("Enter the student's enrollment id:")).thenReturn("2020CSB1066");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        admin.generateTranscript();
        assert (outContent.toString().contains("Transcript generated successfully"));
    }

    @Test
    public void testCheckGraduationStatus_GpaBelow5_ReturnsFalse() throws SQLException {
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        Admin admin = new Admin();
        when(Utils.getInput("Enter the student's enrollment id:")).thenReturn("2020CSB1066");
        boolean result = admin.checkGraduationStatus();
        assertFalse(result);
    }

    @Test
    public void testCheckGraduationStatus_Failed() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<dbAdmin>dbAdminMockedStatic = Mockito.mockStatic(dbAdmin.class);
        Admin admin = new Admin();
        when(Utils.getInput("Enter the student's enrollment id:")).thenReturn("2020CSB1066");
        when(Utils.getInput("Enter the semester:(YYYY-Semester)")).thenReturn("2021-Summer");
        ResultSet resultSet=Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        when(dbAdmin.getStudentCourses("2020CSB1066")).thenReturn(resultSet);
        boolean result = admin.checkGraduationStatus();
        assertFalse(result);
    }

    @Test
    public void testCheckGraduationStatus_Passed() throws SQLException{
        MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class);
        MockedStatic<dbAdmin>dbAdminMockedStatic = Mockito.mockStatic(dbAdmin.class);
        Admin admin = new Admin();
        when(Utils.getInput("Enter the student's enrollment id:")).thenReturn("2020CSB1066");
        CallableStatement callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
        boolean result = admin.checkGraduationStatus();
//        assertTrue(result);
        callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }




}