package org.academics;

import java.sql.Connection;
import java.util.Scanner;
import org.academics.dao.*;
import org.academics.users.Instructor;
import org.academics.users.Student;
import org.academics.users.User;


class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void mainMenu() {

        System.out.println("Welcome to ILM (Institute Learning Management)");
        System.out.println("Please select your role to login:");
        System.out.println("1. Login");
        System.out.println("2. Reset Your Password (Use this only if you have forgotten your password)");
        System.out.println("3. Exit");
        System.out.println("Enter your choice:");
        int userType = scanner.nextInt();
        while (userType < 1 || userType > 3) {
            System.out.println("Invalid choice");
            System.out.println("Enter your choice:");
            userType = scanner.nextInt();
        }
        User user = new User();
        switch (userType) {
            case 1 -> {
                if (try_login(user)) break;
                switch (user.userRole) {
                    case "student" -> {
                        Student student = new Student(user);
                        studentMenu(student);
                    }
                    case "instructor" -> {
                        Instructor instructor = new Instructor(user);
                        instructorMenu(instructor);
                    }
                    case "admin" -> {
                        adminMenu();
                    }
                }
            }
            case 2 -> {
                try {
                    if(user.resetPassword())
                        System.out.println("Password reset successful. Please login again.");
                } catch (Exception e) {
                    System.out.println("Unable to reset password at the moment. Please try again later.");
                    mainMenu();
                }
                mainMenu();
            }
            case 3 -> {
                System.out.println("Thank you for using ILM");
                return;
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private static boolean try_login(User user) {
        try {
            user.login();
            if (user.email_id != null) {
                System.out.println("Welcome " + user.email_id);
            } else {
                System.out.println("Invalid username or password. Returning to main menu.");
                mainMenu();
                return true;
            }
        } catch (Exception e) {
            System.out.println("Unable to login at the moment. Please try again later.");
            mainMenu();
        }
        return false;
    }

    public static void studentMenu(Student student) {
        System.out.println("1. Register for a course");
        System.out.println("2. Drop a course");
        System.out.println("3. View your registered courses");
        System.out.println("4. View your grades");
        System.out.println("5. Compute your GPA");
        System.out.println("6. View your profile");
        System.out.println("7. Logout");
        System.out.println("Enter your choice:");
        int choice = scanner.nextInt();
        while (choice < 1 || choice > 7) {
            System.out.println("Invalid choice");
            System.out.println("Enter your choice:");
            choice = scanner.nextInt();
        }
        switch (choice) {
            case 1:
                try {
                    student.registerCourse();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to register for course at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
                break;
            case 2:
                student.dropCourse();
                break;
            case 3:
                try {
                    student.viewCourses();
                    studentMenu(student);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch courses at the moment. Please try again later.");
                    studentMenu(student);
                }
                break;
            case 4:
                try {
                    student.viewGrades();
                    studentMenu(student);
                } catch (Exception e) {
                    System.out.println("Unable to fetch grades at the moment. Please try again later.");
                    studentMenu(student);
                }
                break;
            case 5:
                try {
                    student.computeGPA();
                    studentMenu(student);
                } catch (Exception e) {
                    System.out.println("Unable to compute GPA at the moment. Please try again later.");
                    studentMenu(student);
                }
                break;
            case 6:
                try
                {
                    student.viewProfile();
                    studentMenu(student);
                }
                catch (Exception e)
                {
                    System.out.println("Unable to fetch profile at the moment. Please try again later.");
                    studentMenu(student);
                }
                break;
            case 7:
                mainMenu();
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }
    }

    public static void instructorMenu(Instructor instructor) {
        System.out.println("1. View your courses");
        System.out.println("2. Float a new course");
        System.out.println("3. Delist a course");
        System.out.println("4. Upload Grades");
        System.out.println("5. View student grades");
        System.out.println("6. View your profile");
        System.out.println("7. Logout");
        System.out.println("Enter your choice:");
        int choice = scanner.nextInt();
        while (choice < 1 || choice > 7) {
            System.out.println("Invalid choice");
            System.out.println("Enter your choice:");
            choice = scanner.nextInt();
        }
        switch (choice) {
            case 1:
                try {
                    instructor.viewCourses();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch courses at the moment. Please try again later.");
                }
                break;
            case 2:
                try {
                    instructor.floatCourse();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to float course at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
                break;
            case 3:
                try {
                    instructor.delistCourse();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to delist course at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
                break;
            case 4:
                try {
                    instructor.uploadGrades();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to upload grades at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
                break;
            case 5:
                try {
                    instructor.viewStudentGrades();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch student grades at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
                break;
            case 6:
                try {
                    instructor.viewProfile();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch profile at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
                break;
            case 7:
                mainMenu();
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }

    }

    public static void adminMenu() {

    }

    public static void main(String[] args) {
        JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
        Connection conn = jdbc.getConnection();
        if (conn == null) {
            System.out.println("Connection failed. Please try again later.");
            return;
        }
        mainMenu();
    }

}