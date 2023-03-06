package org.academics.utility;

import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * A utility class for sending emails using JavaMail API.
 */
public class MailManagement {
    String fromEmail;

    // Read the password from .env file
    String password;

    /**
     * Constructor that initializes the fromEmail and password using values from the .env file.
     */
    public MailManagement() {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        password = dotenv.get("EMAIL_PASSWORD");
        fromEmail = dotenv.get("EMAIL");
    }

    /**
     * Sends an email with the given subject and message to the specified email addresses.
     *
     * @param subject  The subject of the email.
     * @param message  The message body of the email.
     * @param toEmails An array of email addresses to send the email to.
     */
    public void sendMail(String subject, String message, String[] toEmails) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp-mail.outlook.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        // Create a mail session with the given properties
        Session session = Session.getDefaultInstance(prop, null);
        try {
            // Create a new message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(fromEmail));

            // Set the email recipients
            InternetAddress[] toAddresses = new InternetAddress[toEmails.length];
            for (int i = 0; i < toEmails.length; i++) {
                toAddresses[i] = new InternetAddress(toEmails[i]);
            }
            mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);

            // Set the email subject and message body
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            // Connect to the mail server and send the email
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp-mail.outlook.com", fromEmail, password);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
            System.out.println("Mail sent successfully!");
        } catch (MessagingException mex) {
//            mex.printStackTrace();
            // Throw a new runtime exception with the messaging exception as the cause
            throw new RuntimeException(mex);
        }
    }
}
