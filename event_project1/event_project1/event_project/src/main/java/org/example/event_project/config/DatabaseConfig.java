package org.example.event_project.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/project_event?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        System.out.println("Attempting to connect to database...");
        System.out.println("URL: " + URL);
        System.out.println("Username: " + USERNAME);
        
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Try to connect to the database
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Successfully connected to database");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Please make sure:");
            System.err.println("1. XAMPP is running and MySQL service is started");
            System.err.println("2. The database 'project_event' exists");
            System.err.println("3. The user 'root' has no password");
            throw e;
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Database connection test successful!");
                System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 