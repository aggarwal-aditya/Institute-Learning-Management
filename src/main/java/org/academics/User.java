package org.academics;

import java.sql.Connection;
import java.util.Scanner;

public class User {
    Scanner scanner = new Scanner(System.in);

    public void login() {
        JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
        Connection conn = jdbc.getConnection();
        System.out.println("Enter your username(email):");
        String username = scanner.next();
        System.out.println("Enter your password:");
        String password = scanner.next();
        //MD5 hash the password
        String hashedPassword;
        try {
            hashedPassword = utils.getMD5Hash(password);
        } catch (Exception e) {
            System.out.println("Unable to login at the moment. Please try again later.");
            return;
        }
        //TODO Validate details in PostgreSQL
    }

    public void resetPassword() {
        //TODO: Implement resetPassword
        System.out.println("Enter your username(email):");
        String username = scanner.next();
        MailManagement mailManagement = new MailManagement();
        int otp = utils.generateOTP();
        String[] toEmails = {username};
        String subject = "Reset Password";
        String message = "Your OTP to reset your ILM password is: " + otp;
        try {
            mailManagement.sendMail(subject, message, toEmails);
        } catch (Exception e) {
            System.out.println("Unable to reset password at the moment. Please try again later.");
            return;
        }
        System.out.println("Enter the OTP sent on your email to reset your password :");
        int enteredOTP = scanner.nextInt();
        if (otp == enteredOTP) {
            System.out.println("Enter your new password:");
            String newPassword = scanner.next();
            try {
                changePassword(username, newPassword);
            } catch (Exception e) {
                System.out.println("Unable to reset password at the moment. Please try again later.");
                return;
            }
        }
        else {
            System.out.println("Invalid OTP. Redirecting to Main Menu");
        }
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        //TODO: Implement changePassword
    }

    public void changePassword(String username, String newPassword) {

    }
}
