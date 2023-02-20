package org.academics.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ilmtest", "postgres", "password");
    }

    @AfterEach
    void tearDown() throws SQLException {
        System.setIn(System.in);
        System.setOut(System.out);
        connection.close();
    }


    @Test
    void testEnrolCourse(){
        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);

    }
    @Test
    void testDropCourse(){
        try{
            CallableStatement callableStatement = connection.prepareCall("{call populate_database()}");
            callableStatement.execute();
            User user=new User("student","2020csb1066@iitrpr.ac.in");
            Student student=new Student(user);
            String input = "CS201";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            student.dropCourse();
            String output = outputStream.toString();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM course_enrollments WHERE student_id = ? AND course_code = ?");
            preparedStatement.setString(1, "2020CSB1066");
            preparedStatement.setString(2, "CS101");
            ResultSet resultSet = preparedStatement.executeQuery();
            assert (!resultSet.next());
            assert (output.contains("You have successfully dropped the course"));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call clear_database()}");
                callableStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    void testNotEnrolledDroppedCourse(){
        try{
            CallableStatement callableStatement = connection.prepareCall("{call populate_database()}");
            callableStatement.execute();
            User user=new User("student","2020csb1066@iitrpr.ac.in");
            Student student=new Student(user);
            String input = "CS202";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            student.dropCourse();
            String output = outputStream.toString();
            assert (output.contains("You are not registered for this course"));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call clear_database()}");
                callableStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    void testNoDroppedCourse(){
        try{
            CallableStatement callableStatement = connection.prepareCall("{call populate_database()}");
            callableStatement.execute();
            User user=new User("student","2020csb1066@iitrpr.ac.in");
            Student student=new Student(user);
            String input = "0";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            student.dropCourse();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call clear_database()}");
                callableStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    void testNothingEnrolledDropCourse(){
        User user=new User("student","2020csb1066@iitrpr.ac.in");
        Student student=new Student(user);
        String input = "0";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        student.dropCourse();
        String output = outputStream.toString();
        assert (output.contains("You are not registered for any courses in the current semester"));
    }

    @Test
    void testViewCourses() {

        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);
        try {
            CallableStatement callableStatement = connection.prepareCall("{call populate_database()}");
            callableStatement.execute();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            student.viewCourses();
            String output = outputStream.toString();
            assert (output.contains("Course Code")); assert (output.contains("Name"));
            assert (output.contains("CS201")); assert (output.contains("Data Structures"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call clear_database()}");
                callableStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    void testNonViewCourses() {
        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        student.viewCourses();
        String output = outputStream.toString();
        assertTrue(output.contains("You are not registered for any courses in the current semester"));
    }
    @Test
    void testViewGrades() {

        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);
        try {
            CallableStatement callableStatement = connection.prepareCall("{call populate_database()}");
            callableStatement.execute();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            student.viewGrades();
            String output = outputStream.toString();
            assert (output.contains("Course Code")); assert (output.contains("Name")); assert (output.contains("Grade"));
            assert (output.contains("CS201")); assert (output.contains("Data Structures")); assert (output.contains("A"));
            assert (output.contains("CS202")); assert (output.contains("Algorithms")); assert (output.contains("A-"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call clear_database()}");
                callableStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    void testNonViewGrades() {
        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        student.viewGrades();
        String output = outputStream.toString();
        assertTrue(output.contains("You have not completed/registered any courses"));
    }
    @Test
    void testComputeGPA() {
        User user = new User("student", "2020csb1066@iitrpr.ac.in");
        Student student = new Student(user);
        try {
            CallableStatement callableStatement = connection.prepareCall("{call populate_database()}");
            callableStatement.execute();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        student.printGPA();
        String output = outputStream.toString();
        assertTrue(output.contains("Your CGPA is: 9.50"));
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                CallableStatement callableStatement = connection.prepareCall("{call clear_database()}");
                callableStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}