package org.example.event_project.Controllers;

import org.example.event_project.entities.User;
import org.example.event_project.service.UserService;

import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public User getUserById(int id) {
        return userService.getUserById(id);
    }

    public User createUser(User user) {
        return userService.createUser(user);
    }

    public User updateUser(int id, User user) {
        return userService.updateUser(id, user);
    }

    public void deleteUser(int id) {
        userService.deleteUser(id);
    }
} 