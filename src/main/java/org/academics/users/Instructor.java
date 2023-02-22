package org.academics.users;

import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbInstructor;
import org.academics.utility.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class contains methods that are only accessible to the instructor
 */
public class Instructor extends User {
    private final int instructor_id;
    Scanner scanner = new Scanner(System.in);

    /**
     * Constructor for Instructor class
     *
     * @param user User object
     */
    public Instructor(User user){
        super(user.userRole, user.email_id);
        try {
            this.instructor_id = dbInstructor.fetchInstructorID(this);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    /**

     This method allows an instructor to float a new course.

     @throws SQLException if there is an error accessing the database
     */

    public void floatCourse() throws SQLException {

            ResultSet approvedCourses = dbInstructor.fetchApprovedCourses();
            String successMessage = "List of courses approved by Senate";
            String failureMessage = "No courses have been approved by Senate yet";
            Utils.printTable(approvedCourses, new String[]{"Course Code", "Course Name"}, successMessage, failureMessage);

            String course_code = Utils.getInput("Enter the course code to float the course");

            if (!dbInstructor.checkCourseApproval(course_code)) {
                System.out.println("Course not approved by Senate");
                return;
            }
            String session = Utils.getInput("Enter the session (YYYY-Semester)");
            if (!Utils.validateEventTime("course_float", session)) {
                System.out.println("Course floatation for the specified semester is not allowed at this time");
                return;
            }
            String isFloated= dbInstructor.isCourseFloated(course_code, session);
            if (isFloated!=null) {
                System.out.println(isFloated);
                return;
            }

            System.out.println("Enter the minimum CGPA requirement for the course");
            double qualify = scanner.nextDouble();

            String choice = Utils.getInput("Do you want to add additional prerequisites? (Y/N)");
            //Make a 2d array of prerequisites
            ArrayList<String> preRequisites = new ArrayList<>();
            StringBuilder pre = new StringBuilder();
            if (Objects.equals(choice, "Y")) {
                do {
                    String code = Utils.getInput("Enter the course code of the prerequisite");
                    String grade = Utils.getInput("Enter the minimum grade requirement for the prerequisite (Enter 'E' if no minimum grade requirement)");
                    pre.append(code).append("(").append(grade).append(")").append("|");
                    choice = Utils.getInput("Are there any alternatives to the prerequisite? (Y/N)");
                    if (Objects.equals(choice, "Y")) {
                        do {
                            code = Utils.getInput("Enter the course code of the alternative");
                            grade = Utils.getInput("Enter the minimum grade requirement for the alternative (Enter 'E' if no minimum grade requirement)");
                            pre.append(code).append("(").append(grade).append(")").append("|");
                            choice = Utils.getInput("Are there any alternatives to the prerequisite? (Y/N)");
                        } while (Objects.equals(choice, "Y"));
                    }
                    choice = Utils.getInput("Do you want to add additional prerequisites? (Y/N)");
                    preRequisites.add(String.valueOf(pre));
                    pre = new StringBuilder();
                } while (Objects.equals(choice, "Y"));
            }

            System.out.println("Program Core & Elective Selection");
            List<Integer> departmentIds = new ArrayList<>();
            List<Integer> batches = new ArrayList<>();
            List<String> courseTypes = new ArrayList<>();
            System.out.println("Enter department ID (press -1 to stop entering):");
            int departmentId = scanner.nextInt();
            while (departmentId != -1) {
                System.out.println("Enter batch:");
                int batch = scanner.nextInt();
                String courseType = Utils.getInput("Enter course type (core, humanities_elective, programme_elective, science_math_elective, open_elective, internship, btech_project):");
                departmentIds.add(departmentId);
                batches.add(batch);
                courseTypes.add(courseType);
                System.out.println("Enter department ID (press -1 to stop entering):");
                departmentId = scanner.nextInt();
            }
            if(dbInstructor.floatCourse(course_code, session, this.instructor_id,0,qualify,preRequisites))
            {
                System.out.println("Course floated successfully");
            }
            dbInstructor.updateCourseMapping(course_code, session, departmentIds, batches, courseTypes);


    }


    /**
     * Displays the list of courses floated by the instructor.
     *
     * @return true if there are courses available, false otherwise
     * @throws SQLException if there is an SQL error
     */
    public boolean viewCourses() throws SQLException {
        ResultSet instructorCourses = dbInstructor.fetchCourses(this.instructor_id);
        String successMessage = "List of courses floated by you";
        String failureMessage = "You have not floated any courses yet";
        // Displays the list of courses using the printTable() utility method
        Utils.printTable(instructorCourses, new String[]{"Course Code", "Semester", "Qualifying Criteria", "Enrollment Count"}, successMessage, failureMessage);
        return instructorCourses.next();
    }


    /**
     * Checks if student list needs to be downloaded, and downloads and exports the list if needed.
     * Then, uploads grades from a CSV file.
     *
     * @throws SQLException if there is an SQL error
     * @throws IOException if there is an I/O error
     */
    public void uploadGrades() throws SQLException, IOException {
        if (shouldDownloadStudentList()) { // check if student list needs to be downloaded
            downloadAndExportStudentList(); // download and export the student list
        }
        uploadGradesFromFile(); // upload grades from a CSV file
    }

    /**
     * Asks user whether to download the list of students enrolled in the course
     * and returns a boolean indicating the choice.
     *
     * @return true if the user chooses to download the list, false otherwise
     */

    private boolean shouldDownloadStudentList() {
        String choice = Utils.getInput("Do you want to download the list of students enrolled in the course? (Y/N)? Note that this may overwrite a file in downloads folder for the same course");
        return Objects.equals(choice, "Y");
    }


    /**
     * Download the list of students enrolled in the course, exports it to a CSV file
     * @throws SQLException If an error occurs while executing SQL queries
     */
    private void downloadAndExportStudentList() throws SQLException {
        String courseCode = Utils.getInput("Enter the course code");
        String session = Utils.getInput("Enter the session (YYYY-Semester)");
        if (!dbInstructor.isCourseInstructor(courseCode, session, this.instructor_id)) {
            System.out.println("You are not authorized to add grades for this course");
            return;
        }
        String[] extraHeaders = new String[]{"grade"};
        ResultSet studentsList = dbInstructor.fetchStudentsList(courseCode, session);
        Utils.exportCSV(studentsList, courseCode + "_" + session, extraHeaders);
        System.out.println("Please Enter the grades for the students in the CSV file");
    }


    /**
     * Allows the instructor to upload grades from a CSV file
     *
     * @throws SQLException if there is an issue with the database
     * @throws IOException  if there is an issue with reading the file
     */
    private void uploadGradesFromFile() throws SQLException, IOException {
        // Prompt the user for the course code and session
        String courseCode = Utils.getInput("Enter the course code");
        String session = Utils.getInput("Enter the session (YYYY-Semester)");

        // Check if grades submission is allowed for the given session
        if (!Utils.validateEventTime("grades_submission", session)) {
            System.out.println("Grades submission is not allowed at this time");
            return;
        }

        // Prompt the user for the path to the CSV file
        String path = Utils.getInput("Enter the path to the CSV file");

        // Check if the instructor is authorized to upload grades for the given course and session
        if (!dbInstructor.isCourseInstructor(courseCode, session, this.instructor_id)) {
            System.out.println("You are not authorized to add grades for this course");
            return;
        }

        // Upload the grades from the CSV file to the database
        dbInstructor.uploadGrades(path, courseCode, session);
    }


    /**
     * Allows an instructor to delist a course from the course offerings for a given session.
     * The instructor can only delist a course if they are the assigned instructor for the course.
     * If the delisting is successful, the function returns a success message, otherwise, a failure message is returned.
     *
     * @throws SQLException if there is an error with the database query
     */
    public void delistCourse() throws SQLException {
        // Display list of courses the instructor is currently teaching
        if (!viewCourses()) {
            return;
        }
        // Prompt user to enter the course code and session
        String courseCode = Utils.getInput("Enter the course code");
        String session = Utils.getInput("Enter the session (YYYY-Semester)");
        // Check if the instructor is authorized to delist the course
        if (!dbInstructor.isCourseInstructor(courseCode, session, this.instructor_id)) {
            System.out.println("You are not authorized to delist this course");
            return;
        }
        // Attempt to delist the course from the course offerings table
        if (dbInstructor.delistCourse(courseCode, session, this.instructor_id)) {
            System.out.println("Course delisted successfully");
        } else {
            System.out.println("Course delisting failed");
        }
    }



}
