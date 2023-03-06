package org.academics.menus;

import org.academics.users.Student;
import org.academics.utility.Utils;

public class StudentMenu {
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
            case 1 -> {
                student.registerCourse();
                studentMenu(student);
            }
            case 2 -> {
                try {
                    student.dropCourse();
                } catch (Exception e) {
                    System.out.println("Unable to drop course at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
            }
            case 3 -> {
                try {
                    student.viewCourses();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to fetch courses at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
            }
            case 4 -> {
                try {
                    student.viewGrades();
                } catch (Exception e) {
                    System.out.println("Unable to fetch grades at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
            }
            case 5 -> {
                try {
                    student.printGPA();
                } catch (Exception e) {
                    System.out.println("Unable to compute GPA at the moment. Please try again later.");
                    studentMenu(student);
                }
                studentMenu(student);
            }
            case 6 -> {
                try {
                    student.viewProfile();
                } catch (Exception e) {
                    System.out.println("Unable to fetch profile at the moment. Please try again later.");
                }
                studentMenu(student);
            }
            case 7 -> {
                System.out.println("Logging out...");
                MainMenu.mainMenu();
            }
        }
    }
}
