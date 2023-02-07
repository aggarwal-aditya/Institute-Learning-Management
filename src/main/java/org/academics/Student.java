package org.academics;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Scanner;

public class Student {

    Scanner scanner = new Scanner(System.in);

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();
    private final User user;
    public Student(User user) {
        this.user = user;
    }

    public void viewCourses() {
        //TODO
    }

    public void viewGrades() {
        try {

            CallableStatement studentDetails = conn.prepareCall("{? = call student_history(?)}");
            studentDetails.registerOutParameter(1, Types.OTHER);
            studentDetails.setString(2, user.email_id   .substring(0,user.email_id.indexOf("@")).toUpperCase());
            studentDetails.execute();
            ResultSet resultSet= (ResultSet) studentDetails.getObject(1);
            System.out.println("Course Code Course Name Grade");
            while (resultSet.next()) {
                String course_code = resultSet.getString("course_code");
                String course_name = resultSet.getString("course_name");
                String grade = resultSet.getString("grade");
                System.out.println(course_code + " " + course_name + " " + grade);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
        computeGPA();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void registerCourse() {
        String session = Utils.getCurrentSession();
        System.out.println(session);
        System.out.println("The Following Courses are available for registration in " + session + " session");
        try {
            CallableStatement getAvailableCourses = conn.prepareCall("{? = call get_available_courses(?)}");
            if(session==null){
                System.out.println("No courses available for registration");
                return;
            }
            getAvailableCourses.registerOutParameter(1, Types.OTHER);
            getAvailableCourses.setString(2, session);
            getAvailableCourses.execute();
//            ResultSet resultSet = (ResultSet) getAvailableCourses.getObject(1);
//            System.out.println("Course Code Course Name Instructor");
//            while (resultSet.next()) {
//                String course_code = resultSet.getString("course_code");
//                String course_name = resultSet.getString("course_name");
//                String instructor = resultSet.getString("instructor");
//                System.out.println(course_code + " " + course_name + " " + instructor);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hi");
        }
//        System.out.println("Enter the course code of the course you want to register for:");
//        String course_code = scanner.next();
//        try {
//            CallableStatement registerCourse = conn.prepareCall("{call enroll_student(?, ?, ?)}");
//            registerCourse.setString(1, course_code);
//            registerCourse.setString(2, session);
//            registerCourse.setString(3, user.email_id   .substring(0,user.email_id.indexOf("@")).toUpperCase());
//            registerCourse.execute();
//            System.out.println("Course Registered Successfully");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

    }
    public void dropCourse() {
        //TODO
    }

    public void computeGPA() {
        try {

            CallableStatement calculateCGPA = conn.prepareCall("{? = call calculate_cgpa(?)}");
            calculateCGPA.registerOutParameter(1, Types.NUMERIC);
            calculateCGPA.setString(2, user.email_id.substring(0,user.email_id.indexOf("@")).toUpperCase());
            calculateCGPA.execute();
            System.out.println("Your CGPA is: " + calculateCGPA.getDouble(1));
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

}

