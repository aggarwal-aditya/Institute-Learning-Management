package org.academics.utility;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MailManagementTest {

    @Test
    public void testSendMail() {
        MailManagement mailManagement = new MailManagement();
        String subject = "Test Subject";
        String message = "Test Message";
        String[] toEmails = {"2020csb10666@iitrpr.ac.in"};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        mailManagement.sendMail(subject, message, toEmails);
        String output = outputStream.toString();
        assert (output.contains("Mail sent successfully!"));
    }

    @Test
    public void testSendMailWithInvalidRecipient() {
        MailManagement mailManagement = new MailManagement();
        String subject = "Test Subject";
        String message = "Test Message";
        String[] toEmails = {"invalid-email"};
        assertThrows(RuntimeException.class, () -> mailManagement.sendMail(subject, message, toEmails));
    }
}

