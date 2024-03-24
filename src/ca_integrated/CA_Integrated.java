/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ca_integrated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author marktimestwo
 */
public class CA_Integrated {

      
    private static final String URL = "jdbc:mysql://localhost:3306/Integrated_CA";
    private static final String USER = "root";
    private static final String PASSWORD = "@Markcodestoo24";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    
    
    
    public static void main(String[] args) {
        
        
    }
    
}
