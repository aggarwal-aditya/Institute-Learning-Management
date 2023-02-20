package org.academics.utility;


import dnl.utils.text.table.TextTable;
import org.academics.dao.JDBCPostgreSQLConnection;

import java.io.FileOutputStream;
import java.io.FileWriter;
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
    static Scanner scanner = new Scanner(System.in);

    private Utils() {
        //Private constructor to hide the implicit public one
    }

    public static int generateOTP() {
        //generate a random 6-digit number
        return (int) (Math.random() * 900000) + 100000;
    }

    public static int getUserChoice(int maxChoice){
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

    public static String getInput(String message) {
        System.out.println(message);
        return scanner.next();
    }

    public static String getCurrentSession() {
        LocalDate currentDate = CurrentDate.getInstance().getCurrentDate();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT year, semester_number FROM semester WHERE start_date <= ? AND end_date >= ?");
            statement.setDate(1, Date.valueOf(currentDate));
            statement.setDate(2, Date.valueOf(currentDate));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1) + "-" + resultSet.getString(2);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        }
        return null;
    }
    public static boolean validateEventTime(String query,String session){
        LocalDate currentDate = CurrentDate.getInstance().getCurrentDate();
        try{
            int year = Integer.parseInt(session.substring(0,4));
            int semester = Integer.parseInt(session.substring(5));
            query=" "+query;
            PreparedStatement statement = conn.prepareStatement("SELECT"+query+"_start_date, "+query+"_end_date FROM semester WHERE year = ? AND semester_number = ?");
            statement.setInt(1, year);
            statement.setInt(2, semester);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                if (currentDate.isAfter(resultSet.getDate(1).toLocalDate()) && currentDate.isBefore(resultSet.getDate(2).toLocalDate())){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        }
        return false;
    }

    public static void exportTxt(ResultSet resultSet,String fileName,String message){
        try{
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
            printTable(resultSet,new String[]{"Course Code","Course Name", "Semester","Grade"},message);
            System.setIn(System.in);
            System.setOut(System.out);
            System.out.println("File downloaded successfully!");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        }finally {
            System.setIn(System.in);
            System.setOut(System.out);
        }
    }
    public static void exportCSV(ResultSet resultSet, String fileName, String [] p_extraColumnHeaders) {
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
            for(String extraHeader : p_extraColumnHeaders){
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
