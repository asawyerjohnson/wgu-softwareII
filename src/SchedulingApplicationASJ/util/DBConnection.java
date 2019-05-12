/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ.util;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ashley.johnson
 */
public class DBConnection {

    /*
    Connection String
    Server name:  52.206.157.109 
    Database name:  U04Nnp
    Username:  U04Nnp
    Password:  53688289641
    */
    private static final String DBNAME = "U04Nnp" ;
    private static final String DBURL = "jdbc:mysql://52.206.157.109/" + DBNAME;
    private static final String DBUSER = "U04Nnp";
    private static final String DBPASS = "53688289641";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    static Connection conn;
    
    public static void makeConnection() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(DRIVER);
        conn = (Connection) DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        System.out.println("Connection Successful!");
    }
    
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception {
        conn.close();
        System.out.println("Connection Closed!");
    }  
    
    public static java.sql.Connection getConnection() {
        return conn;
    }
}
