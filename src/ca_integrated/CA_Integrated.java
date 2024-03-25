/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ca_integrated;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author marktimestwo
 */
public class CA_Integrated {
    // Database connection to establish and return data from MySQL  
    private static final String URL = "jdbc:mysql://localhost:3306/Integrated_CA";
    private static final String USER = "root";
    private static final String PASSWORD = "@Markcodestoo24";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Method to generate and print out course report details
    public void generateCourseReport() { // SQL query that joins several tables to gather information about the course
        String courseQuery = "SELECT m.Module_Name, p.Programme_Name, COUNT(e.Student_ID) AS Enrolled_Students, "
                + "l.Name AS Lecturer_Name, r.Room_Name FROM Modules m JOIN Programmes p ON m.Programme_ID = p.Programme_ID "
                + "LEFT JOIN Enrollments e ON m.Module_ID = e.Module_ID AND e.Status = 'Enrolled' JOIN Module_Lecturers ml ON m.Module_ID = ml.Module_ID "
                + "JOIN Lecturers l ON ml.Lecturer_ID = l.Lecturer_ID LEFT JOIN Rooms r ON m.Room_ID = r.Room_ID GROUP BY m.Module_ID, l.Name";
        try (Connection connection = getConnection(); // Try resource to ensure that the resources are closed after
             PreparedStatement statement = connection.prepareStatement(courseQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) { // This iterates over each row and formats the report into a readable string
                System.out.println(String.format("Module: %s, Programme: %s, Enrolled Students: %d, Lecturer: %s, Room: %s",
                    resultSet.getString("Module_Name"),
                    resultSet.getString("Programme_Name"),
                    resultSet.getInt("Enrolled_Students"),
                    resultSet.getString("Lecturer_Name"),
                    resultSet.getString("Room_Name")));
            }
        } catch (SQLException e) { // Catch any exception that may occur
            e.printStackTrace();
        }
    }
    // Similar concept as the method above
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
    
    // This defines an interface and a class for generating various reports and declares three methods
    public interface ReportGenerator {
    void generateCourseReport(ResultSet resultSet) throws SQLException;
    void generateStudentReport(ResultSet resultSet) throws SQLException;
    void generateLecturerReport(ResultSet resultSet) throws SQLException;
}

    public class ConsoleReportGenerator implements ReportGenerator {
    
    @Override
    public void generateCourseReport(ResultSet resultSet) throws SQLException {
        System.out.println("Course Report:");
        while (resultSet.next()) {
            System.out.println(String.format("Module: %s, Programme: %s, Enrolled Students: %d, Lecturer: %s, Room: %s",
                resultSet.getString("Module_Name"),
                resultSet.getString("Programme_Name"),
                resultSet.getInt("Enrolled_Students"),
                resultSet.getString("Lecturer_Name"),
                resultSet.getString("Room_Name")));
        }
    }

    @Override
    public void generateStudentReport(ResultSet resultSet) throws SQLException {
        System.out.println("\nStudent Report:");
        while (resultSet.next()) {
            System.out.println(String.format("Student Name: %s, Student ID: %s, Programme: %s, Current Modules: %s, Completed Modules: %s, Modules to Repeat: %s",
                resultSet.getString("Student_Name"),
                resultSet.getString("Student_ID"),
                resultSet.getString("Programme_Name"),
                resultSet.getString("Current_Modules"),
                resultSet.getString("Completed_Modules_With_Grades"),
                resultSet.getString("Modules_To_Repeat")));
        }
    }

    @Override
    public void generateLecturerReport(ResultSet resultSet) throws SQLException {
        System.out.println("\nLecturer Report:");
        while (resultSet.next()) {
            System.out.println(String.format("Lecturer: %s, Role: %s, Teaching Modules: %s, Enrolled Students: %d, Specialties: %s",
                resultSet.getString("Name"),
                resultSet.getString("Role"),
                resultSet.getString("Teaching_Modules"),
                resultSet.getInt("Enrolled_Students"),
                resultSet.getString("Specialties")));
        }
    }
}       
       
    // This class implements the ReportGenerator interface to provide functionality for generating reports in text format and writing them to a .txt file
    public class TxtReportGenerator implements ReportGenerator {

    private void writeToTxtFile(String fileName, String content) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println(content);
        }
    }

    @Override
    public void generateCourseReport(ResultSet resultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("Course Report:\n");
        while (resultSet.next()) {
            sb.append(String.format("Module: %s, Programme: %s, Enrolled Students: %d, Lecturer: %s, Room: %s\n",
                    resultSet.getString("Module_Name"),
                    resultSet.getString("Programme_Name"),
                    resultSet.getInt("Enrolled_Students"),
                    resultSet.getString("Lecturer_Name"),
                    resultSet.getString("Room_Name")));
        }
        try {
            writeToTxtFile("CourseReport.txt", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateStudentReport(ResultSet resultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("\nStudent Report:\n");
        while (resultSet.next()) {
            sb.append(String.format("Student Name: %s, Student ID: %s, Programme: %s, Current Modules: %s, Completed Modules: %s, Modules to Repeat: %s\n",
                    resultSet.getString("Student_Name"),
                    resultSet.getString("Student_ID"),
                    resultSet.getString("Programme_Name"),
                    resultSet.getString("Current_Modules"),
                    resultSet.getString("Completed_Modules_With_Grades"),
                    resultSet.getString("Modules_To_Repeat")));
        }
        try {
            writeToTxtFile("StudentReport.txt", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateLecturerReport(ResultSet resultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("\nLecturer Report:\n");
        while (resultSet.next()) {
            sb.append(String.format("Lecturer: %s, Role: %s, Teaching Modules: %s, Enrolled Students: %d, Specialties: %s\n",
                    resultSet.getString("Name"),
                    resultSet.getString("Role"),
                    resultSet.getString("Teaching_Modules"),
                    resultSet.getInt("Enrolled_Students"),
                    resultSet.getString("Specialties")));
        }
        try {
            writeToTxtFile("LecturerReport.txt", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}       
            
    public class CsvReportGenerator implements ReportGenerator {

    private void writeToCsvFile(String fileName, String[] headers, ResultSet resultSet) throws IOException, SQLException {
        FileWriter csvWriter = new FileWriter(fileName, true);

        // Write the header
        for (int i = 0; i < headers.length; i++) {
            csvWriter.append(headers[i]);
            if (i < headers.length - 1) {
                csvWriter.append(",");
            }
        }
        csvWriter.append("\n");

        // Write the data
        while (resultSet.next()) {
            for (int i = 0; i < headers.length; i++) {
                csvWriter.append(resultSet.getString(i + 1)); // Assuming ResultSet columns align with headers
                if (i < headers.length - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    @Override
    public void generateCourseReport(ResultSet resultSet) throws SQLException {
        String[] headers = {"Module Name", "Programme Name", "Enrolled Students", "Lecturer Name", "Room Name"};
        try {
            writeToCsvFile("CourseReport.csv", headers, resultSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateStudentReport(ResultSet resultSet) throws SQLException {
        String[] headers = {"Student Name", "Student ID", "Programme", "Current Modules", "Completed Modules", "Modules to Repeat"};
        try {
            writeToCsvFile("StudentReport.csv", headers, resultSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateLecturerReport(ResultSet resultSet) throws SQLException {
        String[] headers = {"Lecturer Name", "Role", "Teaching Modules", "Enrolled Students", "Specialties"};
        try {
            writeToCsvFile("LecturerReport.csv", headers, resultSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    
    public ReportGenerator getReportGenerator(String outputType) {
        switch (outputType.toLowerCase()) {
            case "console":
                return new ConsoleReportGenerator();
            case "txt":
                return new TxtReportGenerator();
            case "csv":
                return new CsvReportGenerator();
            default:
                throw new IllegalArgumentException("Unsupported output type: " + outputType);
        }
    }

    
    private void generateReportToFile(String reportContent, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(reportContent);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the TXT file.");
            e.printStackTrace();
        }
    }

    public void generateCourseReportToTXT() {
        StringBuilder reportBuilder = new StringBuilder();
        String courseQuery = "SELECT m.Module_Name, p.Programme_Name, COUNT(e.Student_ID) AS Enrolled_Students, "
                + "l.Name AS Lecturer_Name, r.Room_Name FROM Modules m JOIN Programmes p ON m.Programme_ID = p.Programme_ID "
                + "LEFT JOIN Enrollments e ON m.Module_ID = e.Module_ID AND e.Status = 'Enrolled' JOIN Module_Lecturers ml ON m.Module_ID = ml.Module_ID "
                + "JOIN Lecturers l ON ml.Lecturer_ID = l.Lecturer_ID LEFT JOIN Rooms r ON m.Room_ID = r.Room_ID GROUP BY m.Module_ID, l.Name";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(courseQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reportBuilder.append(String.format("Module: %s, Programme: %s, Enrolled Students: %d, Lecturer: %s, Room: %s%n",
                        resultSet.getString("Module_Name"),
                        resultSet.getString("Programme_Name"),
                        resultSet.getInt("Enrolled_Students"),
                        resultSet.getString("Lecturer_Name"),
                        resultSet.getString("Room_Name")));
            }
            generateReportToFile(reportBuilder.toString(), "CourseReport.txt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    private void generateCourseReportToCSV() {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Module Name,Programme Name,Enrolled Students,Lecturer Name,Room Name\n");
    
        String courseQuery = "SELECT m.Module_Name, p.Programme_Name, COUNT(e.Student_ID) AS Enrolled_Students, "
                + "l.Name AS Lecturer_Name, r.Room_Name FROM Modules m JOIN Programmes p ON m.Programme_ID = p.Programme_ID "
                + "LEFT JOIN Enrollments e ON m.Module_ID = e.Module_ID AND e.Status = 'Enrolled' JOIN Module_Lecturers ml ON m.Module_ID = ml.Module_ID "
                + "JOIN Lecturers l ON ml.Lecturer_ID = l.Lecturer_ID LEFT JOIN Rooms r ON m.Room_ID = r.Room_ID GROUP BY m.Module_ID, l.Name";
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(courseQuery);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                csvBuilder.append(String.format("\"%s\",\"%s\",%d,\"%s\",\"%s\"\n",
                    resultSet.getString("Module_Name"),
                    resultSet.getString("Programme_Name"),
                    resultSet.getInt("Enrolled_Students"),
                    resultSet.getString("Lecturer_Name"),
                    resultSet.getString("Room_Name")));
            }
            generateReportToFile(csvBuilder.toString(), "CourseReport.csv");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
            
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CA_Integrated app = new CA_Integrated();

        // Login
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (!username.equals("admin") || !password.equals("java")) {
            System.out.println("Incorrect username or password.");
            return; // Exit if the credentials are incorrect
        }

        // Main menu
        System.out.println("Login successful! Choose your role:");
        System.out.println("1. Admin");
        System.out.println("2. Office");
        System.out.println("3. Lecturer");
        System.out.print("Enter your choice (1-3): ");

        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        switch (roleChoice) {
            case 1: // Admin
                System.out.println("Admin Menu:");
                System.out.println("1. Add, Modify, Delete Users");
                System.out.println("2. Change Login Details");
                
                int adminoption = scanner.nextInt();
                switch (adminoption) {
                    case 1:
                        System.out.println("Please select:");
                        System.out.println("1. Add User");
                        System.out.println("2. Modify User");
                        System.out.println("3. Delete User");
                        
                        int adminselects = scanner.nextInt();
                        switch (adminselects) {
                            case 1: 
                               System.out.println("Enter User details:");
                               scanner.nextLine();
                               String userDetails = scanner.nextLine();
                               System.out.println("New user added: " + userDetails);
                               break;
                            case 2:
                               System.out.println("Choose User to modify:");
                               scanner.nextLine();
                               String userModify = scanner.nextLine();
                               System.out.println("User modified successfully: " + userModify);
                               break;
                            case 3:
                               System.out.println("Choose User to delete:");
                               scanner.nextLine();
                               String userDelete = scanner.nextLine();
                               System.out.println("User deleted successfully: " + userDelete);
                        }
                        break;
                    case 2:
                        System.out.println("Login details changed sucessfully");
                        break;
                } 
                break;
                
            case 2: // Office
                System.out.println("Office Menu:");
                System.out.println("Select the output format for the reports:");
                System.out.println("1. TXT file");
                System.out.println("2. CSV file");
                System.out.println("3. Console");
                System.out.println("4. Change Login Details");
                System.out.print("Enter your choice (1-4): ");

                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("Generating reports in TXT format");
                        app.generateCourseReportToTXT(); 
                        System.out.println("Successful! TXT File generated");
                        break;
                    case 2:
                        System.out.println("Generating reports in CSV format");
                        app.generateCourseReportToCSV();
                        System.out.println("Successful! CSV File generated");
                        break;
                    case 3:
                        app.generateCourseReport();
                        app.generateStudentReport();
                        app.generateLecturerReport();
                        break;
                    case 4:
                        System.out.println("Login details changed sucessfully!");
                    default:
                        break;
                    }
                break;
            case 3: // Lecturer
                System.out.println("Lecturer Menu:");
                System.out.println("Please select:");
                System.out.println("1. Lecturer Report");
                System.out.println("2. Change Login Details");
                
                int lectureoption = scanner.nextInt();
                switch (lectureoption) {
                    case 1:
                       System.out.println("Select output format for the report:");
                       System.out.println("1. TXT file");
                       System.out.println("2. CSV file");
                       System.out.println("3. Console"); 
                        
                        int lecturereportchoice = scanner.nextInt();
                        switch (lecturereportchoice) {
                            case 1:
                                System.out.println("Generating lecture report in TXT format");
                                app.generateCourseReportToTXT(); 
                                System.out.println("Successful! TXT file report generated");
                            break;
                        case 2:
                                System.out.println("Generating lecturer report in CSV format");
                                app.generateCourseReportToCSV();
                                System.out.println("Successful! CSV file report generated");
                            break;
                        case 3:
                                app.generateLecturerReport();
                            break;
                        }
                break;
                    case 2:
                       System.out.println("Login details changed sucessfully"); 
                    default:
                break;
            }
            scanner.close();
        }  
    } 
}
