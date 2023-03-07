package org.academics.users;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbStudent;
import org.academics.utility.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.academics.dal.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StudentTest {

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();
    @BeforeEach
    void setUp() throws SQLException {
          Mockito.framework().clearInlineMocks();
    }

    @Test
    void testprintGPA() throws SQLException {
        MockedStatic<dbStudent>dbStudentMockedStatic = Mockito.mockStatic(dbStudent.class);
        dbStudentMockedStatic.when(() -> dbStudent.computeGPA(anyString())).thenReturn(3.5);
        User user = new User("student","test@yopmail.com");
        Student student = new Student(user);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        student.printGPA();
        assert(outContent.toString().contains("3.5"));
    }
    @Test
    void testviewGrades() throws SQLException {
        MockedStatic<Utils>mockedUtils=mockStatic(Utils.class);
        MockedStatic<dbStudent>dbStudentMockedStatic = Mockito.mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("CS201");
        when(resultSet.getString(2)).thenReturn("Data Structures");
        when(resultSet.getString(3)).thenReturn("A");
        dbStudentMockedStatic.when(() -> dbStudent.fetchGrades(anyString(),anyString())).thenReturn(resultSet);

        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);

        student.viewGrades();
    }

    @Test
    void testviewCourses() throws SQLException {
        MockedStatic<Utils>mockedUtils=mockStatic(Utils.class);
        MockedStatic<dbStudent>dbStudentMockedStatic = Mockito.mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("CS201");
        when(resultSet.getString(2)).thenReturn("Data Structures");
        dbStudentMockedStatic.when(() -> dbStudent.fetchCourses(anyString(),anyString())).thenReturn(resultSet);

        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);

        student.viewCourses();
    }

    @Test
    void testDropCourseAdditional() throws SQLException {
        MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = Mockito.mockStatic(dbStudent.class);
        utilsMockedStatic.when(Utils::getCurrentSession).thenReturn(null);
        User user = new User("student", "test@yopmail.com");
        Student student = new Student(user);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        student.dropCourse();
        assert(outContent.toString().contains("No session is currently active"));
        utilsMockedStatic.when(Utils::getCurrentSession).thenReturn("2022-2");
        utilsMockedStatic.when(() -> Utils.validateEventTime(anyString(),anyString())).thenReturn(false);
        outContent.reset();
        student.dropCourse();
        assert(outContent.toString().contains("You are not allowed to drop courses now"));
        utilsMockedStatic.when(() -> Utils.validateEventTime(anyString(),anyString())).thenReturn(true);
        dbStudentMockedStatic.when(() -> dbStudent.dropCourse(anyString(),anyString(),anyString())).thenReturn(1);
        utilsMockedStatic.when(() -> Utils.getInput("Enter the course code of the course you want to drop. Press -1 to go back")).thenReturn("-1");
        outContent.reset();
        student.dropCourse();
    }

    @Test
    void testDropCourse() throws SQLException {
        MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = Mockito.mockStatic(dbStudent.class);
        User user = new User("student", "test@yopmail.com");
        Student student = new Student(user);
        utilsMockedStatic.when(Utils::getCurrentSession).thenReturn("2022-2");
        utilsMockedStatic.when(() -> Utils.validateEventTime(anyString(),anyString())).thenReturn(true);
        dbStudentMockedStatic.when(() -> dbStudent.dropCourse(anyString(),anyString(),anyString())).thenReturn(1);
        utilsMockedStatic.when(() -> Utils.getInput("Enter the course code of the course you want to drop. Press -1 to go back")).thenReturn("CS101");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        student.dropCourse();
        assert (outContent.toString().contains("You have successfully dropped the course"));
        dbStudentMockedStatic.when(() -> dbStudent.dropCourse(anyString(),anyString(),anyString())).thenReturn(0);
        outContent.reset();
        student.dropCourse();
        assert (outContent.toString().contains("You are not registered for this course"));

    }

    @Test
    void testRegisterCourse_Nil() throws SQLException{
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        student.registerCourse();
        assert(outContent.toString().contains("No Courses are available for registration"));

    }
    @Test
    void testRegisterCourseback() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
        callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
        MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = Mockito.mockStatic(dbStudent.class);
        User user = new User("student", "2020csb1066@iirpr.ac.in");
        Student student = new Student(user);
        utilsMockedStatic.when(() -> Utils.getInput("Enter the course code of the course you want to drop. Press -1 to go back")).thenReturn("CS101");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        student.registerCourse();
    }

    @Test
    void testRegisterCourse_T1() throws SQLException {

        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        dbStudentMockedStatic.when(() -> dbStudent.fetchCoursesForRegistration(anyString())).thenReturn(resultSet);
        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        student.registerCourse();
        assertTrue(true);
    }

    @Test
    void testRegisterCourse_T2() throws SQLException {

        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the course you want to register. Press -1 to go back")).thenReturn("-1");


        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("CS201");
        when(resultSet.getString(2)).thenReturn("Data Structures");
        when(resultSet.getString(3)).thenReturn("Apurva Mudgal");
        dbStudentMockedStatic.when(() -> dbStudent.fetchCoursesForRegistration(anyString())).thenReturn(resultSet);

        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);
        student.registerCourse();
        assertTrue(true);
    }

    @Test
    void testRegisterCourse_T3() throws SQLException {

        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the course you want to register. Press -1 to go back")).thenReturn("CS201");


        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("CS201");
        when(resultSet.getString(2)).thenReturn("Data Structures");
        when(resultSet.getString(3)).thenReturn("Apurva Mudgal");
        dbStudentMockedStatic.when(() -> dbStudent.fetchCoursesForRegistration(anyString())).thenReturn(resultSet);

        dbStudentMockedStatic.when(() -> dbStudent.checkEnrollmentAvailability(anyString(),anyString())).thenReturn(false);

        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        student.registerCourse();
        assert (outContent.toString().contains("Course not available for registration. Please Choose the course code from the list"));
    }

    @Test
    void testRegisterCourse_T4() throws SQLException {

        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the course you want to register. Press -1 to go back")).thenReturn("CS201");


        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("CS201");
        when(resultSet.getString(2)).thenReturn("Data Structures");
        when(resultSet.getString(3)).thenReturn("Apurva Mudgal");
        dbStudentMockedStatic.when(() -> dbStudent.fetchCoursesForRegistration(anyString())).thenReturn(resultSet);

        dbStudentMockedStatic.when(() -> dbStudent.checkEnrollmentAvailability(anyString(),anyString())).thenReturn(true);

        dbStudentMockedStatic.when(()->dbStudent.computeGPA(anyString())).thenReturn(8.0);
        dbStudentMockedStatic.when(()->dbStudent.fetchMinCGPA(anyString(),anyString())).thenReturn(9.0);

        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        student.registerCourse();
        assert (outContent.toString().contains("You do not meet the minimum CGPA requirement for this course"));
    }

    @Test
    void testRegisterCourse_T5() throws SQLException {

        MockedStatic<Utils> mockedUtils = mockStatic(Utils.class);
        MockedStatic<dbStudent> dbStudentMockedStatic = mockStatic(dbStudent.class);

        mockedUtils.when(Utils::getCurrentSession).thenReturn("2022-2");
        mockedUtils.when(() -> Utils.getInput("Enter the course code of the course you want to register. Press -1 to go back")).thenReturn("CS201");


        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("CS201");
        when(resultSet.getString(2)).thenReturn("Data Structures");
        when(resultSet.getString(3)).thenReturn("Apurva Mudgal");
        dbStudentMockedStatic.when(() -> dbStudent.fetchCoursesForRegistration(anyString())).thenReturn(resultSet);

        dbStudentMockedStatic.when(() -> dbStudent.checkEnrollmentAvailability(anyString(),anyString())).thenReturn(true);

        dbStudentMockedStatic.when(()->dbStudent.computeGPA(anyString())).thenReturn(9.5);
        dbStudentMockedStatic.when(()->dbStudent.fetchMinCGPA(anyString(),anyString())).thenReturn(9.0);

        dbStudentMockedStatic.when(()->dbStudent.getCoursePrerequisite(anyString(),anyString())).thenReturn(false);

        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        student.registerCourse();
        assert (outContent.toString().contains("You do not meet the minimum CGPA requirement for this course"));
    }
}