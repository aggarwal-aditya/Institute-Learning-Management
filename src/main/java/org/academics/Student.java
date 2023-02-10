package org.academics;


import dnl.utils.text.table.TextTable;
import org.academics.dao.JDBCPostgreSQLConnection;

import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Student extends User {

    Scanner scanner = new Scanner(System.in);
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Student(User user) {
        super(user.userRole, user.email_id);
    }

    public void registerCourse() {
        String session = Utils.getCurrentSession();
        if (session == null) {
            System.out.println("No courses available for registration");
            return;
        }
        ResultSet resultSet = null;
        try {
            PreparedStatement getAvailableCourses = conn.prepareStatement("SELECT course_offerings.course_code, course_catalog.course_name,instructors.name FROM course_offerings JOIN course_catalog ON course_offerings.course_code = course_catalog.course_code JOIN instructors ON course_offerings.instructor_id = instructors.instructor_id WHERE course_offerings.semester =?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getAvailableCourses.setString(1, session);
            resultSet = getAvailableCourses.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            assert resultSet != null;
            resultSet.last();
            if (resultSet.getRow() == 0) {
                System.out.println("No courses available for registration");
                return;
            }
            resultSet.beforeFirst();
            System.out.println("The Following Courses are available for registration in " + session + " session");
            String[] columnNames = {"S.No", "Course Code", "Course Name", "Instructor"};
            List<Object[]> data = new ArrayList<>();
            while (resultSet.next()) {
                Object[] course_detail = new Object[4];
                course_detail[0] = data.size() + 1;
                course_detail[1] = resultSet.getString("course_code");
                course_detail[2] = resultSet.getString("course_name");
                course_detail[3] = resultSet.getString("name");
                data.add(course_detail);
            }
            Object[][] courses = data.toArray(new Object[0][]);
            TextTable courseTable = new TextTable(columnNames, courses);
            courseTable.printTable();
            System.out.println("\nEnter the S.No of the course you want to register. Press 0 to exit");
            String course_number = scanner.nextLine();
            if (course_number.equals("0")) {
                return;
            }
            if (Integer.parseInt(course_number) > data.size()|| Integer.parseInt(course_number) < 0) {
                System.out.println("Invalid Course Number");
                return;
            }
            CallableStatement enroll_student = conn.prepareCall("{call enroll_student(?,?,?)}");
            enroll_student.setString(1, (String) courses[Integer.parseInt(course_number) - 1][1]);
            enroll_student.setString(2, session);
            enroll_student.setString(3, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            enroll_student.execute();
            System.out.println("You have successfully registered for " + courses[Integer.parseInt(course_number) - 1][2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewCourses() {
        ResultSet resultSet = null;
        try {
            PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code WHERE course_enrollments.student_id =? AND course_enrollments.semester=?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getRegisteredCourses.setString(1, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            getRegisteredCourses.setString(2, Utils.getCurrentSession());
            resultSet = getRegisteredCourses.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            assert resultSet != null;
            formatOutput(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewGrades() {
        ResultSet resultSet = null;
        try {
            PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code WHERE course_enrollments.student_id =? AND course_enrollments.semester<?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getRegisteredCourses.setString(1, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            getRegisteredCourses.setString(2, Utils.getCurrentSession());
            resultSet = getRegisteredCourses.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            assert resultSet != null;
            formatOutput(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void formatOutput(ResultSet resultSet) throws SQLException {
        resultSet.last();
        if (resultSet.getRow() == 0) {
            System.out.println("No courses to show");
            return;
        }
        resultSet.beforeFirst();
        String[] columnNames = {"Course Code", "Course Name", "Grade"};
        List<Object[]> data = new ArrayList<>();
        while (resultSet.next()) {
            Object[] course_detail = new Object[3];
            course_detail[0] = resultSet.getString("course_code");
            course_detail[1] = resultSet.getString("course_name");
            course_detail[2] = resultSet.getString("grade");
            data.add(course_detail);
        }
        Object[][] courses = data.toArray(new Object[0][]);
        TextTable courseTable = new TextTable(columnNames, courses);
        courseTable.printTable();
    }

    public void dropCourse() {
        viewCourses();
        System.out.println("Enter the course code of the course you want to drop. Press 0 to exit");
        String course_code = scanner.nextLine();
        if (course_code.equals("0")) {
            return;
        }

    }

    public void computeGPA() {
        try {
            CallableStatement calculateCGPA = conn.prepareCall("{? = call calculate_cgpa(?)}");
            calculateCGPA.registerOutParameter(1, Types.NUMERIC);
            calculateCGPA.setString(2, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            calculateCGPA.execute();
            System.out.println("Your CGPA is: " + calculateCGPA.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

}

