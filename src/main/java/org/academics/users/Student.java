package org.academics.users;


import org.academics.dal.JDBCPostgreSQLConnection;
import org.academics.dal.dbStudent;
import org.academics.utility.Utils;

import java.sql.*;


public class Student extends User {

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();
    String studentID;

    public Student(User user) {
        super(user.userRole, user.email_id);
        this.studentID = user.email_id.substring(0, user.email_id.indexOf("@")).toUpperCase();
    }

    public void registerCourse() {
        String session = Utils.getCurrentSession();
        try {
            ResultSet resultSet = dbStudent.fetchCoursesForRegistration(session);
            //Check here if the result se has anything else return
            String message = "The Following Courses are available for registration in " + session + " session";
            Utils.printTable(resultSet, new String[]{"Course Code", "Course Name", "Instructor"}, message);
            String course_code = Utils.getInput("\nEnter the course code of the course you want to register. Press 0 to exit");

            if (course_code.equals("0")) {
                return;
            }

            if (!dbStudent.checkEnrollmentAvailability(course_code, session)) {
                System.out.println("Course not available for registration. Please Choose the course code from the list");
                return;
            }

            if (dbStudent.computeGPA(this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase()) < dbStudent.fetchMinCGPA(course_code, session)) {
                System.out.println("You do not meet the minimum CGPA requirement for this course");
                return;
            }

            ResultSet preRequisites = dbStudent.getCoursePrerequisite(course_code, session);
            String[] prerequisites = extractPrerequisites(preRequisites);
            if (prerequisites != null) {
                if (!checkPrerequisites(prerequisites)) {
                    System.out.println("You do not meet the prerequisites for this course");
                    return;
                }
            }
            if (dbStudent.enrollCourse(this.studentID, course_code, session)) {
                System.out.println("Course Registered Successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts the prerequisites for a course from a ResultSet containing the prerequisite information.
     *
     * @param preRequisites The ResultSet containing the prerequisite information
     * @return An array of prerequisite course codes, or null if no prerequisites are listed for the course
     */
    private String[] extractPrerequisites(ResultSet preRequisites) {
        String[] prerequisites = null;
        try {
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
        } catch (SQLException e) {
            // Print the stack trace if there is an error while processing the prerequisites
            e.printStackTrace();
        }
        return prerequisites;
    }

    private boolean checkPrerequisites(String[] prerequisites) throws SQLException {
        for (String prerequisite : prerequisites) {
            if (prerequisite != null) {
                String[] prerequisiteOptions = prerequisite.split("\\|");
                boolean check = false;
                for (String prerequisiteOption : prerequisiteOptions) {
                    if (prerequisiteOption.equals("")) {
                        continue;
                    }
                    String prerequisiteCode = prerequisiteOption.substring(0, prerequisiteOption.indexOf("("));
                    String minGrade = prerequisiteOption.substring(prerequisiteOption.indexOf("(") + 1, prerequisiteOption.indexOf(")"));
                    System.out.println(prerequisiteCode + " " + minGrade);
                    PreparedStatement checkPrerequisites = conn.prepareStatement("SELECT grade FROM course_enrollments WHERE course_code =? AND student_id =? AND semester<?;");
                    checkPrerequisites.setString(1, prerequisiteCode);
                    checkPrerequisites.setString(2, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
                    checkPrerequisites.setString(3, Utils.getCurrentSession());
                    ResultSet checkPrerequisitesResult = checkPrerequisites.executeQuery();
                    if (!checkPrerequisitesResult.next()) {
                        continue;
                    }
                    if (checkPrerequisitesResult.getString("grade").equals("F")) {
                        continue;
                    }
                    if (minGrade.compareTo(checkPrerequisitesResult.getString("grade")) > 0) {
                        continue;
                    }
                    check = true;
                    break;
                }
                if (!check) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Allows a student to drop a course that they are currently enrolled in. If the drop period has ended,
     * the method will return without doing anything.
     *
     * @throws SQLException if there is an error executing the SQL query
     */
    public void dropCourse() throws SQLException {
        // Check if the current time falls within the course add/drop period
        if (!Utils.validateEventTime("course_add_drop", Utils.getCurrentSession())) {
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

}

