package org.academics;

import java.sql.Connection;
import java.util.Scanner;

public class User {
    Scanner scanner = new Scanner(System.in);

    public void login(String username, String password) {
        //TODO: Implement login
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
        mailManagement.sendMail(subject, message, toEmails);
        System.out.println("Enter the OTP sent to your email:");
        int enteredOTP = scanner.nextInt();
        if (otp == enteredOTP) {
            System.out.println("Enter your new password:");
            String newPassword = scanner.next();
            changePassword(username, newPassword);
        }
        else {
            System.out.println("Invalid OTP");
        }
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        //TODO: Implement changePassword
    }

    public void changePassword(String username, String newPassword) {

    }
}
