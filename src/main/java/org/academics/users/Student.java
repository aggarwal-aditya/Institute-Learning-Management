package org.academics.users;



import org.academics.dal.dbStudent;
import org.academics.utility.Utils;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * This class contains methods that are only accessible to the student
 */
public class Student extends User {

    String studentID;

    /**
     * Constructor for Student class
     *
     * @param user User object
     */
    public Student(User user) {
        super(user.userRole, user.email_id);
        this.studentID = user.email_id.substring(0, user.email_id.indexOf("@")).toUpperCase();
    }

    /**
     * Fetches the available courses for registration and checks for eligibility requirements before enrolling student into the course
     */
    public void registerCourse() {

        try {
            // Get the current session
            String session = Utils.getCurrentSession();

            // Get the available courses for registration in the current session
            ResultSet availableCourses = dbStudent.fetchCoursesForRegistration(session);
            // Set the success and failure messages for printing available courses
            String successMessage = "The Following Courses are available for registration in " + session + " session";
            String failureMessage = "No Courses are available for registration in " + session + " session";
            // Print the available courses along with the success/failure message
            Utils.printTable(availableCourses, new String[]{"Course Code", "Course Name", "Instructor"}, successMessage, failureMessage);
            // Return if no courses are available for registration
            if (!availableCourses.next()) {
                return;
            }
            // Get the course code from user
            String course_code = Utils.getInput("\nEnter the course code of the course you want to register. Press -1 to go back");
            // Return if the user presses -1
            if (course_code.equals("-1")) {
                return;
            }
            // Check if the selected course is available for registration
            if (!dbStudent.checkEnrollmentAvailability(course_code, session)) {
                System.out.println("Course not available for registration. Please Choose the course code from the list");
                return;
            }
            // Check if the student meets the minimum CGPA requirement for the selected course
            if (dbStudent.computeGPA(this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase()) < dbStudent.fetchMinCGPA(course_code, session)) {
                System.out.println("You do not meet the minimum CGPA requirement for this course");
                return;
            }
            // Get the prerequisites for the selected course
            ResultSet preRequisites = dbStudent.getCoursePrerequisite(course_code, session);
            String[] prerequisites = extractPrerequisites(preRequisites);
            // Check if the student meets the prerequisites for the selected course
            if (!dbStudent.checkPreRequisitesEligibility(prerequisites, this.studentID)) {
                System.out.println("You do not meet the prerequisites for this course");
                return;
            }
            // Enroll the student into the selected course if all eligibility criteria are met
            if (dbStudent.enrollCourse(this.studentID, course_code, session)) {
                System.out.println("Course Registered Successfully");
            }
        } catch (Exception e) {
            System.err.println("Message: " + e.getMessage());
        }
    }

    /**
     * Allows a student to drop a course that they are currently enrolled in. If the drop period has ended,
     * the method will return without doing anything.
     *
     * @throws SQLException if there is an error executing the SQL query
     */
    public void dropCourse() throws SQLException {
        // Check if the current time falls within the course add/drop period
        String session = Utils.getCurrentSession();
        if(session==null){
            System.out.println("No session is currently active");
            return;
        }
        if (!Utils.validateEventTime("course_add_drop", session)) {
            System.out.println("You are not allowed to drop courses now");
            return;
        }

        // Prompt the user to enter the course code of the course they want to drop
        String course_code = Utils.getInput("Enter the course code of the course you want to drop. Press -1 to go back");

        // If the user enters -1, return without doing anything
        if (course_code.equals("-1")) {
            return;
        }

        // Attempt to drop the course for the current student in the database
        int countDropped = dbStudent.dropCourse(this.studentID, course_code, Utils.getCurrentSession());

        // If no rows were affected, the student is not registered for the course
        if (countDropped == 0) {
            System.out.println("You are not registered for this course");
        }
        // Otherwise, the course was successfully dropped
        else {
            System.out.println("You have successfully dropped the course");
        }
    }

    /**
     * Displays the courses that the current student is registered for in the current semester, if any.
     *
     * @throws SQLException if an error occurs while interacting with the database
     */
    public void viewCourses() throws SQLException {
        // Fetch the courses that the current student is registered for in the current semester
        ResultSet fetchCourses = dbStudent.fetchCourses(this.studentID, Utils.getCurrentSession());

        // Define success and failure messages to be displayed after the table of courses is printed
        String successMessage = "Please find the list of courses you are registered for in the current semester";
        String failureMessage = "You are not registered for any courses in the current semester";

        // Print a table of the fetched courses, with headers "Course Code" and "Course Name",
        // and either the success message or the failure message, depending on whether any courses were fetched
        Utils.printTable(fetchCourses, new String[]{"Course Code", "Course Name"}, successMessage, failureMessage);
    }


    /**
     * Displays the grades of the current student for all courses taken until the current semester, if any.
     *
     * @throws SQLException if an error occurs while interacting with the database
     */
    public void viewGrades() throws SQLException {
        // Fetch the grades of the current student for all courses taken until the current semester
        ResultSet fetchGrades = dbStudent.fetchGrades(this.studentID, Utils.getCurrentSession());

        // Define success and failure messages to be displayed after the table of grades is printed
        String successMessage = "Please find your grades for the courses you have taken so far";
        String failureMessage = "You have not completed/registered any courses";

        // Print a table of the fetched grades, with headers "Course Code", "Course Name", and "Grade",
        // and either the success message or the failure message, depending on whether any grades were fetched
        Utils.printTable(fetchGrades, new String[]{"Course Code", "Course Name", "Grade"}, successMessage, failureMessage);
    }


    /**
     * Prints the current student's CGPA to the console.
     *
     * @throws SQLException if there is an error in the database query.
     */
    public void printGPA() throws SQLException {
        System.out.println("Your CGPA is: " + dbStudent.computeGPA(this.studentID));
    }

    /**
     * This method extracts the prerequisites from the result set returned by a SQL query. It returns a string
     * array containing the prerequisites. If there are no prerequisites, it returns null.
     *
     * @param preRequisites The ResultSet containing the prerequisites
     * @return A string array containing the prerequisites, or null if there are no prerequisites
     * @throws SQLException If there is an error while processing the ResultSet
     */
    private String[] extractPrerequisites(ResultSet preRequisites) throws SQLException {
        String[] prerequisites = null;
        // Iterate through each row in the ResultSet
        while (preRequisites.next()) {
            // Get the prerequisites array from the first column of the row
            Array prerequisitesResultArray = preRequisites.getArray(1);

            // If the prerequisites array is null, skip to the next row
            if (prerequisitesResultArray == null) {
                continue;
            }

            // Convert the array to a string array
            prerequisites = (String[]) prerequisitesResultArray.getArray();

            // Stop processing rows after the first non-null array is found
            break;
        }
        // Return the prerequisites array
        return prerequisites;
    }


}

