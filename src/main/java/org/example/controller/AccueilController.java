package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class AccueilController {



    @FXML
    public void goToAvis(ActionEvent event) {
        chargerInterface(event, "/avis.fxml");
    }

    @FXML
    public void goToReclamation(ActionEvent event) {
        chargerInterface(event, "/reclamation.fxml");
    }

    private void chargerInterface(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface : " + fxmlPath);
            e.printStackTrace();
        }

    }
    @FXML
    public void ouvrirAffichageAvisReclamations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageAvisReclamations.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Affichage des Avis et Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du fichier FXML : " + e.getMessage());
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



