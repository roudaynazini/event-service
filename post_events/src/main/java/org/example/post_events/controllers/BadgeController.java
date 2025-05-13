package org.example.post_events.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.post_events.entities.Badge;
import org.example.post_events.service.Badgegenerator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BadgeController implements Initializable {

    @FXML
    private TableView<Badge> badgeTable;
    @FXML
    private TableColumn<Badge, String> userIdColumn;
    @FXML
    private TableColumn<Badge, String> nameColumn;
    @FXML
    private TableColumn<Badge, String> descriptionColumn;
    @FXML
    private TableColumn<Badge, String> badgeEmojiColumn;
    @FXML
    private TableColumn<Badge, String> critereColumn;
    @FXML
    private TableColumn<Badge, String> dateColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des colonnes
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        badgeEmojiColumn.setCellValueFactory(new PropertyValueFactory<>("emoji"));
        critereColumn.setCellValueFactory(new PropertyValueFactory<>("critere"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAttribution"));

        // Exemple de récupération des données de badges avec l'ID utilisateur
        List<Badge> badges = Badgegenerator.generateBadges(); // Récupération de la liste des badges
        badgeTable.getItems().addAll(badges);  // Ajout des badges à la table
    }
}
