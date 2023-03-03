package org.academics.utility;


import dnl.utils.text.table.TextTable;
import org.academics.dal.dbUtils;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This class is used to provide utility functions.
 */
public class Utils {

    private Utils() {
        //Private constructor to hide the implicit public one
    }

    /**
     * Generates a one-time password (OTP) that is a 6-digit integer between 100000 and 999999.
     *
     * @return The generated OTP.
     */
    public static int generateOTP() {
        // Math.random() generates a random double between 0.0 and 1.0.
        // We multiply it by 900000 to get a random double between 0.0 and 900000.0.
        // We add 100000 to get a random double between 100000.0 and 1000000.0.
        // Finally, we cast it to an int to get a 6-digit integer between 100000 and 999999.
        return (int) (Math.random() * 900000) + 100000;
    }


    /**
     * Prompts the user to enter a valid integer choice between 1 and maxChoice (inclusive).
     *
     * @param maxChoice The maximum valid choice.
     * @return The user's valid integer choice.
     */
    public static int getUserChoice(int maxChoice) {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid choice");
            System.out.println("Enter your choice:");
            scanner.next();
        }
        int choice = scanner.nextInt();
        while (choice < 1 || choice > maxChoice) {
            System.out.println("Invalid choice");
            System.out.println("Enter your choice:");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid choice");
                System.out.println("Enter your choice:");
                scanner.next();
            }
            choice = scanner.nextInt();
        }
        return choice;
    }


    /**
     * Prompts the user to enter a non-empty string input and returns it.
     *
     * @param message the prompt message to display to the user
     * @return the non-empty string input entered by the user
     */
    public static String getInput(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message + " ");
        String input = scanner.nextLine().trim(); // Read the input as a full line and trim any leading/trailing whitespace
        while (input.isEmpty()) { // Check if the input is empty
            System.out.print("Invalid input. " + message + " "); // Print an error message
            input = scanner.nextLine().trim(); // Read the input again
        }
        return input;
    }


    /**
     * Returns the current academic session, based on the current date.
     *
     * @return the current academic session in the format "YYYY-SEM", or null if no session is found.
     * @throws SQLException if an error occurs while accessing the database
     */
    public static String getCurrentSession() throws SQLException {
        // Get the current date
        LocalDate currentDate = CurrentDate.getInstance().getCurrentDate();
        ResultSet resultSet = dbUtils.getCurrentSession(currentDate);
        if (resultSet.next()) {
            // If a result is found, return the session in the format "YYYY-SEM"
            return resultSet.getString(1) + "-" + resultSet.getString(2);
        } else {
            // If no result is found, return null
            return null;
        }
    }


    /**
     * Validates if the current date falls within the start and end date of an event of a given event type and session.
     *
     * @param eventType the type of the event to validate
     * @param session   the academic session in which the event is taking place
     * @return true if the current date is within the event start and end date, false otherwise
     * @throws SQLException if an error occurs while accessing the database
     */
    public static boolean validateEventTime(String eventType, String session) throws SQLException {
        LocalDate currentDate = CurrentDate.getInstance().getCurrentDate();
        int year = Integer.parseInt(session.substring(0, 4));
        int semester = Integer.parseInt(session.substring(5));
        eventType = " " + eventType;
        return dbUtils.validateEventTime(eventType, year, semester, currentDate);
    }


    /**
     * Exports the result set to a text file with the given filename and a custom message.
     * The text file will contain a table with the columns "Course Code", "Course Name", "Semester", and "Grade".
     * The file will be downloaded to the default Downloads folder of the current user on the operating system.
     * If the operating system is not recognized, the user will be prompted to enter the path to download the file.
     *
     * @param resultSet the result set to be exported
     * @param fileName  the name of the file to be downloaded
     * @param message   the message to be included in the file
     */
    public static void exportTxt(ResultSet resultSet, String fileName, String message) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String username = System.getProperty("user.name");
            String downloadPath ;
            if (os.contains("win")) {
                downloadPath = "C:\\Users\\" + username + "\\Downloads\\" + fileName + ".txt";
            } else if (os.contains("mac")) {
                downloadPath = "/Users/" + username + "/Downloads/" + fileName + ".txt";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                downloadPath = "/home/" + username + "/Downloads/" + fileName + ".txt";
            } else {
                System.out.println("Enter the path to download the file:");
                downloadPath = new Scanner(System.in).nextLine();
            }
            System.out.println("Downloading file to " + downloadPath);
            OutputStream outputStream = new FileOutputStream(downloadPath);
            System.setOut(new PrintStream(outputStream));
            printTable(resultSet, new String[]{"Course Code", "Course Name", "Semester", "Grade"}, message, "");
            System.setIn(System.in);
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            System.out.println("File downloaded successfully!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.setIn(System.in);
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        }
    }


    /**
     * Exports the contents of a ResultSet object to a CSV file.
     *
     * @param resultSet            the ResultSet object to be exported
     * @param fileName             the name of the CSV file to be created
     * @param p_extraColumnHeaders an array of additional column headers to include in the CSV file
     */
    public static void exportCSV(ResultSet resultSet, String fileName, String[] p_extraColumnHeaders) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String username = System.getProperty("user.name");
            String downloadPath;
            if (os.contains("win")) {
                downloadPath = "C:\\Users\\" + username + "\\Downloads\\" + fileName + ".csv";
            } else if (os.contains("mac")) {
                downloadPath = "/Users/" + username + "/Downloads/" + fileName + ".csv";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                downloadPath = "/home/" + username + "/Downloads/" + fileName + ".csv";
            } else {
                System.out.println("Enter the path to download the file:");
                downloadPath = new Scanner(System.in).nextLine();
            }
            System.out.println("Downloading file to " + downloadPath);
            java.io.FileWriter fw = new java.io.FileWriter(downloadPath);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            java.io.PrintWriter pw = new java.io.PrintWriter(bw);
            java.sql.ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) pw.print(",");
                String columnValue = rsmd.getColumnName(i);
                pw.print(columnValue);
            }
            for (String extraHeader : p_extraColumnHeaders) {
                pw.print(",");
                pw.print(extraHeader);
            }
            pw.println();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) pw.print(",");
                    String columnValue = resultSet.getString(i);
                    pw.print(columnValue);
                }
                pw.println();
            }
            pw.flush();
            pw.close();
            bw.close();
            fw.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }


    /**
     * Prints a table of results fetched from a ResultSet object.
     *
     * @param resultSet      the ResultSet object containing the results to print
     * @param columnNames    an array of column names to be used as table headers
     * @param successMessage the message to print if the results are successfully fetched
     * @param failureMessage the message to print if the ResultSet object is empty
     * @throws SQLException if a database access error occurs
     */
    public static void printTable(ResultSet resultSet, String[] columnNames, String successMessage, String failureMessage) throws SQLException {
        resultSet.beforeFirst();
        if (!resultSet.next()) {
            System.out.println(failureMessage);
            return;
        }
        Object[][] data = getData(resultSet, columnNames.length);
        TextTable courseTable = new TextTable(columnNames, data);
        System.out.println(successMessage);
        courseTable.printTable();
    }


    /**
     * Converts the result set into a 2D array of objects.
     *
     * @param resultSet  the result set to be converted
     * @param numColumns the number of columns in the result set
     * @return a 2D array of objects representing the data in the result set
     * @throws SQLException if there is an error accessing the result set
     */
    private static Object[][] getData(ResultSet resultSet, int numColumns) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            Object[] rowData = new Object[numColumns];
            for (int i = 1; i <= numColumns; i++) {
                rowData[i - 1] = resultSet.getString(i);
            }
            data.add(rowData);
        }
        return data.toArray(new Object[0][]);
    }


}
