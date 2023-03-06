package org.academics.dal;

import java.sql.*;
import java.util.ArrayList;

/**
 * A database access class for managing admin actions.
 */
public class dbAdmin {
    private static final JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    private static final Connection conn = jdbc.getConnection();

    /**
     * Adds a new semester timeline to the database.
     *
     * @param year                       the year of the semester
     * @param sem                        the semester
     * @param start_date                 the start date of the semester
     * @param end_date                   the end date of the semester
     * @param grade_submission_date      the deadline for submitting grades
     * @param grade_release_date         the date when grades are released to students
     * @param course_float_start_date    the start date of the course floating period
     * @param course_float_end_date      the end date of the course floating period
     * @param course_add_drop_start_date the start date of the course add/drop period
     * @param course_add_drop_end_date   the end date of the course add/drop period
     * @throws SQLException if an error occurs while executing the SQL query
     */

    public static void addSemesterTimeline(String year, String sem, String start_date, String end_date, String grade_submission_date, String grade_release_date, String course_float_start_date, String course_float_end_date, String course_add_drop_start_date, String course_add_drop_end_date) throws SQLException {
        PreparedStatement addSemesterTimeline = conn.prepareStatement("INSERT INTO semester VALUES (?,?,?,?,?,?,?,?,?,?)");
        addSemesterTimeline.setInt(1, Integer.parseInt(year));
        addSemesterTimeline.setInt(2, Integer.parseInt(sem));
        addSemesterTimeline.setDate(3, Date.valueOf(start_date));
        addSemesterTimeline.setDate(4, Date.valueOf(end_date));
        addSemesterTimeline.setDate(5, Date.valueOf(grade_submission_date));
        addSemesterTimeline.setDate(6, Date.valueOf(grade_release_date));
        addSemesterTimeline.setDate(7, Date.valueOf(course_float_start_date));
        addSemesterTimeline.setDate(8, Date.valueOf(course_float_end_date));
        addSemesterTimeline.setDate(9, Date.valueOf(course_add_drop_start_date));
        addSemesterTimeline.setDate(10, Date.valueOf(course_add_drop_end_date));
        addSemesterTimeline.executeUpdate();
    }


    /**
     * Updates the course catalog with the given information.
     *
     * @param courseCode    the code of the course being updated
     * @param courseName    the name of the course being updated
     * @param L             the number of lecture hours per week
     * @param T             the number of tutorial hours per week
     * @param P             the number of practical hours per week
     * @param S             the number of self-study hours per week
     * @param C             the number of credits for the course
     * @param preRequisites a list of prerequisite course codes
     * @return true if the update was successful, false otherwise
     * @throws SQLException if an error occurs while executing the SQL query
     */
    public static boolean updateCourseCatalog(String courseCode, String courseName, double L, double T, double P, double S, double C, ArrayList<String> preRequisites) throws SQLException {
        PreparedStatement updateCourseCatalog = conn.prepareStatement("INSERT INTO course_catalog VALUES (?,?,?,?)");
        updateCourseCatalog.setString(1, courseCode);
        updateCourseCatalog.setString(2, courseName);
        updateCourseCatalog.setArray(3, conn.createArrayOf("numeric", new Double[]{L, T, P, S, C}));
        updateCourseCatalog.setArray(4, conn.createArrayOf("text", preRequisites.toArray()));
        updateCourseCatalog.executeUpdate();
        return (updateCourseCatalog.getUpdateCount() > 0);

    }

    /**
     * Retrieves the courses enrolled by a student based on their student ID.
     *
     * @param studentId The ID of the student for whom the course information is to be retrieved.
     * @return The ResultSet containing the courses enrolled by the student.
     * @throws SQLException If an error occurs while executing the SQL query.
     */
    public static ResultSet getStudentCourses(String studentId) throws SQLException {
        PreparedStatement getStudentDetails = conn.prepareStatement("SELECT department_id,batch FROM students WHERE student_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getStudentDetails.setString(1, studentId);
        ResultSet studentDetails = getStudentDetails.executeQuery();
        int departmentId = 0;
        int batch = 0;
        while (studentDetails.next()) {
            departmentId = studentDetails.getInt("department_id");
            batch = studentDetails.getInt("batch");
        }
        PreparedStatement getStudentCourses = conn.prepareStatement("SELECT ce.course_code,ce.semester,ce.grade,course_type, cc.credit_str[5] as credits FROM course_enrollments ce JOIN course_mappings cm ON (ce.semester=cm.semester) JOIN course_catalog cc ON (ce.course_code=cc.course_code)  WHERE student_id = ? AND grade!=? AND department_id=? AND batch=?");
        getStudentCourses.setString(1, studentId);
        getStudentCourses.setString(2, "NA");
        getStudentCourses.setInt(3, departmentId);
        getStudentCourses.setInt(4, batch);
        return getStudentCourses.executeQuery();
    }

}
