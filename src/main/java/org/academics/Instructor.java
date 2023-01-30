package org.academics;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Instructor {

    private final User user;

    private final int instructor_id;
    Scanner scanner = new Scanner(System.in);
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Instructor(User user) {
        this.user = user;
        this.instructor_id = getInstructorId();
    }

    public int getInstructorId() {
        try {
            PreparedStatement getInstructorId = conn.prepareStatement("SELECT instructor_id FROM instructors WHERE email_id = ?");
            getInstructorId.setString(1, user.email_id);
            ResultSet resultSet = getInstructorId.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void viewCourses() {
        try {
            CallableStatement getInstructorCourse = conn.prepareCall("{? = call get_instructor_courses(?)}");
            getInstructorCourse.registerOutParameter(1, Types.OTHER);
            getInstructorCourse.setInt(2, instructor_id);
            getInstructorCourse.execute();
            ResultSet getInstructorCourseObject = (ResultSet) getInstructorCourse.getObject(1);
            System.out.println("Course Code Course Name Session Minimum CGPA Required Enrollment Count");
            while (getInstructorCourseObject.next()) {
                String course_code = getInstructorCourseObject.getString(1);
                String course_name = getInstructorCourseObject.getString(2);
                String session = getInstructorCourseObject.getString(3);
                String min_cgpa = getInstructorCourseObject.getString(4);
                String enrollment_count = getInstructorCourseObject.getString(5);
                System.out.println(course_code + " " + course_name + " " + session + " " + min_cgpa + " " + enrollment_count);
            }
        } catch (SQLException e) {
            if (Objects.equals(e.getSQLState(), "02000")) {
                System.out.println("You have not floated any courses");
            } else {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }

    public void floatCourse() {
        try {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void uploadGrades() {

    }


    public void delistCourse() {
    }


    public void viewStudentGrades() {
    }
}
