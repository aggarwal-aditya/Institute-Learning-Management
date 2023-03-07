package org.academics.users;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbAdmin;
import org.academics.utility.CurrentDate;
import org.academics.utility.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Admin extends User {
    Scanner scanner = new Scanner(System.in);
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Admin() {
    }

    public void updateCourseCatalog() throws SQLException {
        String courseCode = Utils.getInput("Enter the course code");
        String courseName = Utils.getInput("Enter the course name");
        //take input for credit structure which is 5 integers, L T P S C
        double L = Double.parseDouble(Utils.getInput("Enter the number of lecture hours"));
        double T = Double.parseDouble(Utils.getInput("Enter the number of tutorial hours"));
        double P = Double.parseDouble(Utils.getInput("Enter the number of practical hours"));
        double S = Double.parseDouble(Utils.getInput("Enter the number of self study hours"));
        double C = Double.parseDouble(Utils.getInput("Enter the number of credits"));
        ArrayList<String> preRequisites = specialPrivileges.getPreRequisites();
        if (dbAdmin.updateCourseCatalog(courseCode, courseName, L, T, P, S, C, preRequisites)) {
            System.out.println("Course added successfully");
        } else {
            System.out.println("Course could not be added");
        }
    }

    public void addSemesterTimeline() throws SQLException {
        String semester = Utils.getInput("Enter the semester:(YYYY-Semester)");
        String year = semester.substring(0, 4);
        String sem = semester.substring(5);
        String start_date = Utils.getInput("Enter the start date:(YYYY-MM-DD)");
        String end_date = Utils.getInput("Enter the end date:(YYYY-MM-DD)");
        String grade_submission_date = Utils.getInput("Enter the grade submission date:(YYYY-MM-DD)");
        String grade_release_date = Utils.getInput("Enter the grade release/submission end date:(YYYY-MM-DD)");
        String course_add_drop_start_date = Utils.getInput("Enter the course add/drop start date:(YYYY-MM-DD)");
        String course_add_drop_end_date = Utils.getInput("Enter the course add/drop end date:(YYYY-MM-DD)");
        String course_float_start_date = Utils.getInput("Enter the course float start date:(YYYY-MM-DD)");
        String course_float_end_date = Utils.getInput("Enter the course float end date:(YYYY-MM-DD)");
        dbAdmin.addSemesterTimeline(year, sem, start_date, end_date, grade_submission_date, grade_release_date, course_float_start_date, course_float_end_date, course_add_drop_start_date, course_add_drop_end_date);
        System.out.println("Semester timeline added successfully");
    }

    public void changeSystemSettings() {
        System.out.println("1.Change System Time & Date");
        System.out.println("2.Go Back to main menu");
        int choice = Utils.getUserChoice(2);
        switch (choice) {
            case 1:
                CurrentDate currentDate = CurrentDate.getInstance();
                int year = Integer.parseInt(Utils.getInput("Enter the year:"));
                int month = Integer.parseInt(Utils.getInput("Enter the month:"));
                int day = Integer.parseInt(Utils.getInput("Enter the day:"));
                currentDate.overwriteCurrentDate(year, month, day);
                System.out.println("System date changed successfully");
                break;
            case 2:
                break;
        }
    }

    private BigDecimal computeGPA(String enrollment_id) throws SQLException{
            CallableStatement calculateCGPA = conn.prepareCall("{? = call calculate_cgpa(?)}");
            calculateCGPA.registerOutParameter(1, Types.NUMERIC);
            calculateCGPA.setString(2, enrollment_id);
            calculateCGPA.execute();
            return calculateCGPA.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP);
    }

    public void generateTranscript() throws SQLException {
        String enrollment_id = Utils.getInput("Enter the student's enrollment id:");
        PreparedStatement getTranscript = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.semester,course_enrollments.grade FROM course_enrollments JOIN course_catalog ON course_enrollments.course_code=course_catalog.course_code JOIN students ON course_enrollments.student_id = students.student_id WHERE course_enrollments.student_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getTranscript.setString(1, enrollment_id);
        ResultSet resultSet = getTranscript.executeQuery();
        Utils.exportTxt(resultSet, enrollment_id + "_transcript", "Your CGPA is " + computeGPA(enrollment_id));
        System.out.println("Transcript generated successfully");
    }

    public boolean checkGraduationStatus() throws SQLException {
        final double coreCreditsRequired = 71.5;
        final double projectCreditsRequired = 9;
        final double internshipCreditsRequired = 3.5;
        final double openElectiveCreditsRequired = 6;
        final double scienceMathCreditsRequired = 30;
        final double humanitiesCreditsRequired = 21;
        final double extraCurricularCreditsRequired = 4;
        final double minCreditsRequired = 145;
        //declare const double


        String enrollmentID = Utils.getInput("Enter the student's enrollment id:");
        ResultSet studentCourses = dbAdmin.getStudentCourses(enrollmentID);
        if (computeGPA(enrollmentID).compareTo(new BigDecimal("5.0")) < 0) {
            System.out.println("CGPA is less than 5.0. Student cannot graduate");
            return false;
        }
        //check if core and programme_elective credits are completed
        double coreCredits = 0;
        double projectCredits = 0;
        double internshipCredits = 0;
        double openElectiveCredits = 0;
        double scienceMathCredits = 0;
        double humanitiesCredits = 0;
        double extraCurricularCredits = 0;

        while (studentCourses.next()) {
            if (!Objects.equals(studentCourses.getString("grade"), "F")) {
                if (studentCourses.getString("course_type").equals("core") || studentCourses.getString("course_type").equals("programme_elective")) {
                    coreCredits += studentCourses.getInt("credits");
                }
                if (studentCourses.getString("course_type").equals("project")) {
                    projectCredits += studentCourses.getInt("credits");
                }
                if (studentCourses.getString("course_type").equals("internship")) {
                    internshipCredits += studentCourses.getInt("credits");
                }
                if (studentCourses.getString("course_type").equals("open_elective")) {
                    openElectiveCredits += studentCourses.getInt("credits");
                }
                if (studentCourses.getString("course_type").equals("science_math")) {
                    scienceMathCredits += studentCourses.getInt("credits");
                }
                if (studentCourses.getString("course_type").equals("humanities")) {
                    humanitiesCredits += studentCourses.getInt("credits");
                }
                if (studentCourses.getString("course_type").equals("extra_curricular")) {
                    extraCurricularCredits += studentCourses.getInt("credits");
                }
            }
        }
        double totalCredits = coreCredits + projectCredits + internshipCredits + openElectiveCredits + scienceMathCredits + humanitiesCredits + extraCurricularCredits;
        if (coreCredits < coreCreditsRequired || projectCredits < projectCreditsRequired || internshipCredits < internshipCreditsRequired || scienceMathCredits < scienceMathCreditsRequired || humanitiesCredits < humanitiesCreditsRequired || extraCurricularCredits < extraCurricularCreditsRequired|| totalCredits < minCreditsRequired) {
            System.out.println("Student cannot graduate");
            return false;
        }
        return true;
    }
}
