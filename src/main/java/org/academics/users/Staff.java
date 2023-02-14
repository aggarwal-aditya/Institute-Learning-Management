package org.academics.users;

import org.academics.dao.JDBCPostgreSQLConnection;

import java.sql.Connection;
import java.util.Scanner;

public class Staff extends User{
    Scanner scanner = new Scanner(System.in);
    JDBCPostgreSQLConnection jdbc = JDBCPostgreSQLConnection.getInstance();
    Connection conn = jdbc.getConnection();

    public Staff(User user) {
        super(user.userRole, user.email_id);
    }
    public void updateCourseCatalog(){

    }
    public void viewStudentGrades(){

    }

    public void generateTranscript(){

    }

    public void checkGraduationStatus(){
        System.out.println("Enter the student's enrollment id:");
        String enrollment_id = scanner.next();

    }

}
