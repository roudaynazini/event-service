package com.roudaynazini.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "connectart1";
    private static final String URL = BASE_URL + DATABASE_NAME;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // First connect without database to create it if needed
                try (Connection tempConn = DriverManager.getConnection(BASE_URL, USERNAME, PASSWORD);
                     Statement stmt = tempConn.createStatement()) {
                    stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
                }
                
                // Now connect to the specific database
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
                // Create tables if they don't exist
                createTablesIfNotExist();
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException("Error connecting to database", e);
            }
        }
        return connection;
    }

    private static void createTablesIfNotExist() throws SQLException {
        try (var stmt = connection.createStatement()) {
            // Create reservations table first (since contracts table references it)
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "client_name VARCHAR(100) NOT NULL, " +
                "event_date DATE NOT NULL, " +
                "status VARCHAR(50) NOT NULL, " +
                "notes TEXT" +
                ")");

            // Create contracts table
            stmt.execute("CREATE TABLE IF NOT EXISTS contracts (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "reservation_id BIGINT NOT NULL, " +
                "contract_number VARCHAR(50) NOT NULL, " +
                "contract_type VARCHAR(50) NOT NULL, " +
                "status VARCHAR(50) NOT NULL, " +
                "start_date DATE NOT NULL, " +
                "end_date DATE NOT NULL, " +
                "total_amount DOUBLE NOT NULL, " +
                "terms TEXT, " +
                "notes TEXT, " +
                "FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE" +
                ")");

            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) NOT NULL" +
                ")");
        }
    }
} 