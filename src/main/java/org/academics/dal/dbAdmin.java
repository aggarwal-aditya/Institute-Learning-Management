package org.academics.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class dbAdmin {
    private static final JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    private static final Connection conn = jdbc.getConnection();

    public static void addSemesterTimeline(String year,String sem,String start_date,String end_date,String grade_submission_date,String grade_release_date,String course_float_start_date,String course_float_end_date,String course_add_drop_start_date, String course_add_drop_end_date) throws SQLException {
        PreparedStatement addSemesterTimeline = conn.prepareStatement("INSERT INTO semester VALUES (?,?,?,?,?,?,?,?,?,?)");
        addSemesterTimeline.setString(1,year);
        addSemesterTimeline.setString(2,sem);
        addSemesterTimeline.setString(3,start_date);
        addSemesterTimeline.setString(4,end_date);
        addSemesterTimeline.setString(5,grade_submission_date);
        addSemesterTimeline.setString(6,grade_release_date);
        addSemesterTimeline.setString(7,course_float_start_date);
        addSemesterTimeline.setString(8,course_float_end_date);
        addSemesterTimeline.setString(9,course_add_drop_start_date);
        addSemesterTimeline.setString(10,course_add_drop_end_date);
        addSemesterTimeline.executeUpdate();
    }


    public static boolean updateCourseCatalog(String courseCode, String courseName, double L, double T, double P, double S, double C, ArrayList<String> preRequisites) throws SQLException {
        PreparedStatement updateCourseCatalog = conn.prepareStatement("INSERT INTO course_catalog VALUES (?,?,?,?)");
        updateCourseCatalog.setString(1,courseCode);
        updateCourseCatalog.setString(2,courseName);
        updateCourseCatalog.setArray(3, conn.createArrayOf("double precision", new Double[]{L, T, P, S, C}));
        updateCourseCatalog.setArray(4, conn.createArrayOf("text", preRequisites.toArray()));
        updateCourseCatalog.executeUpdate();
        return (updateCourseCatalog.getUpdateCount() > 0);

    }

    public static ResultSet getStudentCourses(String studentId) throws SQLException {
        PreparedStatement getStudentDetails = conn.prepareStatement("SELECT department_id,batch FROM students WHERE student_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getStudentDetails.setString(1, studentId);
        ResultSet studentDetails = getStudentDetails.executeQuery();
        studentDetails.next();
        int departmentId = studentDetails.getInt("department_id");
        int batch = studentDetails.getInt("batch");
        PreparedStatement getStudentCourses = conn.prepareStatement("SELECT ce.course_code,ce.semester,ce.grade,course_type, cc.credit_str[5] as credits FROM course_enrollments ce JOIN course_mappings cm ON (ce.semester=cm.semester) JOIN course_catalog cc ON (ce.course_code=cc.course_code)  WHERE student_id = ? AND grade!=? AND department_id=? AND batch=?");
        getStudentCourses.setString(1, studentId);
        getStudentCourses.setString(2, "NA");
        getStudentCourses.setInt(3, departmentId);
        getStudentCourses.setInt(4, batch);
        return getStudentCourses.executeQuery();
    }
}
