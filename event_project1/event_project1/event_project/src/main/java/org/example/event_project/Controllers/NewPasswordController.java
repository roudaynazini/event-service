package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.example.event_project.entities.ConnexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class NewPasswordController {

    @FXML
    private PasswordField newPasswordField;

    private String userEmail;

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText();

        try (Connection conn = ConnexionDB.getConnection()) {
            // Mise à jour avec le nom correct du champ 'motdepasse'
            String sql = "UPDATE per SET motdepasse = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);  // Il serait préférable de hacher le mot de passe
            stmt.setString(2, userEmail);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Mot de passe mis à jour avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé.");
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }
}
