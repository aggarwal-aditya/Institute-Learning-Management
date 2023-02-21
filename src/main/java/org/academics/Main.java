package org.academics;

import java.sql.Connection;
import java.util.Scanner;
import org.academics.dal.*;
import org.academics.users.Admin;
import org.academics.users.Instructor;
import org.academics.users.Student;
import org.academics.users.User;
import org.academics.utility.Utils;


class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void mainMenu() {

        System.out.println("Welcome to ILM (Institute Learning Management)");
        System.out.println("Please select your role to login:");
        System.out.println("1. Login");
        System.out.println("2. Reset Your Password (Use this only if you have forgotten your password)");
        System.out.println("3. Exit");
        System.out.println("Enter your choice:");
        int userChoice = Utils.getUserChoice(3);
        User user = new User();
        switch (userChoice) {
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
                        Admin admin = new Admin(user);
                        adminMenu(admin);
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
        int choice = Utils.getUserChoice(7);

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
                try {
                    student.dropCourse();
                }
                catch (Exception e) {
                    System.out.println("Unable to drop course at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
                break;
            case 3:
                try {
                    student.viewCourses();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch courses at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
                break;
            case 4:
                try {
                    student.viewGrades();
                } catch (Exception e) {
                    System.out.println("Unable to fetch grades at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
                break;
            case 5:
                try {
                    student.printGPA();
                } catch (Exception e) {
                    System.out.println("Unable to compute GPA at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
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
        int choice = Utils.getUserChoice(7);
        switch (choice) {
            case 1 -> {
                try {
                    instructor.viewCourses();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch courses at the moment. Please try again later.");
                }
            }
            case 2 -> {
                try {
                    instructor.floatCourse();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to float course at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
            }
            case 3 -> {
                try {
                    instructor.delistCourse();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to delist course at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
            }
            case 4 -> {
                try {
                    instructor.uploadGrades();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to upload grades at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
            }
            case 5 -> {
                try {
                    instructor.viewStudentGrades();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch student grades at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
            }
            case 6 -> {
                try {
                    instructor.viewProfile();
                    instructorMenu(instructor);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch profile at the moment. Please try again later.");
                    instructorMenu(instructor);
                }
            }
            case 7 -> mainMenu();
            default -> System.out.println("Invalid choice");
        }

    }

    public static void adminMenu(Admin admin) {
        System.out.println("1. Add Course in Course Catalog");
        System.out.println("2. Add Semester Timeline");
        System.out.println("3. View Student Grades");
        System.out.println("4. Generate Transcript");
        System.out.println("5. Check Graduation Eligibility");
        System.out.println("6. Change System Settings(For Testing Only)");
        System.out.println("7. Logout");
        switch (Utils.getUserChoice(5)) {
            case 1 -> {
                try {
                    admin.updateCourseCatalog();
                    adminMenu(admin);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to add course at the moment. Please try again later.");
                    adminMenu(admin);
                }
            }
            case 2 -> {
                try {
                    admin.addSemesterTimeline();
                    adminMenu(admin);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to add semester at the moment. Please try again later.");
                    adminMenu(admin);
                }
            }
            case 3 -> {
                try {
                    admin.viewStudentGrades();
                    adminMenu(admin);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch student grades at the moment. Please try again later.");
                    adminMenu(admin);
                }
            }
            case 4 -> {
                try {
                    admin.generateTranscript();
                    adminMenu(admin);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to generate transcript at the moment. Please try again later.");
                    adminMenu(admin);
                }
            }
            case 5 -> {
                try {
                    admin.checkGraduationStatus();
                    adminMenu(admin);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to change system settings at the moment. Please try again later.");
                    adminMenu(admin);
                }
            }
            case 6 -> {
                try {
                    admin.changeSystemSettings();
                    adminMenu(admin);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to change system settings at the moment. Please try again later.");
                    adminMenu(admin);
                }
            }
            case 7 -> mainMenu();
            default -> System.out.println("Invalid choice");
        }

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