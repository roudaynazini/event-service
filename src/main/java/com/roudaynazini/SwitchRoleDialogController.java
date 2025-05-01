package com.roudaynazini;

import com.roudaynazini.model.User;
import com.roudaynazini.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SwitchRoleDialogController {
    @FXML
    private TextField usernameField;
    @FXML
    private ComboBox<String> roleComboBox;

    private UserRepository userRepository;
    private Stage dialogStage;
    private boolean roleChanged = false;
    private User updatedUser;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isRoleChanged() {
        return roleChanged;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("ADMIN", "USER");
    }

    @FXML
    private void handleChangeRole() {
        String username = usernameField.getText();
        String newRole = roleComboBox.getValue();

        if (username.isEmpty() || newRole == null) {
            showAlert("Error", "Please enter username and select a role.");
            return;
        }

        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRole(newRole);
            userRepository.update(user);
            roleChanged = true;
            updatedUser = user;
            dialogStage.close();
        } else {
            showAlert("Error", "User not found.");
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 