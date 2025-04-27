package com.roudaynazini.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    
    public static void initializeDatabase(Connection connection) {
        try {
            // Read the SQL script from resources
            String sqlScript = new BufferedReader(
                new InputStreamReader(DatabaseInitializer.class.getResourceAsStream("/schema.sql")))
                .lines()
                .collect(Collectors.joining("\n"));
            
            // Split the script into individual statements
            String[] statements = sqlScript.split(";");
            
            // Execute each statement
            Statement stmt = connection.createStatement();
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
            
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 