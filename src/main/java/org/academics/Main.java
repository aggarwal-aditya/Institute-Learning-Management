package org.academics;

import java.sql.Connection;
import java.util.Scanner;


class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void mainMenu() {

        System.out.println("Welcome to ILM (Institute Learning Management)");
        System.out.println("Please select your role to login:");
        System.out.println("1. Student");
        System.out.println("2. Teacher");
        System.out.println("3. Admin");
        System.out.println("4. Reset Your Password (Use this if you have forgotten your password)");
        System.out.println("5. Exit");
        System.out.println("Enter your choice:");
        int userType = scanner.nextInt();
        while (userType < 1 || userType > 5) {
            System.out.println("Invalid choice");
            System.out.println("Enter your choice:");
            userType = scanner.nextInt();
        }
        User user = new User();
        String username;
        switch (userType) {
            case 1:
                try {
                    username = user.login();
                    System.out.println("Welcome " + username);
                }catch (Exception e) {
                    System.out.println("Unable to login at the moment. Please try again later.");
                    mainMenu();
                }
                studentMenu();
                break;
            case 2:
                try {
                    username = user.login();
                    System.out.println("Welcome " + username);
                }catch (Exception e) {
                    System.out.println("Unable to login at the moment. Please try again later.");
                    mainMenu();
                }
                teacherMenu();
                break;
            case 3:
                try {
                    username = user.login();
                    System.out.println("Welcome " + username);
                }catch (Exception e) {
                    System.out.println("Unable to login at the moment. Please try again later.");
                    mainMenu();
                }
                adminMenu();
                break;
            case 4:
                try {
                    user.resetPassword();
                    System.out.println("Password reset successful. Please login again.");
                }catch (Exception e) {
                    System.out.println("Unable to reset password at the moment. Please try again later.");
                    mainMenu();
                }
                mainMenu();
                break;
            case 5:
                System.out.println("Thank you for using ILM");
                return;
            default:
                System.out.println("Invalid choice");
                break;
        }
    }
    public static void studentMenu(){

    }
    public static void teacherMenu(){

    }
    public static void adminMenu(){

    }
    public static void main(String[] args) {
        JDBCPostgreSQLConnection jdbc=JDBCPostgreSQLConnection.getInstance();
        Connection conn = jdbc.getConnection();
        if(conn==null){
            System.out.println("Connection failed. Please try again later.");
            return;
        }
        mainMenu();
    }

}