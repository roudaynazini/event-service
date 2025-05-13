package org.example.event_project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.event_project.entities.Sponsor;
import org.example.event_project.service.ServiceSponsor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ManageSponsor {

    @FXML
    private TableColumn<Sponsor, String> afichhe_descriptin;
    @FXML
    private Button manageivent;
    @FXML
    private TableColumn<Sponsor, Integer> afichhe_id;
    @FXML
    private TableColumn<Sponsor, String> afichhe_name;
    @FXML
    private TableColumn<Sponsor, String> afichhe_url;
    @FXML
    private TableColumn<Sponsor, String> afichhe_website;
    @FXML
    private Button button_creat;
    @FXML
    private Button button_delete;
    @FXML
    private Button button_search;
    @FXML
    private Button button_update;
    @FXML
    private TableView<Sponsor> tableviwsponser;
    @FXML
    private TextField textfiled_description;
    @FXML
    private TextField textfiled_logourl;
    @FXML
    private TextField textfiled_name;
    @FXML
    private TextField textfiled_search;
    @FXML
    private TextField textfiled_website;

    ServiceSponsor serviceSponsor = new ServiceSponsor();

    @FXML
    void initialize() {
        try {
            List<Sponsor> sponsors = serviceSponsor.getAll();
            ObservableList<Sponsor> observableSponsors = FXCollections.observableList(sponsors);
            tableviwsponser.setItems(observableSponsors);

            afichhe_id.setCellValueFactory(new PropertyValueFactory<>("sponsorId"));
            afichhe_name.setCellValueFactory(new PropertyValueFactory<>("name"));
            afichhe_url.setCellValueFactory(new PropertyValueFactory<>("logoUrl"));
            afichhe_website.setCellValueFactory(new PropertyValueFactory<>("website"));
            afichhe_descriptin.setCellValueFactory(new PropertyValueFactory<>("description"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void addsponsor(ActionEvent event) {
        String name = textfiled_name.getText().trim();
        String logoUrl = textfiled_logourl.getText().trim();
        String website = textfiled_website.getText().trim();
        String description = textfiled_description.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ vide", "Le nom est requis.");
            textfiled_name.requestFocus();
            return;
        }
        if (logoUrl.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ vide", "L'URL du logo est requise.");
            textfiled_logourl.requestFocus();
            return;
        }
        if (website.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ vide", "Le site web est requis.");
            textfiled_website.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ vide", "La description est requise.");
            textfiled_description.requestFocus();
            return;
        }

        Sponsor sponsor = new Sponsor(name, logoUrl, website, description);
        try {
            serviceSponsor.ajouterPstm(sponsor);
            refreshTable();
            clearInputs();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor ajouté avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void updatesponsor(ActionEvent event) {
        Sponsor selectedSponsor = tableviwsponser.getSelectionModel().getSelectedItem();
        if (selectedSponsor == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un sponsor à modifier.");
            return;
        }

        String name = textfiled_name.getText().trim();
        String logoUrl = textfiled_logourl.getText().trim();
        String website = textfiled_website.getText().trim();
        String description = textfiled_description.getText().trim();

        if (name.isEmpty() || logoUrl.isEmpty() || website.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Tous les champs doivent être remplis.");
            return;
        }

        selectedSponsor.setName(name);
        selectedSponsor.setLogoUrl(logoUrl);
        selectedSponsor.setWebsite(website);
        selectedSponsor.setDescription(description);

        try {
            serviceSponsor.update(selectedSponsor);
            refreshTable();
            clearInputs();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor mis à jour !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void deletsponsor(ActionEvent event) {
        Sponsor selectedSponsor = tableviwsponser.getSelectionModel().getSelectedItem();
        if (selectedSponsor != null) {
            try {
                serviceSponsor.delete(selectedSponsor);
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor supprimé !");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un sponsor à supprimer.");
        }
    }

    @FXML
    void searchsponsor(ActionEvent event) {
        String searchText = textfiled_search.getText().trim();
        try {
            List<Sponsor> sponsors = searchText.isEmpty()
                    ? serviceSponsor.getAll()
                    : serviceSponsor.rechercherParNom(searchText);
            tableviwsponser.setItems(FXCollections.observableList(sponsors));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshTable() {
        try {
            List<Sponsor> sponsors = serviceSponsor.getAll();
            tableviwsponser.setItems(FXCollections.observableList(sponsors));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearInputs() {
        textfiled_name.clear();
        textfiled_logourl.clear();
        textfiled_website.clear();
        textfiled_description.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void gottomanageivent(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/manage_event_sponsor.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) manageivent.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
