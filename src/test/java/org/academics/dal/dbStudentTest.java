package org.academics.dal;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.math.RoundingMode;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class dbStudentTest {


    private static final String STUDENT_ID = "2020CSB1066";
    private static final String CURRENT_SEMESTER = "2022-2";
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();


    @BeforeEach
    void setUp() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }
    @Test
    public void testComputeGPA() throws SQLException {
        // Test a valid student ID
        double expectedGPA = 9.57;
        double actualGPA = dbStudent.computeGPA(STUDENT_ID);
        assertEquals(expectedGPA, actualGPA, 0.01);
    }

    @Test
    public void testFetchGrades() throws SQLException {
        // Test fetching grades for a valid student ID and semester
        String tillSem = "2022-2";
        ResultSet rs = dbStudent.fetchGrades(STUDENT_ID, tillSem);
        assertTrue(rs.next());
    }

    @Test
    public void testFetchCourses() throws SQLException {
        // Test fetching courses for a valid student ID and semester
        ResultSet rs = dbStudent.fetchCourses(STUDENT_ID, CURRENT_SEMESTER);
        assertTrue(rs.next());
    }

    @Test
    public void testDropCourse() throws SQLException {
        // Test dropping a valid course for a valid student ID and semester
        String courseCode = "CS201";
        int expectedRowsAffected = 1;
        int actualRowsAffected = dbStudent.dropCourse(STUDENT_ID, courseCode, CURRENT_SEMESTER);
        assertEquals(expectedRowsAffected, actualRowsAffected);

        courseCode="CS202";
        expectedRowsAffected =0;
        actualRowsAffected = dbStudent.dropCourse(STUDENT_ID, courseCode, CURRENT_SEMESTER);
        assertEquals(expectedRowsAffected, actualRowsAffected);

    }

    @Test
    public void testFetchCoursesForRegistration() throws SQLException {
        ResultSet result = dbStudent.fetchCoursesForRegistration("2022-2");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.next());
    }

    @Test
    public void testFetchMinCGPA() throws SQLException {
        double result = dbStudent.fetchMinCGPA("CS201", "2022-2");
        Assertions.assertEquals(0.0, result);
    }

    @Test
    public void testGetCoursePrerequisite() throws SQLException {
        ResultSet result = dbStudent.getCoursePrerequisite("CS201", "2022-2");
        Assertions.assertNotNull(result);
    }

    @Test
    public void testEnrollCourse() throws SQLException {
        boolean result = dbStudent.enrollCourse("2020CSB1066", "CS201", "2022-2");
        Assertions.assertFalse(result);
        dbStudent.dropCourse(STUDENT_ID, "CS201", CURRENT_SEMESTER);
        result = dbStudent.enrollCourse("2020CSB1066", "CS201", "2022-2");
        Assertions.assertTrue(result);
    }
    @Test
    public void testCheckEnrollmentAvailability() throws SQLException {
        boolean result = dbStudent.checkEnrollmentAvailability("CS201", "2022-2");
        Assertions.assertTrue(result);
        result = dbStudent.checkEnrollmentAvailability("CS201", "2022-1");
        Assertions.assertFalse(result);
    }



}