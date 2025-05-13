package org.example.event_project.Controllers;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import java.io.FileOutputStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.event_project.entities.reclamation;
import org.example.event_project.service.servicereclamatin;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class ReclamationController {

    @FXML
    private TextField tfIdUtilisateur, tfIdEvenement, tf_search;
    @FXML
    private TextArea tfDescription;
    @FXML
    private ComboBox<String> cbEtat;
    @FXML
    private DatePicker dpDateReclamation;
    @FXML
    private TableView<reclamation> tableReclamation;
    @FXML
    private TableColumn<reclamation, Integer> columnIdReclamation, columnIdUtilisateur, columnIdEvenement;
    @FXML
    private TableColumn<reclamation, String> columnDescription, columnEtat;
    @FXML
    private TableColumn<reclamation, Date> columnDate;
    @FXML
    private Button btnDownloadPdf;
    private servicereclamatin service = new servicereclamatin();
    private ObservableList<reclamation> reclamationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbEtat.setItems(FXCollections.observableArrayList("En attente", "En cours", "Résolue"));

        columnIdReclamation.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdReclamation()).asObject());
        columnIdUtilisateur.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdUtilisateur()).asObject());
        columnIdEvenement.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdEvenement()).asObject());
        columnDescription.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        columnEtat.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEtat()));
        columnDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateReclamation()));

        loadReclamations();
    }

    private void loadReclamations() {
        try {
            List<reclamation> list = service.getAll();
            reclamationList.setAll(list);
            tableReclamation.setItems(reclamationList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterReclamation() {
        try {
            if (tfIdUtilisateur.getText().isEmpty() || tfIdEvenement.getText().isEmpty()) {
                showAlert("Erreur", "ID utilisateur et ID événement sont requis.");
                return;
            }

            int idUtilisateur = Integer.parseInt(tfIdUtilisateur.getText());
            int idEvenement = Integer.parseInt(tfIdEvenement.getText());
            String description = tfDescription.getText();

            // Vérifier si la description contient des mots inappropriés
            if (service.contientMotsInappropries(description)) {
                showAlert("Erreur", "La description contient des mots inappropriés et la réclamation ne peut pas être ajoutée.");
                return; // Arrêter l'ajout si la description contient des mots inappropriés
            }

            // Vérifier si la réclamation existe déjà
            if (service.existeDejaReclamation(idUtilisateur, idEvenement, description)) {
                showAlert("Erreur", "Une réclamation similaire existe déjà pour cet utilisateur et cet événement.");
                return; // Si la réclamation existe déjà, ne pas l'ajouter
            }

            String etat = cbEtat.getValue();

            // Vérification de la date
            Date dateReclamation;
            if (dpDateReclamation.getValue() != null) {
                dateReclamation = java.sql.Date.valueOf(dpDateReclamation.getValue());

                // Vérifier si la date est dans le futur
                if (dateReclamation.after(new java.sql.Date(System.currentTimeMillis()))) {
                    showAlert("Erreur", "La date ne peut pas être dans le futur.");
                    return;
                }
            } else {
                // Si la date n'est pas spécifiée, utiliser la date actuelle
                dateReclamation = new java.sql.Date(System.currentTimeMillis());
                showAlert("Information", "La date n'a pas été spécifiée, la date actuelle a été utilisée.");
            }

            // Créer une nouvelle réclamation
            reclamation r = new reclamation(idUtilisateur, 0, idEvenement, description, dateReclamation, etat);
            service.ajouter(r); // Ajouter la réclamation dans la base de données

            loadReclamations(); // Recharger les réclamations
            clearFields(); // Effacer les champs du formulaire
            showAlert("Succès", "Réclamation ajoutée avec succès."); // Afficher le message de succès
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les ID doivent être des entiers valides.");
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier vos données : " + e.getMessage());
        }
    }


    @FXML
    private void supprimerReclamation() {
        reclamation selected = tableReclamation.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Boîte de confirmation
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer cette réclamation ?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmation de suppression");
            confirm.setHeaderText(null);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                try {
                    service.delete(selected.getIdReclamation());
                    loadReclamations();
                    clearFields();
                    showAlert("Succès", "Réclamation supprimée avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Suppression impossible : " + e.getMessage());
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une réclamation à supprimer.");
        }
    }


    @FXML
    private void modifierReclamation() {
        reclamation selected = tableReclamation.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Récupérer la nouvelle description de la réclamation
                String nouvelleDescription = tfDescription.getText();

                // Vérifier si la description contient des mots inappropriés
                if (service.contientMotsInappropries(nouvelleDescription)) {
                    showAlert("Erreur", "La description contient des mots inappropriés et la réclamation ne peut pas être modifiée.");
                    return; // Arrêter la mise à jour si la description contient des mots inappropriés
                }

                // Mettre à jour les autres informations de la réclamation
                selected.setIdUtilisateur(Integer.parseInt(tfIdUtilisateur.getText()));
                selected.setIdEvenement(Integer.parseInt(tfIdEvenement.getText()));
                selected.setDescription(nouvelleDescription);
                selected.setEtat(cbEtat.getValue());
                selected.setDateReclamation(java.sql.Date.valueOf(dpDateReclamation.getValue()));

                // Effectuer la mise à jour dans la base de données
                service.update(selected);
                loadReclamations(); // Recharger les réclamations après modification
                clearFields(); // Effacer les champs du formulaire
                showAlert("Succès", "Réclamation modifiée avec succès."); // Afficher un message de succès
            } catch (SQLException e) {
                showAlert("Erreur", "Mise à jour impossible : " + e.getMessage());
            }
        }
    }

    @FXML
    private void searchReclamation() {
        String keyword = tf_search.getText();
        try {
            List<reclamation> results = service.rechercherParEtat(keyword);
            reclamationList.setAll(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRowClick(MouseEvent event) {
        reclamation selected = tableReclamation.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tfIdUtilisateur.setText(String.valueOf(selected.getIdUtilisateur()));
            tfIdEvenement.setText(String.valueOf(selected.getIdEvenement()));
            tfDescription.setText(selected.getDescription());
            dpDateReclamation.setValue(new java.sql.Date(selected.getDateReclamation().getTime()).toLocalDate());
            cbEtat.setValue(selected.getEtat());
        }
    }

    private void clearFields() {
        tfIdUtilisateur.clear();
        tfIdEvenement.clear();
        tfDescription.clear();
        cbEtat.getSelectionModel().clearSelection();
        dpDateReclamation.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    private void passerAavis() {
        try {
            // Charger le fichier FXML pour les avis
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/avis.fxml"));
            AnchorPane root = loader.load(); // Charge le layout Avis
            Scene scene = new Scene(root);

            // Récupérer le Stage actuel et changer la scène
            Stage stage = (Stage) tfIdUtilisateur.getScene().getWindow(); // Utilisation d'un composant de la scène actuelle
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Afficher un message d'erreur si la scène ne peut pas être chargée
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers les avis : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void retouracceuil() {
        try {
            // Charger le fichier FXML pour les avis
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/accueil_avis.fxml"));
            Parent root = loader.load(); // Charge le layout Avis
            Scene scene = new Scene(root);

            // Récupérer le Stage actuel et changer la scène
            Stage stage = (Stage) tfIdUtilisateur.getScene().getWindow(); // Utilisation d'un composant de la scène actuelle
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Afficher un message d'erreur si la scène ne peut pas être chargée
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers les avis : " + e.getMessage());
            alert.showAndWait();
        }


    }

    @FXML
    private void reinitialiserChamps() {
        clearFields();
        tf_search.clear(); // Réinitialiser aussi le champ de recherche s’il le faut
    }

    @FXML
    private void ouvrirStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/statrec.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage(); // Nouvelle fenêtre
            stage.setTitle("Statistiques des Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur d'ouverture des statistiques : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleDownloadPdf(ActionEvent event) {
        generatePdfFromTable();
        ouvrirPdf();
    }

    private void generatePdfFromTable() {
        Document document = new Document();

        try {
            String filePath = "reclamations.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Titre stylisé
            Paragraph titre = new Paragraph("Liste des Réclamations", new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            titre.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph(" ")); // Espace

            // Date de génération
            Paragraph date = new Paragraph("Date de génération : " + new Date().toString(), new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10));
            date.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" ")); // Espace

            // Table des réclamations
            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100);
            float[] columnWidths = {2f, 2f, 2f, 4f, 2.5f, 2.5f};
            pdfTable.setWidths(columnWidths);

            // Style d'en-tête
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.BaseColor headerColor = new com.itextpdf.text.BaseColor(230, 230, 230);

            Stream.of("ID Réclamation", "ID Utilisateur", "ID Événement", "Description", "Date", "État")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Paragraph(columnTitle, headerFont));
                        header.setBackgroundColor(headerColor);
                        header.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                        header.setPadding(5);
                        pdfTable.addCell(header);
                    });

            // Corps du tableau
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
            for (reclamation r : tableReclamation.getItems()) {
                pdfTable.addCell(new Paragraph(String.valueOf(r.getIdReclamation()), cellFont));
                pdfTable.addCell(new Paragraph(String.valueOf(r.getIdUtilisateur()), cellFont));
                pdfTable.addCell(new Paragraph(String.valueOf(r.getIdEvenement()), cellFont));
                pdfTable.addCell(new Paragraph(r.getDescription(), cellFont));
                pdfTable.addCell(new Paragraph(r.getDateReclamation().toString(), cellFont));
                pdfTable.addCell(new Paragraph(r.getEtat(), cellFont));
            }

            document.add(pdfTable);
            document.close();

            Thread.sleep(500); // Facultatif : attendre la fermeture du fichier
            ouvrirPdf();

        } catch (DocumentException | IOException | InterruptedException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }


    private void ouvrirPdf() {
        String filePath = "reclamations.pdf"; // Le chemin du fichier PDF généré
        File pdfFile = new File(filePath);

        if (pdfFile.exists()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdfFile); // Ouvre le PDF avec l'application par défaut
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir le fichier PDF : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Le fichier PDF n'existe pas.");
        }
    }


}