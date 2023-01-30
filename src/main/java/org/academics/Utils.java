package org.academics;

import java.security.*;
public class Utils {
    public static int generateOTP() {
        //generate a random 6-digit number
        return (int)(Math.random() * 900000) + 100000;
    }
    // read csv files given the path
    public static Object readCSV(String path) {
        //TODO
        return null;
    }


}
