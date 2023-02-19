package org.academics.utility;


import org.academics.CurrentDate;
import org.academics.dao.JDBCPostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Scanner;

public class Utils {
    static JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    static Connection conn = jdbc.getConnection();

    private Utils() {
        //Private constructor to hide the implicit public one
    }

    public static int generateOTP() {
        //generate a random 6-digit number
        return (int) (Math.random() * 900000) + 100000;
    }

    public static String getCurrentSession() {
        Date currentDate = CurrentDate.getInstance().getCurrentDate();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT year, semester_number FROM semester WHERE start_date <= ? AND end_date >= ?");
            statement.setDate(1, new java.sql.Date(currentDate.getTime()));
            statement.setDate(2, new java.sql.Date(currentDate.getTime()));
            java.sql.ResultSet resultSet = statement.executeQuery();
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
    public static boolean verifyEventTime(String query){
        Date currentDate = CurrentDate.getInstance().getCurrentDate();
        try{
            String session = getCurrentSession();
            assert session != null;
            int year = Integer.parseInt(session.substring(0,4));
            int semester = Integer.parseInt(session.substring(5));
            PreparedStatement statement = conn.prepareStatement("SELECT"+query+"_start_date, "+query+"_end_date FROM semester WHERE year = ? AND semester_number = ?");
            statement.setInt(1, year);
            statement.setInt(2, semester);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                if (currentDate.after(resultSet.getDate(1)) && currentDate.before(resultSet.getDate(2))){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        }
        return false;
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

}
