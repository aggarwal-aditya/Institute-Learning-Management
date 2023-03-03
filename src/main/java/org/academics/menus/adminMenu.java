package org.academics.menus;

import org.academics.users.Admin;
import org.academics.users.specialPrivileges;
import org.academics.utility.Utils;

public class adminMenu {
    public static void adminMenu(Admin admin) {
        System.out.println("1. Add Course in Course Catalog");
        System.out.println("2. Add Semester Timeline");
        System.out.println("3. View Student Grades");
        System.out.println("4. Generate Transcript");
        System.out.println("5. Check Graduation Eligibility");
        System.out.println("6. Change System Settings(For Testing Only)");
        System.out.println("7. Logout");
        switch (Utils.getUserChoice(7)) {
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
                    specialPrivileges.viewStudentGrades();
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
            case 7 -> mainMenu.mainMenu();
            default -> System.out.println("Invalid choice");
        }

    }
}
