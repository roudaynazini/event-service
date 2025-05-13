package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.event_project.entities.ConnexionDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class modifier {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField passwordField;  // Affichage du mot de passe en clair (à remplacer par PasswordField si besoin)
    @FXML
    private TextField confirmPasswordField;

    private String userEmail; // Email de l'utilisateur connecté

    // Méthode pour passer l'email de l'utilisateur depuis 'compte.java'
    public void setUserEmail(String email) {
        this.userEmail = email;
        chargerInfosActuelles();  // Charger les infos de l'utilisateur avec l'email
    }

    // Charger les informations actuelles de l'utilisateur à partir de la base de données
    private void chargerInfosActuelles() {
        try {
            String query = "SELECT * FROM per WHERE email = ?";
            PreparedStatement pst = ConnexionDB.getConnection().prepareStatement(query);
            pst.setString(1, userEmail);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nomField.setText(rs.getString("nom"));
                prenomField.setText(rs.getString("prenom"));
                telephoneField.setText(rs.getString("numtelephone"));
                emailField.setText(rs.getString("email"));
                ageField.setText(String.valueOf(rs.getInt("age")));
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les données : " + e.getMessage());
        }
    }

    @FXML
    void enregistrerModifications() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();
        String ageStr = ageField.getText();
        String motdepasse = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Vérifier que tous les champs obligatoires sont remplis
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérifier que les mots de passe correspondent
        if (!motdepasse.isEmpty() && !motdepasse.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            String sql;

            // Choisir la bonne requête SQL selon que le mot de passe a été modifié ou non
            if (motdepasse.isEmpty()) {
                sql = "UPDATE per SET nom = ?, prenom = ?, numtelephone = ?, email = ?, age = ? WHERE email = ?";
            } else {
                sql = "UPDATE per SET nom = ?, prenom = ?, numtelephone = ?, email = ?, age = ?, motdepasse = ? WHERE email = ?";
            }

            PreparedStatement ps = ConnexionDB.getConnection().prepareStatement(sql);
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, telephone);
            ps.setString(4, email);
            ps.setInt(5, age);

            if (motdepasse.isEmpty()) {
                ps.setString(6, userEmail);
            } else {
                ps.setString(6, motdepasse);
                ps.setString(7, userEmail);
            }

            int result = ps.executeUpdate();

            if (result > 0) {
                showAlert("Succès", "Compte mis à jour !");
            } else {
                showAlert("Erreur", "La mise à jour a échoué.");
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'âge doit être un nombre.");
        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        }
    }
    @FXML
    private void retourCompte() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/compte.fxml"));
            javafx.scene.Parent root = loader.load();

            // Passer l'email à la scène suivante (si besoin)
            compte compteController = loader.getController();
            compteController.setUserEmail(userEmail);

            // Afficher la nouvelle scène
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) nomField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de revenir à la page Compte : " + e.getMessage());
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}