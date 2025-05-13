package org.example.event_project.repository.impl;



import org.example.event_project.config.DatabaseConfig;
import org.example.event_project.repository.NotificationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {
    private final Connection connection;

    public NotificationRepositoryImpl() {
        try {
            this.connection = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    @Override
    public void addNotification(String message) {
        String sql = "INSERT INTO notifications (message, is_read) VALUES (?, false)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add notification", e);
        }
    }

    @Override
    public List<String> getUnreadNotifications() {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT message FROM notifications WHERE is_read = false ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notifications.add(rs.getString("message"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get notifications", e);
        }
        return notifications;
    }

    @Override
    public void markAllAsRead() {
        String sql = "UPDATE notifications SET is_read = true WHERE is_read = false";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark notifications as read", e);
        }
    }
} 