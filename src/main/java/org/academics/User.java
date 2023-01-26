package org.academics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    Scanner scanner = new Scanner(System.in);

    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();
    public String userRole;
    public String username;

    public User() {
        this.userRole = null;
        this.username = null;
    }

    public void setUserDetails(String userRole, String username) {
        this.userRole = userRole;
        this.username = username;
    }

    public String login() {
        System.out.println("Enter your username(email):");
        String username = scanner.next();
        System.out.println("Enter your password:");
        String password = scanner.next();
        //MD5 hash the password
        String hashedPassword;
        try {
            hashedPassword = Utils.getMD5Hash(password);
        } catch (Exception e) {
//            System.out.println("Unable to login at the moment. Please try again later.");
            throw new RuntimeException(e);
        }
        return username;
        //TODO Validate details in PostgreSQL
    }

    public void resetPassword() {
        System.out.println("Enter your username(email):");
        String username = scanner.next();
        MailManagement mailManagement = new MailManagement();
        int otp = Utils.generateOTP();
        String[] toEmails = {username};
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
                changePassword(username, newPassword);
            } catch (Exception e) {
//                System.out.println("Unable to reset password at the moment. Please try again later.");
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Invalid OTP. Redirecting to Main Menu");
        }
    }

    public void changePassword(String username, String newPassword) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("UPDATE users SET password = ? WHERE email_id = ?");
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            assert preparedStatement != null;
            preparedStatement.close();
            throw new RuntimeException(e);
        }

    }
}
