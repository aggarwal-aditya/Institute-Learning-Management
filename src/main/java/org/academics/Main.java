package org.academics;

import java.sql.Connection;
import java.util.Scanner;


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
            case 1:
                if (try_login(user)) break;
                switch (user.userRole) {
                    case "student" -> studentMenu(user);
                    case "instructor" -> teacherMenu(user);
                    case "admin" -> adminMenu();
                }
                break;
            case 2:
                try {
                    user.resetPassword();
                    System.out.println("Password reset successful. Please login again.");
                } catch (Exception e) {
                    System.out.println("Unable to reset password at the moment. Please try again later.");
                    mainMenu();
                }
                mainMenu();
                break;
            case 3:
                System.out.println("Thank you for using ILM");
                return;
            default:
                System.out.println("Invalid choice");
                break;
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

    public static void studentMenu(User user) {
        System.out.println("1. Register for a course");
        System.out.println("2. Drop a course");
        System.out.println("3. View your courses");
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
        Student student = new Student(user);
        switch (choice) {
            case 1:
                student.registerCourse();
                break;
            case 2:
                student.dropCourse();
                break;
            case 3:
                student.viewCourses();
                break;
            case 4:
                student.viewGrades();
                try {
                    student.viewGrades();
                }catch (Exception e) {
                    System.out.println("Unable to fetch grades at the moment. Please try again later.");
                }
                break;
            case 5:
                try {
                    student.computeGPA();
                } catch (Exception e) {
                    System.out.println("Unable to compute GPA at the moment. Please try again later.");
                }
                break;
            case 6:
                user.viewProfile();
                break;
            case 7:
                mainMenu();
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }
    }

    public static void teacherMenu(User user) {
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
        Teacher teacher = new Teacher(user);
        switch (choice){
            case 1:
                try {
                    teacher.viewCourses();
                    teacherMenu(user);
                } catch (Exception e) {
                    System.out.println("Unable to fetch courses at the moment. Please try again later.");
                }
                break;
            case 2:
                try {
                    teacher.floatCourse();
                } catch (Exception e) {
                    System.out.println("Unable to float course at the moment. Please try again later.");
                }
                break;
            case 3:
                try {
                    teacher.delistCourse();
                } catch (Exception e) {
                    System.out.println("Unable to delist course at the moment. Please try again later.");
                }
                break;
            case 4:
                try {
                    teacher.uploadGrades();
                } catch (Exception e) {
                    System.out.println("Unable to upload grades at the moment. Please try again later.");
                }
                break;
            case 5:
                try {
                    teacher.viewStudentGrades();
                } catch (Exception e) {
                    System.out.println("Unable to fetch student grades at the moment. Please try again later.");
                }
                break;
            case 6:
                user.viewProfile();
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