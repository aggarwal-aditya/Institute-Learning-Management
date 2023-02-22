package org.academics.users;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.utility.CurrentDate;
import org.academics.utility.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.Scanner;

public class Admin extends User{
    Scanner scanner = new Scanner(System.in);
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Admin() {
    }
    public void updateCourseCatalog(){

    }
    public void addSemesterTimeline(){
        String semester = Utils.getInput("Enter the semester:(YYYY-Semester)");
        String year= semester.substring(0,4);
        String sem = semester.substring(5);
        String start_date = Utils.getInput("Enter the start date:(YYYY-MM-DD)");
        String end_date = Utils.getInput("Enter the end date:(YYYY-MM-DD)");
        String grade_submission_date = Utils.getInput("Enter the grade submission date:(YYYY-MM-DD)");
        String grade_release_date = Utils.getInput("Enter the grade release/submission end date:(YYYY-MM-DD)");
        String course_add_drop_start_date = Utils.getInput("Enter the course add/drop start date:(YYYY-MM-DD)");
        String course_add_drop_end_date = Utils.getInput("Enter the course add/drop end date:(YYYY-MM-DD)");
        String course_float_start_date = Utils.getInput("Enter the course float start date:(YYYY-MM-DD)");
        String course_float_end_date = Utils.getInput("Enter the course float end date:(YYYY-MM-DD)");
        try {
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
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void changeSystemSettings(){
        System.out.println("1.Change System Time & Date");
        System.out.println("2.Go Back to main menu");
        int choice = Utils.getUserChoice(2);
        switch (choice){
            case 1:
                CurrentDate currentDate = CurrentDate.getInstance();
                System.out.println("Enter the year:");
                int year = scanner.nextInt();
                System.out.println("Enter the month:");
                int month = scanner.nextInt();
                System.out.println("Enter the day:");
                int day = scanner.nextInt();
                currentDate.overwriteCurrentDate(year, month, day);
                break;
            case 2:
                break;
        }
    }

    private BigDecimal computeGPA(String enrollment_id) {
        try {
            CallableStatement calculateCGPA = conn.prepareCall("{? = call calculate_cgpa(?)}");
            calculateCGPA.registerOutParameter(1, Types.NUMERIC);
            calculateCGPA.setString(2, enrollment_id);
            calculateCGPA.execute();
            return calculateCGPA.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void generateTranscript(){
        try {
            String enrollment_id = Utils.getInput("Enter the student's enrollment id:");
            PreparedStatement getTranscript = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.semester,course_enrollments.grade FROM course_enrollments JOIN course_catalog ON course_enrollments.course_code=course_catalog.course_code JOIN students ON course_enrollments.student_id = students.student_id WHERE course_enrollments.student_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getTranscript.setString(1, enrollment_id);
            ResultSet resultSet = getTranscript.executeQuery();
            Utils.exportTxt(resultSet, enrollment_id+"transcript.txt","Your CGPA is "+computeGPA(enrollment_id));
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkGraduationStatus(){
        System.out.println("Enter the student's enrollment id:");
        String enrollment_id = scanner.next();

    }

}
