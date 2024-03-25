/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ca_integrated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    
        public void generateCourseReport() {
        String courseQuery = "SELECT m.Module_Name, p.Programme_Name, COUNT(e.Student_ID) AS Enrolled_Students, "
                + "l.Name AS Lecturer_Name, r.Room_Name FROM Modules m JOIN Programmes p ON m.Programme_ID = p.Programme_ID "
                + "LEFT JOIN Enrollments e ON m.Module_ID = e.Module_ID AND e.Status = 'Enrolled' JOIN Module_Lecturers ml ON m.Module_ID = ml.Module_ID "
                + "JOIN Lecturers l ON ml.Lecturer_ID = l.Lecturer_ID LEFT JOIN Rooms r ON m.Room_ID = r.Room_ID GROUP BY m.Module_ID, l.Name";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(courseQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println(String.format("Module: %s, Programme: %s, Enrolled Students: %d, Lecturer: %s, Room: %s",
                    resultSet.getString("Module_Name"),
                    resultSet.getString("Programme_Name"),
                    resultSet.getInt("Enrolled_Students"),
                    resultSet.getString("Lecturer_Name"),
                    resultSet.getString("Room_Name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
    
    public static void main(String[] args) {
          
    }
    
}
