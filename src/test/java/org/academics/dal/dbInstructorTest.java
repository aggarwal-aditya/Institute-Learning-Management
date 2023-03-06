package org.academics.dal;

import com.opencsv.CSVWriter;
import org.academics.users.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class dbInstructorTest {
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
    public void testFetchInstructorID() throws SQLException {
        // Test fetching instructor ID for a valid instructor email ID
        User user = new User("instructor", "mudgal@yopmail.com");
        assert (dbInstructor.fetchInstructorID(user) == 1);
    }

    @Test
    public void testFetchCourses() throws SQLException {
        int INSTRUCTOR_ID = 1;
        ResultSet rs = dbInstructor.fetchCourses(INSTRUCTOR_ID);
        Assertions.assertNotNull(rs);
    }

    @Test
    public void testFetchApprovedCourses() throws SQLException {
        // Test fetching approved courses
        ResultSet rs = dbInstructor.fetchApprovedCourses();
        Assertions.assertNotNull(rs);
    }

    @Test
    public void testCheckCourseApproval() throws SQLException {
        // Test checking course approval for a valid course ID
        String COURSE_ID = "CS202";
        assert (dbInstructor.checkCourseApproval(COURSE_ID));
        String COURSE_ID2 = "CS101";
        assert (!dbInstructor.checkCourseApproval(COURSE_ID2));
    }

    @Test
    public void testFetchCourseGrades() throws SQLException {
        // Test fetching course details for a valid course ID
        String COURSE_ID = "CS202";
        String SEMESTER = "2022-2";
        ResultSet rs = dbInstructor.fetchCourseGrades(COURSE_ID, SEMESTER);
        Assertions.assertNotNull(rs);
    }

    @Test
    public void testFetchStudentGrades() throws SQLException {
        // Test fetching course details for a valid course ID
        String STUDENT_ID = "2020CSB1066";
        ResultSet rs = dbInstructor.fetchStudentGrades(STUDENT_ID);
        Assertions.assertNotNull(rs);
    }

    @Test
    public void testisCourseInstructor() throws SQLException {
        // Test fetching course details for a valid course ID
        String COURSE_ID = "CS202";
        int INSTRUCTOR_ID = 1;
        String Semester = "2022-1";
        assert (dbInstructor.isCourseInstructor(COURSE_ID, Semester, INSTRUCTOR_ID));

        String COURSE_ID2 = "CS101";
        assert (!dbInstructor.isCourseInstructor(COURSE_ID2, Semester, INSTRUCTOR_ID));
    }

    @Test
    public void testisCourseFloated() throws SQLException {
        // Test fetching course details for a valid course ID
        String COURSE_ID = "CS202";
        String Semester = "2022-1";
        assert (Objects.equals(dbInstructor.isCourseFloated(COURSE_ID, Semester), "The course is already floated by Dr.Apurva Mudgal"));
        String COURSE_ID2 = "CS101";
        assert (dbInstructor.isCourseFloated(COURSE_ID2, Semester) == null);
    }

    @Test
    public void testdelistCourse() throws SQLException {
        // Test fetching course details for a valid course ID
        String COURSE_ID = "CS202";
        String Semester = "2022-1";
        int INSTRUCTOR_ID = 1;
        assert (dbInstructor.delistCourse(COURSE_ID, Semester, INSTRUCTOR_ID));
    }

    @Test
    public void testfetchStudentList() throws SQLException {
        // Test fetching course details for a valid course ID
        String COURSE_ID = "CS202";
        String Semester = "2022-1";
        ResultSet rs = dbInstructor.fetchStudentsList(COURSE_ID, Semester);
        Assertions.assertNotNull(rs);
    }

    @Test
    public void testfloatCourse() throws SQLException {
        String COURSE_ID = "CS201";
        String Semester = "2022-1";
        int INSTRUCTOR_ID = 1;
        ArrayList<String> preRequisites = new ArrayList<>();
        assert (dbInstructor.floatCourse(COURSE_ID, Semester, INSTRUCTOR_ID, 10, 0, preRequisites));

    }

    @Test
    public void testUploadGradesFileNotFound() throws SQLException, IOException {
        // Call the method being tested with a file that doesn't exist
        int numGradesUploaded = dbInstructor.uploadGrades("nonexistent_file.csv", "COMP101", "Fall2022");
        Assertions.assertEquals(0, numGradesUploaded);

    }

    @Test
    public void testUploadGrades() throws SQLException, IOException {
        // Create a mock CSV file with sample data
        String filePath = "sample_file.csv";
        String course_code = "CS202";
        String semester = "2022-1";
        String[][] data = {{"enrollment_id", "student_id", "course_code", "grade"},
                {"2", "2020CSB1066", "CS202", "A"},
                {"3", "2020CSB1067", "CS202", "B"}};
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        writer.writeAll(Arrays.asList(data));
        writer.close();

        // Set up a mock database connection


        // Call the method being tested
        int numGradesUploaded = dbInstructor.uploadGrades(filePath, course_code, semester);
        // Check that the expected number of grades were uploaded
        Assertions.assertEquals(1, numGradesUploaded);
    }

    @Test
    public void testupdateCourseMappings() throws SQLException {
        String COURSE_ID = "CS201";
        String Semester = "2022-2";
        int INSTRUCTOR_ID = 1;
        ArrayList<Integer> departmentID = new ArrayList<>();
        departmentID.add(1);
        ArrayList<Integer> batches = new ArrayList<>();
        batches.add(2020);
        ArrayList<String> courseTypes = new ArrayList<>();
        courseTypes.add("Core");
        dbInstructor.updateCourseMapping(COURSE_ID, Semester, departmentID, batches, courseTypes);


    }

}