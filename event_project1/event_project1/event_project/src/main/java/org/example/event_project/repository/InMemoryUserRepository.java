package org.example.event_project.repository;

import org.example.event_project.entities.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void setConnection(Connection connection) {
        // No-op for in-memory implementation
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public User findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
            users.add(user);
        } else {
            update(user);
        }
        return user;
    }

    @Override
    public User update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return user;
            }
        }
        return null;
    }

    @Override
    public void delete(int id) {
        users.removeIf(user -> user.getId() == id);
    }
} 