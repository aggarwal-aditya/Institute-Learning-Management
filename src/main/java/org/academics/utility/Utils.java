package org.academics.utility;


import dnl.utils.text.table.TextTable;
import org.academics.dao.JDBCPostgreSQLConnection;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    static JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    static Connection conn = jdbc.getConnection();

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
        int otp = (int) (Math.random() * 900000) + 100000;
        return otp;
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
     */
    public static String getCurrentSession() {
        // Get the current date
        LocalDate currentDate = CurrentDate.getInstance().getCurrentDate();

        try {
            // Prepare the SQL statement to retrieve the current session
            PreparedStatement statement = conn.prepareStatement("SELECT year, semester_number FROM semester WHERE start_date <= ? AND end_date >= ?");
            statement.setDate(1, Date.valueOf(currentDate));
            statement.setDate(2, Date.valueOf(currentDate));

            // Execute the query and get the result set
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // If a result is found, return the session in the format "YYYY-SEM"
                return resultSet.getString(1) + "-" + resultSet.getString(2);
            } else {
                // If no result is found, return null
                return null;
            }
        } catch (SQLException e) {
            // Log the exception and return null
            e.printStackTrace();
            System.out.println("Something went wrong!");
            return null;
        }
    }


    public static boolean validateEventTime(String eventType, String session) {
        LocalDate currentDate = CurrentDate.getInstance().getCurrentDate();
        try {
            int year = Integer.parseInt(session.substring(0, 4));
            int semester = Integer.parseInt(session.substring(5));
            eventType = " " + eventType;
            PreparedStatement statement = conn.prepareStatement("SELECT" + eventType + "_start_date, " + eventType + "_end_date FROM semester WHERE year = ? AND semester_number = ?");
            statement.setInt(1, year);
            statement.setInt(2, semester);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (currentDate.isAfter(resultSet.getDate(1).toLocalDate()) && currentDate.isBefore(resultSet.getDate(2).toLocalDate())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        }
        return false;
    }

    public static void exportTxt(ResultSet resultSet, String fileName, String message) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String username = System.getProperty("user.name");
            String downloadPath = "";
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
            printTable(resultSet, new String[]{"Course Code", "Course Name", "Semester", "Grade"}, message);
            System.setIn(System.in);
            System.setOut(System.out);
            System.out.println("File downloaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        } finally {
            System.setIn(System.in);
            System.setOut(System.out);
        }
    }

    public static void exportCSV(ResultSet resultSet, String fileName, String[] p_extraColumnHeaders) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String username = System.getProperty("user.name");
            String downloadPath = "";
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
            e.printStackTrace();
        }

    }

    public static void printTable(ResultSet resultSet, String[] columnNames, String message) throws SQLException {
        resultSet.beforeFirst();
        //Check if there are no entries in result set
        if (!resultSet.next()) {
            return;
        }
        Object[][] data = getData(resultSet, columnNames.length);
        TextTable courseTable = new TextTable(columnNames, data);
        System.out.println(message);
        courseTable.printTable();
    }

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
