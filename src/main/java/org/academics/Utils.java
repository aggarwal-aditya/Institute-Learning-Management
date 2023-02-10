package org.academics;


import org.academics.dao.JDBCPostgreSQLConnection;

import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static void exportCSV(ResultSet resultSet){
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("file.csv"));
            int result = fileChooser.showSaveDialog(null);
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\file.csv"));
                // Write the data
                while (resultSet.next()) {
                    writer.write(resultSet.getString(1) + "," + resultSet.getString(2));
                    writer.newLine();
                }
                // Close the writer
                writer.close();

                System.out.println("Data exported successfully to ");
            resultSet.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // read csv files given the path
//    public static Object readCSV(String path) {
//        Scann
//    }
}
