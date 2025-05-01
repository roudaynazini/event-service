package com.roudaynazini.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "connectart1";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        System.out.println("Attempting to connect to database...");
        System.out.println("URL: " + URL + DATABASE_NAME);
        System.out.println("Username: " + USERNAME);
        
        try {
            // First try to connect to the specific database
            Connection conn = DriverManager.getConnection(URL + DATABASE_NAME, USERNAME, PASSWORD);
            System.out.println("Successfully connected to database: " + DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            System.out.println("Could not connect to database, attempting to create it...");
            // If database doesn't exist, connect to MySQL server and create it
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
                System.out.println("Database " + DATABASE_NAME + " created successfully");
            }
            // Now connect to the newly created database
            conn = DriverManager.getConnection(URL + DATABASE_NAME, USERNAME, PASSWORD);
            System.out.println("Successfully connected to newly created database");
            return conn;
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