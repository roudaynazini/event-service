package org.example.event_project.repository;

import org.example.event_project.entities.User;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void setConnection(Connection connection);
    List<User> findAll();
    Optional<User> findById(int id);
    User findByUsername(String username);
    User save(User user);
    User update(User user);
    void delete(int id);
} 