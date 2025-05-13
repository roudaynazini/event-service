package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.event_project.entities.ConnexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class compte {

    @FXML private Label nomLabel;
    @FXML private Label prenomLabel;
    @FXML private Label emailLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label ageLabel;
    @FXML private Label idLabel;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
        chargerCompte();
    }

    private void chargerCompte() {
        System.out.println("Chargement du compte pour l'email: " + userEmail);
        String query = "SELECT id, nom, prenom, email, numtelephone, age FROM per WHERE email = ?";

        try (
                Connection conn = ConnexionDB.getConnection();
                PreparedStatement pst = conn.prepareStatement(query)
        ) {
            pst.setString(1, userEmail);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Données trouvées dans la base de données.");
                    idLabel.setText(rs.getString("id"));
                    nomLabel.setText(rs.getString("nom"));
                    prenomLabel.setText(rs.getString("prenom"));
                    emailLabel.setText(rs.getString("email"));
                    telephoneLabel.setText(rs.getString("numtelephone"));
                    ageLabel.setText(rs.getString("age"));
                } else {
                    System.out.println("Aucun résultat trouvé pour cet email.");
                    showAlert("Erreur", "Utilisateur non trouvé !");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL: " + e.getMessage());
            showAlert("Erreur SQL", e.getMessage());
        }
    }

    @FXML
    void modifierCompte() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/modifier.fxml"));
            Parent root = loader.load();
            modifier modifierController = loader.getController();
            modifierController.setUserEmail(userEmail);

            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le compte");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de modification.");
        }
    }

    @FXML
    void supprimerCompte() {
        String query = "DELETE FROM per WHERE email = ?";

        try (
                Connection conn = ConnexionDB.getConnection();
                PreparedStatement pst = conn.prepareStatement(query)
        ) {
            pst.setString(1, userEmail);
            int res = pst.executeUpdate();

            if (res > 0) {
                showAlert("Succès", "Compte supprimé !");
                deconnexion();
            } else {
                showAlert("Erreur", "Aucun compte trouvé à supprimer !");
            }
        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        }
    }

    @FXML
    void deconnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/connexion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à l'écran de connexion.");
        }
    }

    @FXML
    void allerProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/compte.fxml"));
            Parent root = loader.load();

            compte compteController = loader.getController();
            compteController.setUserEmail(userEmail);

            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Compte");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au profil.");
        }
    }

    @FXML
    private void allerAccueil(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/accueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'accueil.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
