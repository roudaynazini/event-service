package org.example.event_project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.event_project.entities.Commentaire;
import org.example.event_project.service.ServiceCommentaire;

import java.time.LocalDate;
import java.util.List;

public class CommentaireController {

    @FXML
    private Button button_create;

    @FXML
    private Button button_delete;

    @FXML
    private Button button_search;

    @FXML
    private Button button_update;

    @FXML
    private TableColumn<Commentaire, String> column_content;

    @FXML
    private TableColumn<Commentaire, String> column_date;

    @FXML
    private TableColumn<Commentaire, Integer> column_id;

    @FXML
    private TableColumn<Commentaire, Integer> column_postId;

    @FXML
    private TableColumn<Commentaire, Integer> column_userId;

    @FXML
    private TableView<Commentaire> table_comments;

    @FXML
    private TextField textfield_content;

    @FXML
    private TextField textfield_date;

    @FXML
    private TextField textfield_postId;

    @FXML
    private TextField textfield_userId;

    private final ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    @FXML
    private void initialize() {
        column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        column_userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        column_postId.setCellValueFactory(new PropertyValueFactory<>("postId"));
        column_content.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        column_date.setCellValueFactory(new PropertyValueFactory<>("dateCommentaire"));

        afficherTousCommentaires();
    }

    @FXML
    void ajouter(ActionEvent event) {
        String userIdText = textfield_userId.getText().trim();
        String postIdText = textfield_postId.getText().trim();
        String contenu = textfield_content.getText().trim();
        String dateText = textfield_date.getText().trim();

        if (userIdText.isEmpty() || postIdText.isEmpty() || contenu.isEmpty() || dateText.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        int userId, postId;
        try {
            userId = Integer.parseInt(userIdText);
            postId = Integer.parseInt(postIdText);
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Les champs ID utilisateur et ID post doivent être des entiers.");
            return;
        }

        if (contenu.length() < 3) {
            showAlert("Contenu invalide", "Le contenu du commentaire doit contenir au moins 3 caractères.");
            return;
        }

        // Vérification du format de la date : "2025-04-27 00:00:00"
        if (!dateText.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
            showAlert("Format de date invalide", "La date doit être au format : yyyy-MM-dd HH:mm:ss (ex : 2025-04-27 00:00:00).");
            return;
        }

        Commentaire c = new Commentaire(postId, userId, contenu, dateText);
        serviceCommentaire.ajouter(c);
        afficherTousCommentaires();
        clearFields();
    }


    @FXML
    void updateDescription(ActionEvent event) {
        Commentaire selected = table_comments.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String contenu = textfield_content.getText().trim();

            if (contenu.isEmpty()) {
                showAlert("Champ vide", "Veuillez entrer un contenu pour le commentaire.");
                return;
            }

            if (contenu.length() < 3) {
                showAlert("Contenu invalide", "Le contenu doit contenir au moins 3 caractères.");
                return;
            }

            selected.setContenu(contenu);
            serviceCommentaire.updateContenu(selected);
            afficherTousCommentaires();
            clearFields();
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un commentaire à modifier.");
        }
    }

    @FXML
    void delete(ActionEvent event) {
        Commentaire selected = table_comments.getSelectionModel().getSelectedItem();
        if (selected != null) {
            serviceCommentaire.delete(selected.getId());
            afficherTousCommentaires();
            clearFields();
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un commentaire à supprimer.");
        }
    }

    @FXML
    void search(ActionEvent event) {
        try {
            int postId = Integer.parseInt(textfield_postId.getText().trim());
            List<Commentaire> commentaires = serviceCommentaire.afficherParPost(postId);
            table_comments.getItems().setAll(commentaires);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID du post doit être un nombre.");
        }
    }

    private void afficherTousCommentaires() {
        List<Commentaire> commentaires = serviceCommentaire.afficherAll();
        table_comments.getItems().setAll(commentaires);
    }

    private void clearFields() {
        textfield_userId.clear();
        textfield_postId.clear();
        textfield_content.clear();
        textfield_date.clear();
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
