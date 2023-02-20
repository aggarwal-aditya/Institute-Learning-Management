package org.academics.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MailManagementTest {

    @Test
    public void testSendMail() {
        MailManagement mailManagement = new MailManagement();
        String subject = "Test Subject";
        String message = "Test Message";
        String[] toEmails = {"2020csb1066@iitrpr.ac.in"};
        mailManagement.sendMail(subject, message, toEmails);
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
