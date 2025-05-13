
package org.example.event_project.Controllers;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import org.example.event_project.service.serviceavis;

import java.sql.SQLException;
import java.util.*;

public class AffichageAvisReclamationsController {

    @FXML private TextField tfIdUtilisateur;
    @FXML private TextField tfIdEvenement;
    @FXML private TableView<Map<String, Object>> tableView;

    @FXML private TableColumn<Map<String, Object>, String> colIdAvis;
    @FXML private TableColumn<Map<String, Object>, String> colNote;
    @FXML private TableColumn<Map<String, Object>, String> colCommentaire;
    @FXML private TableColumn<Map<String, Object>, String> colIdReclamation;
    @FXML private TableColumn<Map<String, Object>, String> colEtat;
    @FXML private TableColumn<Map<String, Object>, String> colDescription;

    @FXML
    private void initialize() {
        colIdAvis.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("idAvis"))));
        colNote.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("note"))));
        colCommentaire.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("commentaire"))));
        colIdReclamation.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("idReclamation"))));
        colEtat.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("etat"))));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get("description"))));
    }

    @FXML
    private void afficherAvisEtReclamations() {
        try {
            int idUtilisateur = Integer.parseInt(tfIdUtilisateur.getText());
            int idEvenement = Integer.parseInt(tfIdEvenement.getText());

            serviceavis serviceAvis = new serviceavis();
            List<Map<String, Object>> resultat = serviceAvis.getAvisEtReclamations(idUtilisateur, idEvenement);

            if (resultat.isEmpty()) {
                showAlert("Info", "Aucun avis ni réclamation trouvé.");
                tableView.setItems(FXCollections.observableArrayList());
            } else {
                ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(resultat);
                tableView.setItems(data);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des IDs valides.");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur SQL : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
