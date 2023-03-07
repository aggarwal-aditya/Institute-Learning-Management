package org.academics.users;

import org.academics.dal.dbUser;
import org.academics.utility.MailManagement;
import org.academics.utility.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * A class to represent a user.
 */

public class User {
    public String userRole;
    public String email_id;
    Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a new User object with null user role and email ID.
     */
    public User() {
        this.userRole = null;
        this.email_id = null;
    }

    /**
     * Constructs a new User object with the given user role and email ID.
     *
     * @param userRole The user's role.
     * @param email_id The user's email ID.
     */
    public User(String userRole, String email_id) {
        this.userRole = userRole;
        this.email_id = email_id;
    }


    /**
     * Set the user role and email ID.
     *
     * @param userRole The user's role.
     * @param email_id The user's email ID.
     */
    public void setUserDetails(String userRole, String email_id) {
        this.userRole = userRole;
        this.email_id = email_id;
    }


    /**
     * Authenticates a user by requesting their email and password, validating the credentials
     * with the database, and setting the user details if the credentials are valid.
     *
     * @return true if the credentials are valid and the user details are set, false otherwise.
     * @throws SQLException if there is an error accessing the database.
     */
    public boolean login() throws SQLException {
        // Request email and password input from the user
        String email_id = Utils.getInput("Enter your username(email):");
        String password = Utils.getInput("Enter your password:");
        // Validate the credentials with the database and get the user's role
        String role = dbUser.validateCredentials(email_id, password);

        // If the role is not null, the credentials are valid
        if (role != null) {
            // Set the user details with the user's role and email ID
            this.setUserDetails(role, email_id);
            System.out.println("Welcome " + this.email_id);
            return true;
        } else {
            // If the role is null, the credentials are invalid
            System.out.println("Invalid credentials. Redirecting to Main Menu");
            return false;
        }
    }


    /**
     * Resets the user's password by generating and sending an OTP to their email address.
     *
     * @return true if the password was reset successfully, false otherwise.
     * @throws SQLException if an error occurs while accessing the database.
     */
    public boolean resetPassword() throws SQLException {
        // Prompt the user to enter their username (email address)
        String email_id = Utils.getInput("Enter your username(email):");

        // Check if the username exists in the database
        if (!dbUser.validateCredentials(email_id)) {
            System.out.println("Username not registered with ILM. Contact Admin for new Account Creation.");
            return false;
        }

        // Generate a random OTP and send it to the user's email address
        int otp = Utils.generateOTP();
        MailManagement mailManagement = new MailManagement();
        String[] toEmails = {email_id};
        String subject = "Reset Password";
        String message = "Your OTP to reset your ILM password is: " + otp;
        mailManagement.sendMail(subject, message, toEmails);

        // Prompt the user to enter the OTP sent to their email address
        int enteredOTP = Integer.parseInt(Utils.getInput("Enter the OTP sent on your email to reset your password :"));

        // If the entered OTP is valid, prompt the user to enter their new password and update it in the database
        if (otp == enteredOTP) {
            String newPassword = Utils.getInput("Enter your new password:");
            this.email_id = email_id;
            dbUser.changePassword(this, newPassword);
        } else {
            // If the entered OTP is invalid, display an error message and return false
            System.out.println("Invalid OTP. Redirecting to Main Menu");
            return false;
        }
        // Return true if the password was reset successfully
        return true;
    }

    /**
     * This method displays the user's profile details and prompts the user to either edit their profile or go back to the main menu.
     *
     * @throws SQLException if there is an error accessing the database
     */
    public void viewProfile() throws SQLException {
        System.out.printf("Hi %s !\n", this.email_id);
        ResultSet profileDetails = dbUser.getProfileDetails(this);
        while (profileDetails.next()) {
            System.out.printf("ID: %s\n", profileDetails.getString(1));
            System.out.printf("Name: %s\n", profileDetails.getString(2));
            System.out.printf("Phone Number: %s\n", profileDetails.getString(3));
            System.out.printf("Department: %s\n", profileDetails.getString(4));
            if (this.userRole.equals("student")) {
                System.out.printf("Batch: %s\n", profileDetails.getString(5));
            } else if (this.userRole.equals("instructor")) {
                System.out.printf("Date of Joining: %s\n", profileDetails.getString(5));
            }
        }
        System.out.println("Press 1 to Edit your profile");
        System.out.println("Press 2 to go back to Main Menu");

        // Prompt user for choice
        int choice = Utils.getUserChoice(2);
        if (choice == 1) {
            editProfile();
        }
    }

    /**
     * Displays the menu for editing user profile and performs the selected action.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    public void editProfile() throws SQLException {
        // Display the menu options for editing user profile
        System.out.println("1. Update Phone Number");
        System.out.println("2. Update Password");
        System.out.println("3. Go back to Main Menu");

        // Get the user's choice from the menu options
        int choice = Utils.getUserChoice(3);

        // Perform the selected action based on the user's choice
        switch (choice) {
            case 1 -> {
                // Prompt the user to enter their new phone number and attempt to update it in the database
                String newPhoneNumber = Utils.getInput("Enter your new phone number:");
                if (dbUser.updatePhone(this, newPhoneNumber)) {
                    System.out.println("Phone number updated successfully");
                } else {
                    System.out.println("Unable to update phone number. Please try again later.");
                }
            }
            case 2 -> {
                // Prompt the user to enter their new password and attempt to update it in the database
                String newPassword = Utils.getInput("Enter your new password:");
                if (dbUser.changePassword(this, newPassword)) {
                    System.out.println("Password updated successfully");
                } else {
                    System.out.println("Unable to update password. Please try again later.");
                }
            }
            default -> {
            }
        }
    }

}
