package org.academics.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {



    @Test
    void generateOTP() {
        int otp = Utils.generateOTP();
        assertTrue(otp >= 100000 && otp <= 999999, "OTP is not a 6-digit number");
    }

    @Test
    void testValidInput() {
        String input = "2\n"; // Set the input to be the number 2 followed by a newline
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        int choice = Utils.getUserChoice(3);
        assertEquals(2, choice);
    }
    @Test
    void testOutOfRangeInput() {
        String input = "10\nqwerty\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        int choice = Utils.getUserChoice(5);
        assertEquals(3, choice);
    }

    @Test
    void testInvalidInput() {
        String input = "qwerty\n3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        int choice = Utils.getUserChoice(5);
        assertEquals(3, choice);
    }


    @Test
    void getCurrentSession() {
    }

    @Test
    void validateEventTime() {
    }

    @Test
    void exportCSV() {

    }
}