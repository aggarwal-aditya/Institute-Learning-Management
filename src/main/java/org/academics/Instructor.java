package org.academics;

import dnl.utils.text.table.TextTable;
import org.academics.dao.JDBCPostgreSQLConnection;
import org.academics.utility.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Instructor extends User {
    private final int instructor_id;
    Scanner scanner = new Scanner(System.in);
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Instructor(User user) {
        super(user.userRole, user.email_id);
        this.instructor_id = getInstructorId();
    }

    public int getInstructorId() {
        try {
            PreparedStatement getInstructorId = conn.prepareStatement("SELECT instructor_id FROM instructors WHERE email_id = ?");
            getInstructorId.setString(1, this.email_id);
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
            PreparedStatement viewCourses = conn.prepareStatement("SELECT course_offerings.course_code,course_offerings.semester,course_offerings.qualify,course_offerings.enrollment_count FROM course_offerings WHERE instructor_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            viewCourses.setInt(1, instructor_id);
            ResultSet resultSet = viewCourses.executeQuery();
            resultSet.last();
            if (resultSet.getRow() == 0) {
                System.out.println("You have not floated any courses");
                return;
            }
            resultSet.beforeFirst();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void floatCourse() {
        try {
            PreparedStatement getCourseCodes = conn.prepareStatement("SELECT course_catalog.course_code,course_catalog.course_name, prerequisite FROM course_catalog", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = getCourseCodes.executeQuery();
            resultSet.last();
            if (resultSet.getRow() == 0) {
                System.out.println("No courses approved by Senate");
                return;
            }
            resultSet.beforeFirst();
            String[] columnNames = {"Course Code", "Course Name"};
            List<Object[]> data = new ArrayList<>();
            while (resultSet.next()) {
                Object[] course_detail = new Object[2];
                course_detail[0] = resultSet.getString(1);
                course_detail[1] = resultSet.getString(2);
                data.add(course_detail);
            }
            Object[][] courses = data.toArray(new Object[0][]);
            TextTable courseTable = new TextTable(columnNames, courses);
            System.out.println("List of courses approved by Senate");
            courseTable.printTable();
            System.out.println("Enter the course code to float the course");
            String course_code = scanner.next();
            System.out.println("Enter the session (YYYY-Semester)");
            String session = scanner.next();
            PreparedStatement validateCourse = conn.prepareStatement("SELECT instructors.name FROM course_offerings JOIN instructors  on course_offerings.instructor_id = instructors.instructor_id WHERE course_offerings.course_code = ? AND course_offerings.semester = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            validateCourse.setString(1, course_code);
            validateCourse.setString(2, session);
            resultSet = validateCourse.executeQuery();
            if (resultSet.next()) {
                System.out.println("The course is already floated by Dr." + resultSet.getString(1));
                return;
            }
            System.out.println("Enter the minimum CGPA requirement for the course");
            int qualify = scanner.nextInt();
            System.out.println("Do you want to add additional prerequisites? (Y/N)");
            String choice = scanner.next();
            //Make a 2d array of prerequisites
            ArrayList<String> preRequisites = new ArrayList<>();
            StringBuilder pre = new StringBuilder();
            if(Objects.equals(choice, "Y")){
                do {
                    System.out.println("Enter the course code of the prerequisite");
                    String code = scanner.next();
                    System.out.println("Enter the minimum grade requirement for the prerequisite (Enter 'E' if no minimum grade requirement)");
                    String grade = scanner.next();
                    pre.append(code).append("(").append(grade).append(")").append("|");
                    System.out.println("Are there any alternatives to the prerequisite? (Y/N)");
                    choice = scanner.next();
                    if(Objects.equals(choice, "Y")){
                        do{
                            System.out.println("Enter the course code of the alternative");
                            code = scanner.next();
                            System.out.println("Enter the minimum grade requirement for the alternative (Enter Pass if no minimum grade requirement)");
                            grade = scanner.next();
                            pre.append(code).append("(").append(grade).append(")").append("|");
                            System.out.println("Are there any more alternatives to the prerequisite? (Y/N)");
                            choice = scanner.next();
                        }while (Objects.equals(choice, "Y"));
                    }
                    System.out.println("Do you want to add additional prerequisites? (Y/N)");
                    choice = scanner.next();
                    preRequisites.add(String.valueOf(pre));
                    pre= new StringBuilder();
                } while (Objects.equals(choice, "Y"));
            }
            PreparedStatement floatCourse = conn.prepareStatement("INSERT INTO course_offerings (course_code, semester, instructor_id, enrollment_count, qualify, prerequisite) VALUES (?, ?, ?, ?, ?, ?)");
            floatCourse.setString(1, course_code);
            floatCourse.setString(2, session);
            floatCourse.setInt(3, instructor_id);
            floatCourse.setInt(4, 0);
            floatCourse.setInt(5, qualify);
            floatCourse.setArray(6, conn.createArrayOf("text", preRequisites.toArray()));
            floatCourse.executeUpdate();
            System.out.println("Course floated successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }


    public void uploadGrades() {
        System.out.println("Enter the course code");
        String course_code = scanner.next();
        System.out.println("Enter the session (YYYY-Semester)");
        String session = scanner.next();
        try {
            PreparedStatement validateCourse = conn.prepareStatement("SELECT * FROM course_offerings WHERE course_code = ? AND semester = ? AND instructor_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            validateCourse.setString(1, course_code);
            validateCourse.setString(2, session);
            validateCourse.setInt(3, instructor_id);
            ResultSet resultSet = validateCourse.executeQuery();
            if (!resultSet.next()) {
                System.out.println("You have not floated the course or the course does not exist");
                return;
            }
            PreparedStatement getEnrolledStudents = conn.prepareStatement("SELECT course_enrollments.student_id, students.name FROM course_enrollments JOIN students on course_enrollments.student_id = students.student_id WHERE course_code = ? AND semester = ?");
            getEnrolledStudents.setString(1, course_code);
            getEnrolledStudents.setString(2, session);
            resultSet = getEnrolledStudents.executeQuery();
            Utils.exportCSV(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void delistCourse() {

    }


    public void viewStudentGrades() {
        System.out.println("1. Select a course to view grades");
        System.out.println("2. Search a student to view grades");
        System.out.println("3. Go back to main menu");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: {
                System.out.println("Enter the course code");
                String course_code = scanner.next();
                System.out.println("Enter the session (YYYY-Semester)");
                String session = scanner.next();
                ResultSet resultSet;
                try {
                    PreparedStatement getGrades = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.semester, course_enrollments.student_id, students.name, course_enrollments.grade FROM course_enrollments JOIN course_catalog ON course_enrollments.course_code=course_catalog.course_code JOIN students ON course_enrollments.student_id = students.student_id WHERE course_enrollments.course_code = ? AND course_enrollments.semester = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    getGrades.setString(1, course_code);
                    getGrades.setString(2, session);
                    resultSet = getGrades.executeQuery();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    resultSet.last();
                    int rowCount = resultSet.getRow();
                    if (rowCount == 0) {
                        System.out.println("No course was found with the given course code and session or no students enrolled in the course");
                        return;
                    }
                    formatOutput(resultSet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 2:
                System.out.println("Enter the student's Enrollment ID");
                String enrollment_id = scanner.next();
                ResultSet resultSet;
                try {
                    PreparedStatement getGrades = conn.prepareStatement("SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.semester, course_enrollments.student_id, students.name, course_enrollments.grade FROM course_enrollments JOIN course_catalog ON course_enrollments.course_code=course_catalog.course_code JOIN students ON course_enrollments.student_id = students.student_id WHERE course_enrollments.student_id = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    getGrades.setString(1, enrollment_id);
                    resultSet = getGrades.executeQuery();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    resultSet.last();
                    int rowCount = resultSet.getRow();
                    if (rowCount == 0) {
                        System.out.println("No student was found with the given enrollment ID or no courses enrolled by the student");
                        return;
                    }
                    formatOutput(resultSet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid choice. Redirecting to main menu");
                break;
        }
    }

    private void formatOutput(ResultSet resultSet) throws SQLException {
        resultSet.beforeFirst();
        String[] columnNames = {"Course Code", "Course Name", "Semester", "Student ID", "Student Name", "Grade"};
        List<Object[]> data = new ArrayList<>();
        while (resultSet.next()) {
            Object[] course_detail = new Object[6];
            course_detail[0] = resultSet.getString(1);
            course_detail[1] = resultSet.getString(2);
            course_detail[2] = resultSet.getString(3);
            course_detail[3] = resultSet.getString(4);
            course_detail[4] = resultSet.getString(5);
            course_detail[5] = resultSet.getString(6);
            data.add(course_detail);
        }
        Object[][] courses = data.toArray(new Object[0][]);
        TextTable courseTable = new TextTable(columnNames, courses);
        courseTable.printTable();
    }
}
