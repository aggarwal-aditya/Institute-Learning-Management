package org.academics.menus;

import org.academics.users.Admin;
import org.academics.users.Instructor;
import org.academics.users.Student;
import org.academics.users.User;
import org.academics.utility.Utils;

public class MainMenu {
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
                try {
                    if (!user.login()) {
                        mainMenu();
                    }
                } catch (Exception e) {
//                    System.out.println("Unable to login at the moment. Please try again later.");
//                    mainMenu();
                }
                if(user.userRole!=null) {
                    switch (user.userRole) {
                        case "student" -> {
                            Student student = new Student(user);
                            StudentMenu.studentMenu(student);
                        }
                        case "instructor" -> {
                            Instructor instructor = new Instructor(user);
                            InstructorMenu.instructorMenu(instructor);
                        }
                        case "admin" -> {
                            Admin admin = new Admin();
                            AdminMenu.adminMenu(admin);
                        }
                    }
                }
            }
            case 2 -> {
                try {
                    if (user.resetPassword())
                        System.out.println("Password reset successful. Please login again.");
                } catch (Exception e) {
//                    System.out.println("Unable to reset password at the moment. Please try again later.");
                }
                mainMenu();
            }
            case 3 -> {
                System.out.println("Thank you for using ILM");
                return;
            }
        }
    }
}
