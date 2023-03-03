package org.academics;

import java.sql.Connection;
import java.util.Scanner;
import org.academics.dal.*;
import org.academics.menus.mainMenu;


class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
        Connection conn = jdbc.getConnection();
        if (conn == null) {
            System.out.println("Connection failed. Please try again later.");
            return;
        }
        mainMenu.mainMenu();
    }

}