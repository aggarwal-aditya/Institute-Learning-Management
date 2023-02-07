package org.academics;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

public class  Utils {
    static JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    static Connection conn = jdbc.getConnection();
    public static int generateOTP() {
        //generate a random 6-digit number
        return (int)(Math.random() * 900000) + 100000;
    }

    public static String getCurrentSession(){
        Date currentDate = CurrentDate.getInstance().getCurrentDate();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT year, semester_number FROM semester WHERE start_date <= ? AND end_date >= ?");
            statement.setDate(1, new java.sql.Date(currentDate.getTime()));
            statement.setDate(2, new java.sql.Date(currentDate.getTime()));
            java.sql.ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1) + "-" + resultSet.getString(2);
            }
            else {
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Oye! Something went wrong!");
        }
        return null;
    }

    // read csv files given the path
    public static Object readCSV(String path) {
        //TODO
        return null;
    }
}
