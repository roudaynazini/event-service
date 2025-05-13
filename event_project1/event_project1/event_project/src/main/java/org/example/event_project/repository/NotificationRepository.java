package org.example.event_project.repository;

import java.util.List;

public interface NotificationRepository {
    void addNotification(String message);
    List<String> getUnreadNotifications();
    void markAllAsRead();
} 