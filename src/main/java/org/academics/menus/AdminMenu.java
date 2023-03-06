package org.academics.menus;

import org.academics.users.Admin;
import org.academics.users.specialPrivileges;
import org.academics.utility.Utils;

public class AdminMenu {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to add course at the moment. Please try again later.");
                }
                adminMenu(admin);
            }
            case 2 -> {
                try {
                    admin.addSemesterTimeline();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to add semester at the moment. Please try again later.");
                }
                adminMenu(admin);
            }
            case 3 -> {
                try {
                    specialPrivileges.viewStudentGrades();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to view student grades at the moment. Please try again later.");
                }
                adminMenu(admin);
            }
            case 4 -> {
                try {
                    admin.generateTranscript();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to generate transcript at the moment. Please try again later.");
                }
                adminMenu(admin);
            }
            case 5 -> {
                try {
                    if(admin.checkGraduationStatus()){
                        System.out.println("Student is eligible for graduation. Download transcript to get the degree.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to check graduation status at the moment. Please try again later.");
                }
                adminMenu(admin);
            }
            case 6 -> {
                admin.changeSystemSettings();
                adminMenu(admin);
            }
            case 7 -> {
                System.out.println("Logging out...");
                MainMenu.mainMenu();
            }
        }

    }
}
