package org.example.event_project.Controllers;



import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.event_project.entities.ConnexionDB;
import org.example.event_project.entities.Personne;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class AdminController {

    @FXML private TableView<Personne> tableView;
    @FXML private TableColumn<Personne, Integer> idCol;
    @FXML private TableColumn<Personne, String> nomCol;
    @FXML private TableColumn<Personne, String> prenomCol;
    @FXML private TableColumn<Personne, String> emailCol;
    @FXML private TableColumn<Personne, String> telCol;
    @FXML private TableColumn<Personne, Integer> ageCol;
    @FXML private TableColumn<Personne, String> motdepasseCol;
    @FXML private TextField searchFieldID;
    @FXML private Button logoutButton;




    private ObservableList<Personne> personnes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        nomCol.setCellValueFactory(data -> data.getValue().nomProperty());
        prenomCol.setCellValueFactory(data -> data.getValue().prenomProperty());
        emailCol.setCellValueFactory(data -> data.getValue().emailProperty());
        telCol.setCellValueFactory(data -> data.getValue().numTelephoneProperty());
        ageCol.setCellValueFactory(data -> data.getValue().ageProperty().asObject());
        motdepasseCol.setCellValueFactory(data -> data.getValue().motDePasseProperty());

        chargerDonnees();
    }

    @FXML
    void handleRechercher(ActionEvent event) {
        rechercherParID();
    }

    private void chargerDonnees() {
        personnes.clear();
        String req = "SELECT * FROM per";

        try (Connection conn = ConnexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(req)) {

            while (rs.next()) {
                Personne p = new Personne(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("numtelephone"),
                        rs.getInt("age"),
                        rs.getString("motdepasse")
                );
                personnes.add(p);
            }

            tableView.setItems(personnes);

        } catch (SQLException e) {
            afficherAlerte("Erreur SQL", e.getMessage());
        }
    }

    private void rechercherParID() {
        String searchText = searchFieldID.getText();

        if (searchText == null || searchText.isEmpty()) {
            tableView.setItems(personnes);
            return;
        }

        if (!searchText.matches("\\d+")) {
            afficherAlerte("Erreur", "Veuillez entrer un ID valide (chiffres uniquement).");
            return;
        }

        int idRecherche = Integer.parseInt(searchText);
        ObservableList<Personne> filtered = personnes.filtered(p -> p.getId() == idRecherche);

        if (filtered.isEmpty()) {
            afficherAlerte("Aucun résultat", "Aucune personne trouvée avec l'ID : " + idRecherche);
        }

        tableView.setItems(filtered);
    }

    @FXML
    void supprimer() {
        Personne selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                String req = "DELETE FROM per WHERE id = ?";

                try (Connection conn = ConnexionDB.getConnection();
                     PreparedStatement pst = conn.prepareStatement(req)) {

                    pst.setInt(1, selected.getId());
                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {
                        personnes.remove(selected);
                        tableView.setItems(personnes);
                        afficherAlerte("Succès", "Utilisateur supprimé avec succès.");
                    } else {
                        afficherAlerte("Erreur", "Suppression échouée.");
                    }

                } catch (SQLException e) {
                    afficherAlerte("Erreur SQL", e.getMessage());
                }
            }
        });
    }

    @FXML
    void telechargerPdf(ActionEvent event) {
        Document document = new Document();

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer en PDF");
            fileChooser.setInitialFileName("utilisateurs.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

            if (file != null) {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Liste des utilisateurs\n\n"));

                PdfPTable table = new PdfPTable(7); // 7 colonnes
                table.addCell("ID");
                table.addCell("Nom");
                table.addCell("Prénom");
                table.addCell("Email");
                table.addCell("Téléphone");
                table.addCell("Âge");
                table.addCell("Mot de passe");

                for (Personne p : tableView.getItems()) {
                    table.addCell(String.valueOf(p.getId()));
                    table.addCell(p.getNom());
                    table.addCell(p.getPrenom());
                    table.addCell(p.getEmail());
                    table.addCell(p.getNumTelephone());
                    table.addCell(String.valueOf(p.getAge()));
                    table.addCell(p.getMotDePasse());
                }

                document.add(table);
                afficherAlerte("Succès", "Le fichier PDF a été généré avec succès !");
            }

        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
        } finally {
            document.close();
        }
    }
    @FXML
    void ouvrirInterfaceModifier(ActionEvent event) {
        Personne selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            afficherAlerte("Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/AdminView/Dashboard/modifier.fxml"));
            Parent root = loader.load();

            // Appel à la méthode existante setUserEmail() dans le contrôleur modifier.java
            modifier controller = loader.getController();
            controller.setUserEmail(selected.getEmail());

            Stage stage = new Stage();
            stage.setTitle("Modifier Utilisateur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/AdminView/Dashboard/Connexion.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle via n'importe quel élément (ici le bouton)
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de se déconnecter : " + e.getMessage());
        }
    }
    @FXML
    void go_to_event(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/AdminView/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
