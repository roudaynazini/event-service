package org.example.event_project.Controllers;
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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.BaseColor;
import org.example.event_project.entities.avis;
import org.example.event_project.service.serviceavis;


import java.io.FileOutputStream;

import java.io.File;
import java.awt.Desktop;

import java.util.stream.Stream;


public class Aviscontroller {

    @FXML
    private TextField tf_idUtilisateur, tf_idEvenement, tf_commentaire, tf_search;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<avis> tableview_avis;
    @FXML
    private TableColumn<avis, Integer> column_idAvis, column_idUtilisateur, column_idEvenement;
    @FXML
    private TableColumn<avis, String> column_note;
    @FXML
    private TableColumn<avis, String> column_commentaire;
    @FXML
    private TableColumn<avis, Date> column_dateAvis;

    @FXML
    private RadioButton rb_1, rb_2, rb_3, rb_4, rb_5;
    private ToggleGroup ratingToggleGroup;


    private serviceavis sa = new serviceavis();
    private ObservableList<avis> avisList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        column_idAvis.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdAvis()).asObject());
        column_idUtilisateur.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdUtilisateur()).asObject());
        column_idEvenement.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdEvenement()).asObject());
        column_note.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(getStars(cell.getValue().getNote())));

        column_commentaire.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCommentaire()));
        column_dateAvis.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getDateAvis()));

        loadAvis();

        ratingToggleGroup = new ToggleGroup();

        // Associer chaque RadioButton au ToggleGroup
        rb_1.setToggleGroup(ratingToggleGroup);
        rb_2.setToggleGroup(ratingToggleGroup);
        rb_3.setToggleGroup(ratingToggleGroup);
        rb_4.setToggleGroup(ratingToggleGroup);
        rb_5.setToggleGroup(ratingToggleGroup);

    }

    private void loadAvis() {
        try {
            List<avis> list = sa.getAll();
            avisList.setAll(list);
            tableview_avis.setItems(avisList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedNote() {
        if (rb_1.isSelected()) return 1;
        if (rb_2.isSelected()) return 2;
        if (rb_3.isSelected()) return 3;
        if (rb_4.isSelected()) return 4;
        if (rb_5.isSelected()) return 5;
        return 0;
    }

    private boolean validateFields() {
        if (tf_idUtilisateur.getText().isEmpty() || !tf_idUtilisateur.getText().matches("\\d+")) {
            showAlert("Validation Error", "Please enter a valid user ID (numeric).");
            return false;
        }
        if (tf_idEvenement.getText().isEmpty() || !tf_idEvenement.getText().matches("\\d+")) {
            showAlert("Validation Error", "Please enter a valid event ID (numeric).");
            return false;
        }
        if (tf_commentaire.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter a comment.");
            return false;
        }
        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a date.");
            return false;
        }
        // ✅ Vérifie que la date est aujourd’hui
        if (!datePicker.getValue().isEqual(LocalDate.now())) {
            showAlert("Validation Error", "La date doit être celle d'aujourd'hui.");
            return false;
        }
        if (getSelectedNote() == 0) {
            showAlert("Validation Error", "Please select a rating.");
            return false;
        }
        return true;
    }


    @FXML
    void createAvis() {
        if (!validateFields()) return; // Valider les champs avant toute action

        try {
            // Récupérer les valeurs des champs
            int utilisateur = Integer.parseInt(tf_idUtilisateur.getText());
            int evenement = Integer.parseInt(tf_idEvenement.getText());
            String commentaire = tf_commentaire.getText();
            int note = getSelectedNote();
            LocalDate localDate = datePicker.getValue();
            Date date = java.sql.Date.valueOf(localDate);

            // Vérifier si le commentaire contient des mots inappropriés
            if (sa.contientMotsInappropries(commentaire)) {
                showInfo("Erreur", "L'avis contient des mots inappropriés et ne peut pas être ajouté.");
                return; // Arrêter l'ajout si le commentaire est inapproprié
            }

            // Vérifier si un avis similaire existe déjà (même utilisateur, même événement, même commentaire)
            if (sa.existeDejaAvis(utilisateur, evenement, commentaire)) {
                showInfo("Erreur", "Un avis similaire existe déjà pour cet utilisateur et cet événement.");
                return; // Ne pas ajouter l'avis si similaire
            }

            // Si le commentaire est valide et qu'il n'existe pas d'avis similaire, créer un objet avis
            avis a = new avis(utilisateur, evenement, commentaire, note, (java.sql.Date) date);

            // Ajouter l'avis dans la base de données
            sa.ajouter(a);
            loadAvis(); // Recharger la liste des avis
            clearFields(); // Effacer les champs du formulaire
            showInfo("Succès", "L'avis a été ajouté avec succès."); // Afficher un message de succès

        } catch (SQLException e) {
            // Gérer l'exception liée à la base de données
            showAlert("Erreur de base de données", "Un problème est survenu lors de l'ajout de l'avis. Veuillez vérifier la base de données.");
            e.printStackTrace(); // Optionnel, pour afficher la trace de l'exception dans la console
        } catch (Exception e) {
            e.printStackTrace(); // Gérer d'autres exceptions
        }
    }


    @FXML
    void updateAvis() {
        if (!validateFields()) return;

        avis selected = tableview_avis.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Vérifier si le commentaire contient des mots inappropriés
                String commentaire = tf_commentaire.getText();
                if (sa.contientMotsInappropries(commentaire)) {
                    showInfo("Erreur", "Le commentaire contient des mots inappropriés et ne peut pas être modifié.");
                    return; // Arrêter la mise à jour si le commentaire est inapproprié
                }

                // Si le commentaire est valide, procéder à la mise à jour
                selected.setIdUtilisateur(Integer.parseInt(tf_idUtilisateur.getText()));
                selected.setIdEvenement(Integer.parseInt(tf_idEvenement.getText()));
                selected.setCommentaire(commentaire);
                selected.setNote(getSelectedNote());
                selected.setDateAvis(java.sql.Date.valueOf(datePicker.getValue()));

                // Mettre à jour l'avis dans la base de données
                sa.update(selected);
                loadAvis(); // Recharger la liste des avis
                clearFields(); // Effacer les champs du formulaire
                showInfo("Succès", "L'avis a été modifié avec succès.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void deleteAvis() {
        avis selected = tableview_avis.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Ajouter la confirmation ici
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer cet avis ?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmation de suppression");
            confirm.setHeaderText(null);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                try {
                    sa.delete(selected.getIdAvis());
                    loadAvis();
                    clearFields();
                    showInfo("Succès", "L'avis a été supprimé avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Aucun avis sélectionné", "Veuillez sélectionner un avis à supprimer.");
        }
    }



    @FXML
    void searchAvis() {
        String keyword = tf_search.getText();
        try {
            int note = Integer.parseInt(keyword);
            List<avis> results = sa.rechercherParNote(note);
            avisList.setAll(results);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide pour la note.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onRowClick(MouseEvent event) {
        avis selected = tableview_avis.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tf_idUtilisateur.setText(String.valueOf(selected.getIdUtilisateur()));
            tf_idEvenement.setText(String.valueOf(selected.getIdEvenement()));
            tf_commentaire.setText(selected.getCommentaire());
            datePicker.setValue(new java.sql.Date(selected.getDateAvis().getTime()).toLocalDate());

            switch (selected.getNote()) {
                case 1 -> rb_1.setSelected(true);
                case 2 -> rb_2.setSelected(true);
                case 3 -> rb_3.setSelected(true);
                case 4 -> rb_4.setSelected(true);
                case 5 -> rb_5.setSelected(true);
            }
        }
    }

    private void clearFields() {
        tf_idUtilisateur.clear();
        tf_idEvenement.clear();
        tf_commentaire.clear();
        datePicker.setValue(null);
        ratingToggleGroup.selectToggle(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
    @FXML
    private void passerAreclamation() {
        try {
            // Charger le fichier FXML pour les réclamations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/reclamation.fxml"));
            AnchorPane root = loader.load(); // Charge le layout Réclamation
            Scene scene = new Scene(root);

            // Récupérer la stage actuelle
            Stage stage = (Stage) tf_idUtilisateur.getScene().getWindow();

            // Changer la scène de la fenêtre actuelle
            stage.setScene(scene);
            stage.show(); // Afficher la nouvelle scène

        } catch (IOException e) {
            e.printStackTrace();
            // Afficher un message d'erreur si la scène ne peut pas être chargée
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers les réclamations : " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void retouracceuil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/accueil_avis.fxml"));
            Parent root = loader.load();  // Peu importe le layout utilisé dans accueil.fxml
            Scene scene = new Scene(root);

            Stage stage = (Stage) tf_idUtilisateur.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers l'accueil : " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/stat.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) tf_idUtilisateur.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur d'ouverture des statistiques : " + e.getMessage()).showAndWait();
        }
    }
    private String getStars(int note) {
        return "★".repeat(note) + "☆".repeat(5 - note);
    }

    @FXML
    private void handleDownloadPdf() {
        generatePdfFromAvis();
        ouvrirPdfAvis();
    }
    private void generatePdfFromAvis() {
        Document document = new Document();

        try {
            String filePath = "avis.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Titre
            Paragraph titre = new Paragraph("Liste des Avis", new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            titre.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph(" ")); // Espace

            // Date de génération
            Paragraph date = new Paragraph("Date de génération : " + new Date().toString(),
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10));
            date.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" "));

            // Tableau PDF
            PdfPTable pdfTable = new PdfPTable(5);
            pdfTable.setWidthPercentage(100);
            float[] columnWidths = {2f, 2f, 4f, 1f, 2.5f};
            pdfTable.setWidths(columnWidths);

            // En-têtes
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            BaseColor headerColor = new BaseColor(230, 230, 230);

            Stream.of("ID Utilisateur", "ID Événement", "Commentaire", "Note", "Date")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Paragraph(columnTitle, headerFont));
                        header.setBackgroundColor(headerColor);
                        header.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                        header.setPadding(5);
                        pdfTable.addCell(header);
                    });

            // Contenu
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
            for (avis a : tableview_avis.getItems()) {
                pdfTable.addCell(new Paragraph(String.valueOf(a.getIdUtilisateur()), cellFont));
                pdfTable.addCell(new Paragraph(String.valueOf(a.getIdEvenement()), cellFont));
                pdfTable.addCell(new Paragraph(a.getCommentaire(), cellFont));
                pdfTable.addCell(new Paragraph(String.valueOf(a.getNote()), cellFont));
                pdfTable.addCell(new Paragraph(a.getDateAvis().toString(), cellFont));
            }

            document.add(pdfTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
        }}
        private void ouvrirPdfAvis() {
            File pdfFile = new File("avis.pdf");
            if (pdfFile.exists()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    showAlert("Erreur", "Impossible d'ouvrir le fichier PDF : " + e.getMessage());
                }
            } else {
                showAlert("Erreur", "Le fichier PDF n'existe pas.");
            }}

}





