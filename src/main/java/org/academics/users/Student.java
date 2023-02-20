package org.academics.users;


import org.academics.dao.JDBCPostgreSQLConnection;
import org.academics.utility.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;


public class Student extends User {

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Student(User user) {
        super(user.userRole, user.email_id);
    }

    public void registerCourse() {
        String session = Utils.getCurrentSession();
        try {
            ResultSet resultSet = getAvailableCourses(session);
            String message = "The Following Courses are available for registration in " + session + " session";
            Utils.printTable(resultSet, new String[]{"Course Code", "Course Name", "Instructor"}, message);
            String course_code = Utils.getInput("\nEnter the course code of the course you want to register. Press 0 to exit");

            if (course_code.equals("0")) {
                return;
            }

            if(!validateCourse(course_code,session)){
                System.out.println("Course not available for registration. Please Choose the course code from the list");
                return;
            }

            if (computeGPA().doubleValue() < getMinCGPA(course_code,session)) {
                System.out.println("You do not meet the minimum CGPA requirement for this course");
                return;
            }

            ResultSet preRequisites = getCoursePrerequisite(course_code, session);
            String[] prerequisites = massageResultSet(preRequisites);
            if (prerequisites != null) {
                if (!checkPrerequisites(prerequisites)) {
                    System.out.println("You do not meet the prerequisites for this course");
                    return;
                }
            }
            if(updateCourseEnroll(course_code, session)){
                System.out.println("Course Registered Successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResultSet getCoursePrerequisite(String course_code, String Semester) {
        ResultSet preRequisites;
        try {
            PreparedStatement getPrerequisite = conn.prepareStatement("SELECT prerequisite from course_offerings WHERE course_code =? AND semester=? UNION SELECT prerequisite from course_catalog WHERE course_code =?;");
            getPrerequisite.setString(1, course_code);
            getPrerequisite.setString(2, Semester);
            getPrerequisite.setString(3, course_code);
            preRequisites = getPrerequisite.executeQuery();
            return preRequisites;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] massageResultSet(ResultSet preRequisites) {
        String[] prerequisites = null;
        try {
            while (preRequisites.next()) {
                Array prerequisitesResultArray = preRequisites.getArray(1);
                if (prerequisitesResultArray == null) {
                    continue;
                }
                prerequisites = (String[]) prerequisitesResultArray.getArray();
            }
        } catch (Exception e) {
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

    private boolean validateCourse(String course_code,String semester) throws SQLException {
        PreparedStatement checkCourse = conn.prepareStatement("SELECT course_code FROM course_offerings WHERE course_code =? AND semester =?;");
        checkCourse.setString(1, course_code);
        checkCourse.setString(2, semester);
        ResultSet checkCourseResult = checkCourse.executeQuery();
        return checkCourseResult.next();
    }

    private double getMinCGPA(String course_code,String semester) throws SQLException{
        PreparedStatement minCGPA = conn.prepareStatement("SELECT qualify from course_offerings WHERE course_code =? AND semester =?;");
        minCGPA.setString(1, course_code);
        minCGPA.setString(2, semester);
        ResultSet minCGPAResult = minCGPA.executeQuery();
        minCGPAResult.next();
        return minCGPAResult.getDouble(1);
    }

    private ResultSet getAvailableCourses(String semester) throws SQLException{
        if (semester == null) {
            System.out.println("No courses available for registration");
            return null;
        }
        PreparedStatement getAvailableCourses = conn.prepareStatement("SELECT course_offerings.course_code, course_catalog.course_name,instructors.name FROM course_offerings JOIN course_catalog ON course_offerings.course_code = course_catalog.course_code JOIN instructors ON course_offerings.instructor_id = instructors.instructor_id WHERE course_offerings.semester =?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getAvailableCourses.setString(1, semester);
        ResultSet resultSet = getAvailableCourses.executeQuery();
        resultSet.last();
        if (resultSet.getRow() == 0) {
            System.out.println("No courses available for registration");
            return null;
        }
        return resultSet;
    }

    private boolean updateCourseEnroll(String course_code, String semester) {
        try {
            PreparedStatement enrolCourse = conn.prepareStatement("INSERT INTO course_enrollments (course_code, semester, student_id) VALUES(?,?,?);");
            enrolCourse.setString(1, course_code);
            enrolCourse.setString(2, semester);
            enrolCourse.setString(3, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            enrolCourse.execute();
            System.out.println("You have successfully registered for " + course_code);
            return true;
        } catch (SQLException ex) {
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

    public void dropCourse() {
        if (!viewCourses()) {
            return;
        }
        String course_code = Utils.getInput("Enter the course code of the course you want to drop. Press 0 to exit");
        if (course_code.equals("0")) {
            return;
        }
        if (!Utils.validateEventTime("course_add_drop", Utils.getCurrentSession())) {
            System.out.println("You are not allowed to drop courses now");
            return;
        }
        try {
            PreparedStatement dropCourse = conn.prepareStatement("DELETE FROM course_enrollments WHERE course_code =? AND student_id =? AND semester=?;");
            dropCourse.setString(1, course_code);
            dropCourse.setString(2, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            dropCourse.setString(3, Utils.getCurrentSession());
            dropCourse.executeUpdate();
            if (dropCourse.getUpdateCount() == 0) {
                System.out.println("You are not registered for this course");
                return;
            }
            System.out.println("You have successfully dropped the course");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean viewCourses() {
        ResultSet resultSet = null;
        try {
            PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code WHERE course_enrollments.student_id =? AND course_enrollments.semester=?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getRegisteredCourses.setString(1, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            getRegisteredCourses.setString(2, Utils.getCurrentSession());
            resultSet = getRegisteredCourses.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!resultSet.next()) {
                System.out.println("You are not registered for any courses in the current semester");
                return false;
            }
            Utils.printTable(resultSet, new String[]{"Course Code", "Course Name"}, "Please find the list of courses you are registered for in the current semester");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void viewGrades() {
        ResultSet resultSet = null;
        try {
            PreparedStatement getRegisteredCourses = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade FROM course_catalog JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code WHERE course_enrollments.student_id =?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getRegisteredCourses.setString(1, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            resultSet = getRegisteredCourses.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!resultSet.next()) {
                System.out.println("You have not completed/registered any courses");
                return;
            }
            Utils.printTable(resultSet, new String[]{"Course Code", "Course Name", "Grade"}, "Please find your grades for the courses you have taken so far");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printGPA() {
        System.out.println("Your CGPA is: " + computeGPA());
    }

    private BigDecimal computeGPA() {
        try {
            CallableStatement calculateCGPA = conn.prepareCall("{? = call calculate_cgpa(?)}");
            calculateCGPA.registerOutParameter(1, Types.NUMERIC);
            calculateCGPA.setString(2, this.email_id.substring(0, this.email_id.indexOf("@")).toUpperCase());
            calculateCGPA.execute();
            return calculateCGPA.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}

