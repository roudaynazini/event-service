package com.roudaynazini.repository;

import java.util.List;

public interface NotificationRepository {
    void addNotification(String message);
    List<String> getUnreadNotifications();
    void markAllAsRead();
} 