package org.academics.dal;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbAdmin;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.ArrayList;

class dbAdminTest {
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection connection = jdbc.getConnection();

    @BeforeEach
    void setUp() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call clear_database()");
        callableStatement.execute();
    }


    @Test
    void testAddSemesterTimeline() throws SQLException {
        // Given
        String year = "2023";
        String sem = "1";
        String start_date = "2023-01-01";
        String end_date = "2023-05-31";
        String grade_submission_date = "2023-06-10";
        String grade_release_date = "2023-06-15";
        String course_float_start_date = "2023-02-01";
        String course_float_end_date = "2023-02-15";
        String course_add_drop_start_date = "2023-01-20";
        String course_add_drop_end_date = "2023-02-10";

        // When
        dbAdmin.addSemesterTimeline(year, sem, start_date, end_date, grade_submission_date, grade_release_date,
                course_float_start_date, course_float_end_date, course_add_drop_start_date, course_add_drop_end_date);

        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM semester WHERE year = ? AND semester_number = ?");
        pstmt.setInt(1, Integer.parseInt(year));
        pstmt.setInt(2, Integer.parseInt(sem));
        ResultSet rs = pstmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(start_date, rs.getString("start_date"));
        assertEquals(end_date, rs.getString("end_date"));
        assertEquals(grade_submission_date, rs.getString("grades_submission_start_date"));
        assertEquals(grade_release_date, rs.getString("grades_submission_end_date"));
        assertEquals(course_float_start_date, rs.getString("course_float_start_date"));
        assertEquals(course_float_end_date, rs.getString("course_float_end_date"));
        assertEquals(course_add_drop_start_date, rs.getString("course_add_drop_start_date"));
        assertEquals(course_add_drop_end_date, rs.getString("course_add_drop_end_date"));
    }


    @Test
    void testUpdateCourseCatalog() throws SQLException {
        // Given
        String courseCode = "CS101";
        String courseName = "Introduction to Computer Science";
        double L = 2.0;
        double T = 1.0;
        double P = 2.0;
        double S = 4.0;
        double C = 4.0;
        ArrayList<String> preRequisites = new ArrayList<>();
        preRequisites.add("MT101");
        boolean result = dbAdmin.updateCourseCatalog(courseCode, courseName, L, T, P, S, C, preRequisites);
        assert (result);
    }
    @Test
    void testGetStudentCourses() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call populate_database()");
        callableStatement.execute();
        ResultSet rs = dbAdmin.getStudentCourses("2020CSB1066");
        assertNotNull(rs);
    }

}

