package org.academics;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailManagement {
    private String fromEmail;

    //Read the password from .env file
    private String password;


    public MailManagement() {
        Dotenv dotenv = Dotenv.load();
        password=dotenv.get("EMAIL_PASSWORD");
        fromEmail = dotenv.get("EMAIL");


    }

    public void sendMail(String subject, String message, String[] toEmails) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp-mail.outlook.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(prop, null);
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(fromEmail));

            InternetAddress[] toAddresses = new InternetAddress[toEmails.length];
            for (int i = 0; i < toEmails.length; i++) {
                toAddresses[i] = new InternetAddress(toEmails[i]);
            }
            mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp-mail.outlook.com", fromEmail, password);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
//MailManagement mailManagement = new MailManagement("sender@gmail.com", "password");
//String[] toEmails = {"receiver1@gmail.com", "receiver2@gmail.com"};
//mailManagement.sendMail("Hello World", toEmails);
