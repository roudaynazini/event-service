package com.roudaynazini.repository;

import com.roudaynazini.config.DatabaseConfig;
import com.roudaynazini.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLUserRepository implements UserRepository {
    private Connection connection;

    public MySQLUserRepository() {
        try {
            System.out.println("Initializing MySQLUserRepository...");
            connection = DatabaseConfig.getConnection();
            System.out.println("Database connection established");
            createTableIfNotExists();
            System.out.println("Table creation/verification complete");
        } catch (Exception e) {
            System.err.println("Error initializing MySQLUserRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        System.out.println("Creating users table if not exists...");
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) NOT NULL UNIQUE," +
                "password VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "role VARCHAR(20) NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table created/verified successfully");
            
            // Verify table exists and is accessible
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next()) {
                    System.out.println("Users table contains " + rs.getInt(1) + " records");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating/verifying users table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public User findByUsername(String username) {
        System.out.println("Finding user by username: " + username);
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    System.out.println("User found: " + user.getUsername());
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("No user found with username: " + username);
        return null;
    }

    @Override
    public User save(User user) {
        System.out.println("Saving user: " + user.getUsername());
        if (user.getId() == 0) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        System.out.println("Inserting new user: " + user.getUsername());
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Insert affected " + affectedRows + " rows");
            
            if (affectedRows == 0) {
                System.err.println("Failed to insert user: " + user.getUsername());
                return null;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    System.out.println("User inserted with ID: " + user.getId());
                    
                    // Verify the user was actually inserted
                    User verifiedUser = findByUsername(user.getUsername());
                    if (verifiedUser != null) {
                        System.out.println("User verified in database: " + verifiedUser.getUsername());
                    } else {
                        System.err.println("Failed to verify user insertion!");
                    }
                }
            }
            return user;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        return user;
    }
} 