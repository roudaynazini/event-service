package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.event_project.entities.ConnexionDB;

import java.sql.SQLException;
import java.sql.Statement;

public class SupprimerPersonneController {

    @FXML
    private TextField idField;

    @FXML
    void supprimerPersonne() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un ID.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            Statement st = ConnexionDB.getStatement();
            String req = "DELETE FROM per WHERE id = " + id;
            int result = st.executeUpdate(req);

            if (result > 0) {
                showAlert("Succès", "✅ Personne supprimée avec succès !");
                idField.clear();
            } else {
                showAlert("Erreur", "❌ Aucune personne trouvée avec cet ID.");
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID doit être un nombre !");
        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
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

