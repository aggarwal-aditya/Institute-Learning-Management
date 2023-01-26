package org.academics;

public class utils {
    public static int generateOTP() {
        //generate a random 6-digit number
        return (int)(Math.random() * 900000) + 100000;
    }
}
