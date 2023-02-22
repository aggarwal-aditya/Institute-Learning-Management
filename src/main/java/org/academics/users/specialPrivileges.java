package org.academics.users;

import org.academics.dal.dbInstructor;
import org.academics.utility.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class contains methods that are only accessible to the admin and instructor
 */
public class specialPrivileges {

    /**
     * Displays menu to view grades of a course or a student and prints grades table
     *
     * @throws SQLException If there's an error in the SQL operation
     */
    public static void viewStudentGrades() throws SQLException {
        System.out.println("1. Select a course to view grades");
        System.out.println("2. Search a student to view grades");
        System.out.println("3. Go back to main menu");

        // Get user's choice
        int choice = Utils.getUserChoice(3);

        switch (choice) {
            case 1: {
                String course_code = Utils.getInput("Enter the course code");
                String session = Utils.getInput("Enter the session (YYYY-Semester)");

                // Get grades of the course
                ResultSet courseGrades = dbInstructor.fetchCourseGrades(course_code, session);

                String successMessage = "Please Find the Grades for the Course";
                String failureMessage = "No grades found for the course/ No Course found with the Course Code and Session";

                // Print grades table
                Utils.printTable(courseGrades, new String[]{"Course Code", "Course Name", "Semester", "Student ID", "Student Name", "Grade"}, successMessage, failureMessage);
                break;
            }
            case 2: {
                String studentID = Utils.getInput("Enter the student's Enrollment ID");

                // Get grades of the student
                ResultSet studentGrades = dbInstructor.fetchStudentGrades(studentID);

                String successMessage = "Please Find the Grades for the Student";
                String failureMessage = "No grades found for the student/ No Student found with the Enrollment ID";

                // Print grades table
                Utils.printTable(studentGrades, new String[]{"Course Code", "Course Name", "Semester", "Student ID", "Student Name", "Grade"}, successMessage, failureMessage);
                break;
            }
            case 3:
                break;
            default:
                System.out.println("Invalid choice. Redirecting to main menu");
                break;
        }
    }


}
