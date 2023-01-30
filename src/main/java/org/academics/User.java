package org.academics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    Scanner scanner = new Scanner(System.in);

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();
    public String userRole;
    public String email_id;

    public User() {
        this.userRole = null;
        this.email_id = null;
    }

    public void setUserDetails(String userRole, String email_id) {
        this.userRole = userRole;
        this.email_id = email_id;
    }

    public void login() {
        System.out.println("Enter your username(email):");
        String email_id = scanner.next();
        System.out.println("Enter your password:");
        String password = scanner.next();
//        String password= Arrays.toString(System.console().readPassword());
        PreparedStatement userDetails = null;
        PreparedStatement statementRole=null;
        try {
            userDetails = conn.prepareStatement("SELECT * FROM users WHERE email_id = ? AND password = ?");
            userDetails.setString(1, email_id);
            userDetails.setString(2, password);
            if (userDetails.executeQuery().next()) {
                statementRole = conn.prepareStatement("SELECT role FROM users WHERE email_id = ?");
                statementRole.setString(1, email_id);
                ResultSet resultSet=statementRole.executeQuery();
                while (resultSet.next()){
                    userRole=resultSet.getString(1);
                }
                setUserDetails(userRole, email_id);
            }
            else {
                return;
            }
        } catch (Exception e) {
            //Print exception stack trace
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void resetPassword() {
        System.out.println("Enter your username(email):");
        String email_id = scanner.next();
        //Check if the username exists
        PreparedStatement userDetails = null;
        try {
            userDetails = conn.prepareStatement("SELECT * FROM users WHERE email_id = ?");
            userDetails.setString(1, email_id);
            if (!userDetails.executeQuery().next()) {
                System.out.println("Invalid username. Redirecting to Main Menu");
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MailManagement mailManagement = new MailManagement();
        int otp = Utils.generateOTP();
        String[] toEmails = {email_id};
        String subject = "Reset Password";
        String message = "Your OTP to reset your ILM password is: " + otp;
        try {
            mailManagement.sendMail(subject, message, toEmails);
        } catch (Exception e) {
//            System.out.println("Unable to reset password at the moment. Please try again later.");
            throw new RuntimeException(e);
        }
        System.out.println("Enter the OTP sent on your email to reset your password :");
        int enteredOTP = scanner.nextInt();
        if (otp == enteredOTP) {
            System.out.println("Enter your new password:");
            String newPassword = scanner.next();
            try {
                changePassword(email_id, newPassword);
            } catch (Exception e) {
//                System.out.println("Unable to reset password at the moment. Please try again later.");
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Invalid OTP. Redirecting to Main Menu");
        }
    }

    public void changePassword(String email_id, String newPassword) throws SQLException {
        PreparedStatement userDetails = null;
        try {
            userDetails = conn.prepareStatement("UPDATE users SET password = ? WHERE email_id = ?");
            userDetails.setString(1, newPassword);
            userDetails.setString(2, email_id);
            userDetails.executeUpdate();
            userDetails.close();
        } catch (Exception e) {
            assert userDetails != null;
            userDetails.close();
            throw new RuntimeException(e);
        }

    }

    public void viewProfile(){
        System.out.printf("Hi %s !\n", this.email_id);
        PreparedStatement userDetails = null;
        try {
            if(this.userRole.equals("student")){
                userDetails=conn.prepareStatement("SELECT * FROM students WHERE email_id = ?");
            }
            else if(this.userRole.equals("instructor")){
                userDetails=conn.prepareStatement("SELECT * FROM instructors WHERE email_id = ?");
            }
            assert userDetails != null;
            userDetails.setString(1, this.email_id);
            ResultSet resultSet=userDetails.executeQuery();
            //    TODO

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
