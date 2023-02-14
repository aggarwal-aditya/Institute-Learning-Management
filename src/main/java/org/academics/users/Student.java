package org.academics.users;


import dnl.utils.text.table.TextTable;
import org.academics.dao.JDBCPostgreSQLConnection;
import org.academics.utility.Utils;

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
            if (Integer.parseInt(course_number) > data.size() || Integer.parseInt(course_number) < 0) {
                System.out.println("Invalid Course Number");
                return;
            }

            //Check Pre-requisites in course_catalog
            PreparedStatement Prerequisites = conn.prepareStatement("SELECT prerequisite from course_catalog WHERE course_code =?;");
            Prerequisites.setString(1, courses[Integer.parseInt(course_number) - 1][1].toString());
            ResultSet PrerequisitesResult = Prerequisites.executeQuery();
            while (PrerequisitesResult.next()) {
                Array prerequisitesResultArray = PrerequisitesResult.getArray(1);
                if (prerequisitesResultArray == null) {
                    continue;
                }
                String[] prerequisites = (String[]) prerequisitesResultArray.getArray();
                for (String prerequisite : prerequisites) {
                    if (prerequisite != null) {
                        String[] prerequisiteOptions = prerequisite.split("\\|");
                        boolean check = false;
                        for (String prerequisiteOption : prerequisiteOptions) {
                            if(prerequisiteOption.equals("")){
                                continue;
                            }
                            String prerequisiteCode = prerequisiteOption.substring(0, prerequisiteOption.indexOf("("));
                            String minGrade = prerequisiteOption.substring(prerequisiteOption.indexOf("(") + 1, prerequisiteOption.indexOf(")"));
                            System.out.println(prerequisiteCode+" "+minGrade);
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
                            if(minGrade.compareTo(checkPrerequisitesResult.getString("grade"))>0){
                                continue;
                            }
                            check = true;
                            break;
                        }
                        if (!check) {
                            System.out.println("You have not completed the prerequisites for this course");
                            return;
                        }
                    }
                }
            }

            PreparedStatement PrerequisitesOfferings = conn.prepareStatement("SELECT prerequisite from course_offerings WHERE course_code =? AND semester=?;");
            PrerequisitesOfferings.setString(1, courses[Integer.parseInt(course_number) - 1][1].toString());
            PrerequisitesOfferings.setString(2, Utils.getCurrentSession());
            ResultSet PrerequisitesOfferingsResult = PrerequisitesOfferings.executeQuery();
            while (PrerequisitesOfferingsResult.next()) {
                Array prerequisitesOfferingsResultArray = PrerequisitesOfferingsResult.getArray(1);
                if (prerequisitesOfferingsResultArray == null) {
                    continue;
                }
                String[] prerequisites = (String[]) prerequisitesOfferingsResultArray.getArray();
                for (String prerequisite : prerequisites) {
                    if (prerequisite != null) {
                        String[] prerequisiteOptions = prerequisite.split("\\|");
                        boolean check = false;
                        for (String prerequisiteOption : prerequisiteOptions) {
                            if(prerequisiteOption.equals("")){
                                continue;
                            }
                            String prerequisiteCode = prerequisiteOption.substring(0, prerequisiteOption.indexOf("("));
                            String minGrade = prerequisiteOption.substring(prerequisiteOption.indexOf("(") + 1, prerequisiteOption.indexOf(")"));
                            System.out.println(prerequisiteCode+" "+minGrade);
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
                            if(minGrade.compareTo(checkPrerequisitesResult.getString("grade"))>0){
                                continue;
                            }
                            check = true;
                            break;
                        }
                        if (!check) {
                            System.out.println("You have not completed the prerequisites for this course");
                            return;
                        }
                    }
                }
            }
            System.out.println("You have successfully registered for " + courses[Integer.parseInt(course_number) - 1][2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void dropCourse() {
        viewCourses();
        System.out.println("Enter the course code of the course you want to drop. Press 0 to exit");
        String course_code = scanner.nextLine();
        if (course_code.equals("0")) {
            return;
        }
        try {
            PreparedStatement dropCourse = conn.prepareStatement("DELETE FROM course_enrollments WHERE course_code =? AND student_id =? AND semester=?;");
            dropCourse.setString(1, course_code);
            dropCourse.setString(2, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            dropCourse.setString(3, Utils.getCurrentSession());
            dropCourse.executeUpdate();
            if(dropCourse.getUpdateCount()==0){
                System.out.println("You are not allowed to drop this course now or you are registered for this course");
                return;
            }
            System.out.println("You have successfully dropped the course");
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
        try {
            assert resultSet != null;
            formatOutput(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewGrades() {
        ResultSet resultSet = null;
        try {
//            PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code WHERE course_enrollments.student_id =? AND course_enrollments.semester<?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code WHERE course_enrollments.student_id =?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getRegisteredCourses.setString(1, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
//            getRegisteredCourses.setString(2, Utils.getCurrentSession());
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

    private boolean checkPrerequisite(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Array prerequisitesOfferingsResultArray = resultSet.getArray(1);
            if (prerequisitesOfferingsResultArray == null) {
                continue;
            }
            String[] prerequisites = (String[]) prerequisitesOfferingsResultArray.getArray();
            for (String prerequisite : prerequisites) {
                if (prerequisite != null) {
                    String[] prerequisiteOptions = prerequisite.split("\\|");
                    boolean check = false;
                    for (String prerequisiteOption : prerequisiteOptions) {
                        if(prerequisiteOption.equals("")){
                            continue;
                        }
                        String prerequisiteCode = prerequisiteOption.substring(0, prerequisiteOption.indexOf("("));
                        String minGrade = prerequisiteOption.substring(prerequisiteOption.indexOf("(") + 1, prerequisiteOption.indexOf(")"));
                        System.out.println(prerequisiteCode+" "+minGrade);
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
                        if(minGrade.compareTo(checkPrerequisitesResult.getString("grade"))>0){
                            continue;
                        }
                        check = true;
                        break;
                    }
                    if (!check) {
                        System.out.println("You have not completed the prerequisites for this course");
                        return false;
                    }
                }
            }
        }
        return true;
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

}

