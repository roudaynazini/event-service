package org.example.event_project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.event_project.entities.ConnexionDB;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class  ajout implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField numTelephoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Integer> ageComboBox;

    private String nom, prenom, numTelephone, email, motDePasse;
    private int age;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> ages = FXCollections.observableArrayList();
        for (int i = 10; i <= 100; i++) {
            ages.add(i);
        }
        ageComboBox.setItems(ages);
    }

    @FXML
    void ajouterPersonne(ActionEvent event) {
        if (!validateFields()) return;

        nom = nomField.getText();
        prenom = prenomField.getText();
        numTelephone = numTelephoneField.getText();
        email = emailField.getText();
        motDePasse = passwordField.getText();
        age = ageComboBox.getValue();
        String role = "utilisateur";

        // Génération du code de vérification
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        try {
           EmailSender.sendVerificationCode(email, verificationCode);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Email", "Impossible d'envoyer l'e-mail : " + e.getMessage());
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/verification.fxml"));
            Parent root = loader.load();

            VerificationController controller = loader.getController();
            controller.setCodeAttendu(verificationCode, this::insererPersonne);

            Stage stage = new Stage();
            stage.setTitle("Vérification Email");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de vérification : " + e.getMessage());
        }
    }

    private void insererPersonne() {
        String req = "INSERT INTO per (nom, prenom, numtelephone, email, motdepasse, age, role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnexionDB.getConnection();
             PreparedStatement pst = conn.prepareStatement(req)) {

            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, numTelephone);
            pst.setString(4, email);
            pst.setString(5, motDePasse);
            pst.setInt(6, age);
            pst.setString(7, "utilisateur");

            int result = pst.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Personne ajoutée avec succès !");
                clearFields();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Ajout échoué : " + e.getMessage());
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().isEmpty()) errors.append("- Nom obligatoire\n");
        if (prenomField.getText().isEmpty()) errors.append("- Prénom obligatoire\n");
        if (numTelephoneField.getText().isEmpty()) errors.append("- Numéro de téléphone obligatoire\n");
        if (emailField.getText().isEmpty()) errors.append("- Email obligatoire\n");
        if (passwordField.getText().isEmpty()) errors.append("- Mot de passe obligatoire\n");
        if (ageComboBox.getValue() == null) errors.append("- Âge obligatoire\n");

        if (!isValidEmail(emailField.getText())) errors.append("- Email invalide\n");
        if (!numTelephoneField.getText().matches("\\d{8}")) errors.append("- Numéro de téléphone invalide (8 chiffres)\n");

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Champs invalides", errors.toString());
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        numTelephoneField.clear();
        emailField.clear();
        passwordField.clear();
        ageComboBox.setValue(null);
    }

    @FXML
    void annulerAction(ActionEvent event) {
        clearFields();
    }
}
