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

        public void generateStudentReport() {
        String studentQuery = "SELECT "
                + "s.Student_ID, "
                + "s.Name AS Student_Name, "
                + "p.Programme_Name, "
                + "GROUP_CONCAT(DISTINCT CASE WHEN e.Status = 'Enrolled' THEN m.Module_Name END) AS Current_Modules, "
                + "GROUP_CONCAT(DISTINCT CASE WHEN e.Status = 'Completed' THEN CONCAT(m.Module_Name, ': Grade ', g.Grade) END) AS Completed_Modules_With_Grades, "
                + "GROUP_CONCAT(DISTINCT CASE WHEN e.Status = 'To Repeat' THEN m.Module_Name END) AS Modules_To_Repeat "
                + "FROM Students s "
                + "JOIN Programmes p ON s.Programme_ID = p.Programme_ID "
                + "LEFT JOIN Enrollments e ON s.Student_ID = e.Student_ID "
                + "LEFT JOIN Modules m ON e.Module_ID = m.Module_ID "
                + "LEFT JOIN Grades g ON e.Enrollment_ID = g.Enrollment_ID "
                + "GROUP BY s.Student_ID";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(studentQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println(String.format("Student Name: %s, Student ID: %s, Programme: %s, Current Modules: %s, Completed Modules: %s, Modules to Repeat: %s",
                    resultSet.getString("Student_Name"),
                    resultSet.getString("Student_ID"),
                    resultSet.getString("Programme_Name"),
                    resultSet.getString("Current_Modules"),
                    resultSet.getString("Completed_Modules_With_Grades"),
                    resultSet.getString("Modules_To_Repeat")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
            public void generateLecturerReport() {
        String lecturerQuery = "SELECT l.Name, l.Role, GROUP_CONCAT(DISTINCT m.Module_Name ORDER BY m.Module_Name SEPARATOR ', ') AS Teaching_Modules, "
                + "COUNT(DISTINCT e.Student_ID) AS Enrolled_Students, l.Specialties "
                + "FROM Lecturers l "
                + "JOIN Module_Lecturers ml ON l.Lecturer_ID = ml.Lecturer_ID "
                + "JOIN Modules m ON ml.Module_ID = m.Module_ID "
                + "LEFT JOIN Enrollments e ON m.Module_ID = e.Module_ID "
                + "GROUP BY l.Lecturer_ID";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(lecturerQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println(String.format("Lecturer: %s, Role: %s, Teaching Modules: %s, Enrolled Students: %d, Specialties: %s",
                    resultSet.getString("Name"),
                    resultSet.getString("Role"),
                    resultSet.getString("Teaching_Modules"),
                    resultSet.getInt("Enrolled_Students"),
                    resultSet.getString("Specialties")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
          
    }
    
}
