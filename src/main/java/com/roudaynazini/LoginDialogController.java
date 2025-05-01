package com.roudaynazini;

import com.roudaynazini.model.User;
import com.roudaynazini.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginDialogController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserRepository userRepository;
    private Stage dialogStage;
    private User loggedInUser;
    private boolean loginSuccessful = false;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    public void setUserRepository(UserRepository repository) {
        this.userRepository = repository;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Attempting login for user: " + username);

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Login failed: Empty username or password");
            showError("Please enter both username and password");
            return;
        }

        User user = userRepository.findByUsername(username);
        System.out.println("User found in database: " + (user != null));
        
        if (user != null) {
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Entered password: " + password);
        }

        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for user: " + username);
            loggedInUser = user;
            loginSuccessful = true;
            dialogStage.close();
        } else {
            System.out.println("Login failed: Invalid username or password");
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
} 