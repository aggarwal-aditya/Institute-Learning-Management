package org.academics.dal;

import com.opencsv.CSVReader;
import org.academics.users.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to fetch data from the database pertaining to instructor.
 */
public class dbInstructor {
    private static final JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    private static final Connection conn = jdbc.getConnection();


    /**
     * This method fetches the instructor ID of the instructor with the given email ID.
     * @param user the user whose instructor ID is to be fetched
     * @return the instructor ID of the instructor with the given email ID
     * @throws SQLException if there is an error with database access
     */
    public static int fetchInstructorID(User user) throws SQLException {
        PreparedStatement getInstructorId = conn.prepareStatement("SELECT instructor_id FROM instructors WHERE email_id = ?");
        getInstructorId.setString(1, user.email_id);
        ResultSet resultSet = getInstructorId.executeQuery();
        resultSet.next();
        return resultSet.getInt("instructor_id");


    }


    /**
     * Fetches all the courses taught by the instructor with the given ID from the database.
     *
     * @param instructorID The ID of the instructor whose courses are to be fetched.
     * @return The ResultSet object containing the courses taught by the instructor.
     * @throws SQLException If an error occurs while executing the SQL query.
     */
    public static ResultSet fetchCourses(int instructorID) throws SQLException {
        // Creating a prepared statement to fetch courses taught by the instructor with the given ID
        PreparedStatement fetchCourses = conn.prepareStatement("SELECT course_offerings.course_code,course_offerings.semester,course_offerings.qualify,course_offerings.enrollment_count FROM course_offerings WHERE instructor_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        // Setting the instructor_id parameter in the PreparedStatement
        fetchCourses.setInt(1, instructorID);
        // Executing the query and returning the ResultSet
        return fetchCourses.executeQuery();
    }

    /**
     * This method fetches all the approved courses from the course catalog.
     * @return a ResultSet containing the approved courses
     * @throws SQLException if there is an error with the SQL query
     */
    public static ResultSet fetchApprovedCourses() throws SQLException {
        // Prepare the SQL statement to fetch the approved courses
        PreparedStatement approvedCourses = conn.prepareStatement("SELECT course_catalog.course_code,course_catalog.course_name, prerequisite FROM course_catalog", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        // Execute the SQL query and return the result set
        return approvedCourses.executeQuery();
    }


    /**
     * This method checks if a course with the given course code is approved by the Senate.
     * @param course_code the code of the course to be checked
     * @return true if the course is approved, false otherwise
     * @throws SQLException if there is an error with the SQL query
     */
    public static boolean checkCourseApproval(String course_code) throws SQLException {
        // Prepare the SQL statement to validate the course
        PreparedStatement validateCourse = conn.prepareStatement("SELECT course_code FROM course_catalog WHERE course_code = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        // Set the course code parameter in the prepared statement
        validateCourse.setString(1, course_code);
        // Execute the SQL query and return true if the result set is not empty
        return validateCourse.executeQuery().next();
    }

    /**
     * Fetches the grades of students in a particular course and session.
     *
     * @param course_code the course code of the course to fetch grades for
     * @param session     the session to fetch grades for
     * @return a ResultSet containing the course code, course name, semester, student ID, name, and grade for all students in the course and session
     * @throws SQLException if an error occurs while executing the SQL query
     */
    public static ResultSet fetchCourseGrades(String course_code, String session) throws SQLException {
        PreparedStatement getGrades = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.semester, course_enrollments.student_id, students.name, course_enrollments.grade FROM course_enrollments JOIN course_catalog ON course_enrollments.course_code=course_catalog.course_code JOIN students ON course_enrollments.student_id = students.student_id WHERE course_enrollments.course_code = ? AND course_enrollments.semester = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getGrades.setString(1, course_code);
        getGrades.setString(2, session);
        return getGrades.executeQuery();
    }

    /**
     * Returns a ResultSet of grades for a given student.
     *
     * @param studentID ID of the student whose grades are to be fetched
     * @return ResultSet of grades for the student
     * @throws SQLException if there is an error in executing the SQL query
     */
    public static ResultSet fetchStudentGrades(String studentID) throws SQLException {
        PreparedStatement getGrades = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.semester, course_enrollments.student_id, students.name, course_enrollments.grade FROM course_enrollments JOIN course_catalog ON course_enrollments.course_code=course_catalog.course_code JOIN students ON course_enrollments.student_id = students.student_id WHERE course_enrollments.student_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getGrades.setString(1, studentID);
        // Executing the query and returning the ResultSet
        return getGrades.executeQuery();
    }

    /**
     * Checks whether a course with the given course code, semester, belongs to a given instructor ID
     *
     * @param course_code  the course code to check
     * @param semester     the semester to check
     * @param instructorID the instructor ID to check
     * @return true if the course exists in the course_offerings table, false otherwise
     * @throws SQLException if an error occurs while executing the SQL query
     */
    public static boolean isCourseInstructor(String course_code, String semester, int instructorID) throws SQLException {
        PreparedStatement validateCourse = conn.prepareStatement("SELECT * FROM course_offerings WHERE course_code = ? AND semester = ? AND instructor_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        validateCourse.setString(1, course_code);
        validateCourse.setString(2, semester);
        validateCourse.setInt(3, instructorID);
        return validateCourse.executeQuery().next();
    }

    /**
     * Checks if the course has already been floated in the given semester
     * @param course_code The code of the course to check
     * @param semester The semester to check
     * @return A string message indicating if the course is already floated or null if it is not floated yet
     * @throws SQLException If there is an error executing the SQL query
     */
    public static String isCourseFloated(String course_code, String semester) throws SQLException {
        PreparedStatement isCourseFloated = conn.prepareStatement("SELECT instructors.name FROM course_offerings JOIN instructors  on course_offerings.instructor_id = instructors.instructor_id WHERE course_offerings.course_code = ? AND course_offerings.semester = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet;
        isCourseFloated.setString(1, course_code);
        isCourseFloated.setString(2, semester);
        resultSet = isCourseFloated.executeQuery();
        if (resultSet.next()) {
            return "The course is already floated by Dr." + resultSet.getString(1);
        }
        return null;
    }


    /**
     * Delete a course from the list of courses offered by an instructor
     *
     * @param course_code  course code of the course to be deleted
     * @param semester     semester in which the course is offered
     * @param instructorID ID of the instructor
     * @return true if the course is deleted, false otherwise
     * @throws SQLException if there's an error executing the SQL statement
     */
    public static boolean delistCourse(String course_code, String semester, int instructorID) throws SQLException {
        PreparedStatement delistCourse = conn.prepareStatement("DELETE FROM course_offerings WHERE course_code = ? AND semester = ? AND instructor_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        delistCourse.setString(1, course_code);
        delistCourse.setString(2, semester);
        delistCourse.setInt(3, instructorID);
        delistCourse.executeUpdate();
        return (delistCourse.getUpdateCount() > 0);
    }

    /**
     * Uploads grades for students in a course and semester from a CSV file.
     *
     * @param filePath    the path to the CSV file
     * @param course_code the course code for which to upload the grades
     * @param semester    the semester for which to upload the grades
     * @throws SQLException if a database access error occurs
     * @throws IOException  if an I/O error occurs while reading the CSV file
     */
    public static void uploadGrades(String filePath, String course_code, String semester) throws SQLException, IOException {

        // Prepare an SQL statement to update course enrollments with grades
        PreparedStatement uploadGrades = conn.prepareStatement("UPDATE course_enrollments SET grade = ? WHERE enrollment_id = ? AND course_code = ? AND semester = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        CSVReader reader = null;
        // Read the CSV file using a CSVReader
        try {reader = new CSVReader(new FileReader(filePath));
        }catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }
        String[] line;

        // Loop through each row in the CSV file
        reader.readNext(); // Skip the first line
        while ((line = reader.readNext()) != null) {

            // Set the parameters for the SQL statement
            uploadGrades.setString(1, line[3]); // set the grade
            uploadGrades.setInt(2, Integer.parseInt((line[0]))); // set the student_id
            uploadGrades.setString(3, course_code); // set the course_code
            uploadGrades.setString(4, semester); // set the semester


            // Execute the SQL statement to update the course enrollment with the grade
            uploadGrades.executeUpdate();

            // Check if any rows were updated by the SQL statement
            if (uploadGrades.getUpdateCount() == 0) {
                System.out.printf("Error uploading grades for enrollment ID %s (StudentID %s) (No Course Enrollment Record Found)\n" ,line[0], line[1]);
            }
        }
    }

    /**
     * Fetches a result set of all students enrolled in a given course and semester.
     *
     * @param course_code the course code to fetch students for
     * @param semester the semester to fetch students for
     * @return a {@link java.sql.ResultSet} containing the enrollment ID, student ID, and name of each enrolled student
     * @throws java.sql.SQLException if a database error occurs
     */
    public static ResultSet fetchStudentsList(String course_code, String semester) throws SQLException {
        PreparedStatement getEnrolledStudents = conn.prepareStatement("SELECT course_enrollments.enrollment_id, course_enrollments.student_id, students.name FROM course_enrollments JOIN students on course_enrollments.student_id = students.student_id WHERE course_code = ? AND semester = ? ORDER BY course_enrollments.student_id ASC");
        getEnrolledStudents.setString(1, course_code);
        getEnrolledStudents.setString(2, semester);
        return getEnrolledStudents.executeQuery();
    }


    /**
     * Adds a new course offering in the database.
     *
     * @param course_code the code of the course to float
     * @param semester the semester of the course offering
     * @param instructorID the ID of the instructor for the course
     * @param enrollmentCount the enrollment count for the course offering
     * @param qualify the qualification score required for the course offering
     * @param preRequisites the prerequisites for the course offering
     * @return true if the course offering was successfully floated, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean floatCourse(String course_code, String semester, int instructorID, int enrollmentCount, double qualify, ArrayList<String> preRequisites) throws SQLException {
        PreparedStatement floatCourse = conn.prepareStatement("INSERT INTO course_offerings (course_code, semester, instructor_id, enrollment_count, qualify, prerequisite) VALUES (?, ?, ?, ?, ?, ?)");
        floatCourse.setString(1, course_code);
        floatCourse.setString(2, semester);
        floatCourse.setInt(3, instructorID);
        floatCourse.setInt(4, enrollmentCount);
        floatCourse.setDouble(5, qualify);
        floatCourse.setArray(6, conn.createArrayOf("text", preRequisites.toArray()));
        floatCourse.executeUpdate();
        return (floatCourse.getUpdateCount() > 0);
    }


    /**
     * Updates course mappings in the database for a given course code, semester, and other parameters.
     *
     * @param course_code   The code of the course being mapped.
     * @param semester      The semester for which the course is being mapped.
     * @param departmentIds A list of department IDs to map the course to.
     * @param batches       A list of batches to map the course to.
     * @param courseTypes   A list of course types to map the course to.
     * @throws SQLException If an error occurs while executing the SQL statement.
     */
    public static void updateCourseMapping(String course_code, String semester, List<Integer> departmentIds, List<Integer> batches, List<String> courseTypes) throws SQLException {
        PreparedStatement updateCourseMapping = conn.prepareStatement("INSERT INTO course_mappings (course_code, semester, department_id, batch, course_type) VALUES (?, ?, ?, ?,?)");
        for (int i = 0; i < departmentIds.size(); i++) {
            updateCourseMapping.setString(1, course_code);
            updateCourseMapping.setString(2, semester);
            updateCourseMapping.setInt(3, departmentIds.get(i));
            updateCourseMapping.setInt(4, batches.get(i));
            updateCourseMapping.setString(5, courseTypes.get(i));
            updateCourseMapping.executeUpdate();
        }
    }




}