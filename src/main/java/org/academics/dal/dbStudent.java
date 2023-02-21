package org.academics.dal;

import java.math.RoundingMode;
import java.sql.*;

public class dbStudent {

    private static JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    private static Connection conn = jdbc.getConnection();

    /**
     * Computes the grade point average (GPA) for a given student using a stored procedure in the database.
     *
     * @param studentID the ID of the student whose GPA is to be computed
     * @return the computed GPA value, rounded to two decimal places
     * @throws SQLException if an error occurs while interacting with the database
     */
    public static double computeGPA(String studentID) throws SQLException {
        // Prepare a CallableStatement to call the stored procedure calculate_cgpa
        CallableStatement calculateCGPA = conn.prepareCall("{? = call calculate_cgpa(?)}");

        // Register an output parameter with index 1 of type NUMERIC to receive the computed GPA value
        calculateCGPA.registerOutParameter(1, Types.NUMERIC);

        // Set the studentID parameter as the second input parameter to the stored procedure
        calculateCGPA.setString(2, studentID);

        // Execute the stored procedure
        calculateCGPA.execute();

        // Get the computed GPA value from the output parameter with index 1,
        // round it to two decimal places using the HALF_UP rounding mode,
        // and convert it to a double value
        return calculateCGPA.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Fetches the grades of a given student for all courses taken until a given semester, using a prepared statement.
     *
     * @param studentID the ID of the student whose grades are to be fetched
     * @param tillSem   the semester until which the grades are to be fetched
     * @return a ResultSet object containing the fetched grades
     * @throws SQLException if an error occurs while interacting with the database
     */
    public static ResultSet fetchGrades(String studentID, String tillSem) throws SQLException {
        // Prepare a PreparedStatement to fetch the grades of a student until a given semester
        PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade " + "FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code " + "WHERE course_enrollments.student_id =? AND course_enrollments.semester<=?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // Set the studentID parameter as the first input parameter to the prepared statement
        getRegisteredCourses.setString(1, studentID);

        // Set the tillSem parameter as the second input parameter to the prepared statement
        getRegisteredCourses.setString(2, tillSem);

        // Execute the prepared statement and return the ResultSet object containing the fetched grades
        return getRegisteredCourses.executeQuery();
    }

    /**
     * Returns a ResultSet object that contains the course codes, course names, and grades for the courses
     * that a particular student has enrolled in up to the current semester.
     *
     * @param studentID the ID of the student whose enrolled courses are to be fetched
     * @param semester  the current semester
     * @return a ResultSet object that contains the course codes, course names, and grades for the courses
     * that the student has enrolled in
     * @throws SQLException if there is an error executing the SQL query
     */
    public static ResultSet fetchCourses(String studentID, String semester) throws SQLException {
        // Prepare a PreparedStatement to fetch the grades of a student until a given semester
        PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade " + "FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code " + "WHERE course_enrollments.student_id =? AND course_enrollments.semester=?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // Set the studentID parameter as the first input parameter to the prepared statement
        getRegisteredCourses.setString(1, studentID);

        // Set the current semester as the second input parameter to the prepared statement
        getRegisteredCourses.setString(2, semester);

        // Execute the prepared statement and return the ResultSet object containing the fetched grades
        return getRegisteredCourses.executeQuery();
    }

    /**
     * Drops a course from a student's schedule for the current semester.
     *
     * @param studentID  the ID of the student whose schedule should be updated
     * @param courseCode the code of the course to be dropped
     * @param semester   the semester in which the course is being dropped
     * @return the number of rows affected by the update (should be 1 if the drop was successful)
     * @throws SQLException if an error occurs while interacting with the database
     */
    public static int dropCourse(String studentID, String courseCode, String semester) throws SQLException {
        // Prepare a PreparedStatement to delete the enrollment record for the given student and course in the current semester
        PreparedStatement dropCourse = conn.prepareStatement("DELETE FROM course_enrollments WHERE student_id=? AND course_code=? AND semester=?;");

        // Set the studentID parameter as the first input parameter to the prepared statement
        dropCourse.setString(1, studentID);

        // Set the courseCode parameter as the second input parameter to the prepared statement
        dropCourse.setString(2, courseCode);

        // Set the semester as the third input parameter to the prepared statement
        dropCourse.setString(3, semester);

        // Execute the prepared statement and return the number of rows affected by the delete operation
        return dropCourse.executeUpdate();
    }

    /**
     * This method retrieves the list of available courses for a given semester
     *
     * @param semester The semester for which the available courses are to be fetched
     * @return A ResultSet object containing the available courses for the given semester
     * @throws SQLException if there is an error executing the SQL statement
     */
    public static ResultSet fetchCoursesForRegistration(String semester) throws SQLException {
        // Prepare a PreparedStatement to fetch the available courses for a given semester
        PreparedStatement getAvailableCourses = conn.prepareStatement("SELECT course_offerings.course_code, course_catalog.course_name,instructors.name FROM course_offerings JOIN course_catalog ON course_offerings.course_code = course_catalog.course_code JOIN instructors ON course_offerings.instructor_id = instructors.instructor_id WHERE course_offerings.semester =?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // Set the semester parameter as the first input parameter to the prepared statement
        getAvailableCourses.setString(1, semester);

        // Execute the prepared statement and return the ResultSet object containing the available courses
        return getAvailableCourses.executeQuery();
    }

    /**
     * This method fetches the minimum CGPA required to qualify for a particular course in a given semester
     *
     * @param course_code The course code of the course
     * @param semester    The semester in which the course is being offered
     * @return The minimum CGPA required to qualify for the course
     * @throws SQLException If there is an error while executing the SQL query
     */
    public static double fetchMinCGPA(String course_code, String semester) throws SQLException {
        // Prepare a PreparedStatement to fetch the minimum CGPA required to qualify for the course
        PreparedStatement minCGPA = conn.prepareStatement("SELECT qualify from course_offerings WHERE course_code =? AND semester =?;");

        // Set the course_code parameter as the first input parameter to the prepared statement
        minCGPA.setString(1, course_code);

        // Set the semester parameter as the second input parameter to the prepared statement
        minCGPA.setString(2, semester);

        // Execute the prepared statement and return the minimum CGPA as a double
        ResultSet minCGPAResult = minCGPA.executeQuery();
        minCGPAResult.next();
        return minCGPAResult.getDouble(1);
    }

    /**
     * Returns the prerequisites for a given course in a given semester.
     *
     * @param course_code the code of the course for which prerequisites are to be retrieved
     * @param semester    the semester for which the prerequisites are to be retrieved
     * @return a ResultSet object containing the prerequisites of the given course in the given semester
     * @throws SQLException if there is an error while accessing the database
     */
    public static ResultSet getCoursePrerequisite(String course_code, String semester) throws SQLException {
        // Prepare a PreparedStatement to retrieve the prerequisites of a given course for a given semester
        PreparedStatement getPrerequisite = conn.prepareStatement("SELECT prerequisite from course_offerings WHERE course_code =? AND semester=? UNION SELECT prerequisite from course_catalog WHERE course_code =?;");

        // Set the parameters of the PreparedStatement
        getPrerequisite.setString(1, course_code);
        getPrerequisite.setString(2, semester);
        getPrerequisite.setString(3, course_code);

        // Execute the PreparedStatement and return the ResultSet object containing the prerequisites
        return getPrerequisite.executeQuery();
    }

    /**
     * Enrolls a student in a course for a given semester after performing credit limit and already enrolled checks.
     *
     * @param studentID   the ID of the student to enroll
     * @param course_code the code of the course to enroll in
     * @param semester    the semester to enroll in
     * @return true if the student was enrolled successfully, false otherwise
     */
    public static boolean enrollCourse(String studentID, String course_code, String semester) {
        try {
            PreparedStatement enrolCourse = conn.prepareStatement("INSERT INTO course_enrollments (course_code, semester, student_id) VALUES(?,?,?);");
            enrolCourse.setString(1, course_code);
            enrolCourse.setString(2, semester);
            enrolCourse.setString(3, studentID);
            enrolCourse.execute();
            System.out.println("You have successfully registered for " + course_code);
            return true;
        } catch (SQLException ex) {
            // Loop through the possible SQL exceptions and print the corresponding error message
            for (Throwable e : ex) {
                if (e.getMessage().contains("The student is already enrolled in the course")) {
                    System.out.println("You have already registered for this course");
                    return false;
                } else if (e.getMessage().contains("The student has already completed the course earlier")) {
                    System.out.println("You have already completed this course");
                    return false;
                } else if (e.getMessage().contains("The student will exceed the credit limit for this semester")) {
                    System.out.println("You will exceed the credit limit for this semester");
                    return false;
                } else {
                    System.err.println("Message: " + e.getMessage());
                    System.out.println("An error occurred while registering for the course");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a given course is being offered in the given semester.
     *
     * @param course_code the code for the course being checked
     * @param semester    the semester in which the course is being checked for availability
     * @return a boolean indicating whether the course is being offered in the given semester
     * @throws SQLException if there is an error executing the SQL query
     */
    public static boolean checkEnrollmentAvailability(String course_code, String semester) throws SQLException {
        // Prepare a PreparedStatement to check if a given course is being offered in the given semester
        PreparedStatement checkCourse = conn.prepareStatement("SELECT course_code FROM course_offerings WHERE course_code =? AND semester =?;");

        // Set the course_code parameter as the first input parameter to the prepared statement
        checkCourse.setString(1, course_code);

        // Set the semester parameter as the second input parameter to the prepared statement
        checkCourse.setString(2, semester);

        // Execute the prepared statement and return a boolean indicating whether the course is being offered in the given semester
        ResultSet checkCourseResult = checkCourse.executeQuery();
        return checkCourseResult.next();
    }


}
