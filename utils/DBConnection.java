/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Max
 */
public class DBConnection {
    
    // JDBC URL parts
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/U05zNa";
    
    // JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    
    // Driver & Connection interface reference
    private static final String mysqljdbcDriver = "com.mysql.jdbc.Driver";
    static Connection conn;
    
    private static final String username = "U05zNa";
    private static final String password = "53688654091";
    
    public static Connection startConnection()
    {
        try{
            Class.forName(mysqljdbcDriver);
            conn = (Connection)DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection successful.");
        }
        catch(ClassNotFoundException e) 
        {
            System.out.println("Connection Error: " + e.getMessage());
        }
        catch(SQLException e) 
        {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return conn;
    }
    
    public static void closeConnection()
    {
        try{
            conn.close();
            System.out.println("Connection closed.");
        }
        catch(SQLException e) 
        {
            System.out.println("Disconnect Error: " + e.getMessage());
        }
    }
}
