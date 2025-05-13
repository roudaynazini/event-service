package org.example.event_project.Controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

import java.sql.SQLException;

import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import org.example.event_project.entities.Invitee;
import org.example.event_project.service.ServiceInvitee;

import java.io.IOException;

public class InviteeController {

    @FXML
    private TextField txtNomInvitee;
    @FXML
    private TextField txtEmailInvitee;
    @FXML
    private TextField txtTlfInvitee;
    @FXML
    private TextField txtAdresseInvitee;
    @FXML
    private TextField txtIdSupprimer; // ID de l'invité à supprimer
    @FXML
    private Label statusMessage; // Afficher les messages d'erreur ou de succès

    @FXML
    private TableView<Invitee> tableViewInvitee;

    @FXML
    private TableColumn<Invitee, Integer> colIdInvitee;
    @FXML
    private TableColumn<Invitee, String> colNomInvitee;
    @FXML
    private TableColumn<Invitee, String> colEmailInvitee;
    @FXML
    private TableColumn<Invitee, String> colTlfInvitee;
    @FXML
    private TableColumn<Invitee, String> colAdresseInvitee;

    @FXML
    private Button ajouterInviteeButton;
    @FXML
    private Button modifierInviteeButton;
    @FXML
    private Button supprimerInviteeButton;
    @FXML
    private Button btnRetourInviteeAffichage;


    private ServiceInvitee serviceInvitee = new ServiceInvitee();
    private Invitee inviteeSelectionnee = null;




    @FXML
    public void initialize() {
        // Définir les colonnes de la TableView
        colIdInvitee.setCellValueFactory(new PropertyValueFactory<>("idInvitee"));
        colNomInvitee.setCellValueFactory(new PropertyValueFactory<>("nomInvitee"));
        colEmailInvitee.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTlfInvitee.setCellValueFactory(new PropertyValueFactory<>("tlf"));
        colAdresseInvitee.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        loadInvitees(); // Charger les invités à l'initialisation

        // Ajouter des actions pour les boutons
        ajouterInviteeButton.setOnAction(this::handleAjouterInvitee);
        modifierInviteeButton.setOnAction(this::handleModifierInvitee);
        supprimerInviteeButton.setOnAction(this::handleSupprimerInvitee);
        btnRetourInviteeAffichage.setOnAction(this::handleRetourInviteeAffichage);

        tableViewInvitee.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                inviteeSelectionnee = newVal;  // Stocker l'invité sélectionné
                remplirChampsDepuisInvitee(newVal);  // Remplir les champs avec les données de l'invité
            }
        });

    }


    //dans modifier: Remplir les champs avec les données de l'invité sélectionné
    public void remplirChampsDepuisInvitee(Invitee invitee) {
        txtNomInvitee.setText(invitee.getNomInvitee());  // Remplir le champ Nom
        txtEmailInvitee.setText(invitee.getEmail());  // Remplir le champ Email
        txtTlfInvitee.setText(invitee.getTlf());  // Remplir le champ Téléphone
        txtAdresseInvitee.setText(invitee.getAdresse());  // Remplir le champ Adresse
    }



    // Charger les invités depuis la base de données
    private void loadInvitees() {
        try {
            List<Invitee> list = serviceInvitee.getAllInvitees();
            ObservableList<Invitee> observableList = FXCollections.observableArrayList(list);
            tableViewInvitee.setItems(observableList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des invités : " + e.getMessage());
        }
    }



    // Ajouter un invité
    private void handleAjouterInvitee(ActionEvent event) {
        String nomInvitee = txtNomInvitee.getText();
        String emailInvitee = txtEmailInvitee.getText();
        String tlfInvitee = txtTlfInvitee.getText();
        String adresseInvitee = txtAdresseInvitee.getText();

        // Vérification des champs
        if (nomInvitee.isEmpty() || emailInvitee.isEmpty() || tlfInvitee.isEmpty() || adresseInvitee.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérification email et numéro
        boolean emailValide = emailInvitee.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        boolean numeroValide = tlfInvitee.matches("^\\+?[0-9]{8,15}$");

        if (!emailValide && !numeroValide) {
            showAlert("Erreur", "Email et numéro de téléphone invalides.");
            return;
        } else if (!emailValide) {
            showAlert("Erreur", "Email invalide.");
            return;
        } else if (!numeroValide) {
            showAlert("Erreur", "Numéro invalide. Il doit contenir uniquement des chiffres (avec un + optionnel), entre 8 et 15 chiffres.");
            return;
        }




        Invitee invitee = new Invitee(nomInvitee, emailInvitee, tlfInvitee, adresseInvitee);

        try {
            boolean success = serviceInvitee.ajouterInvitee(invitee);
            if (success) {
                showAlert("Succès", "Invitee ajouté avec succès.");
                loadInvitees();
                clearFields();
            } else {
                showAlert("Erreur", "Erreur lors de l'ajout de l'invité.");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'invité : " + e.getMessage());
        }
    }






    private Invitee rechercherInviteeParId(int id) {
        try {
            return serviceInvitee.getInviteeById(id); // Cette méthode doit retourner un Invitee
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la recherche de l'invité : " + e.getMessage());
            return null;
        }
    }



    private void handleSupprimerInvitee(ActionEvent event) {
        String idText = txtIdSupprimer.getText();

        if (idText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un ID d'invité à supprimer.");
            return;
        }

        int idInvitee;
        try {
            idInvitee = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID de l'invité doit être un nombre valide.");
            return;
        }

        // Rechercher l'invité par ID avant de le supprimer
        Invitee invitee = rechercherInviteeParId(idInvitee);

        if (invitee != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer l'invité \"" + invitee.getNomInvitee() + "\" ?",
                    ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Suppression d'invité");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        boolean success = serviceInvitee.supprimerInvitee(idInvitee);

                        if (success) {
                            loadInvitees();
                            clearFields();
                            showAlert("Succès", "Invité supprimé avec succès.");
                        } else {
                            showAlert("Erreur", "Erreur lors de la suppression de l'invité.");
                        }
                    } catch (SQLException e) {
                        // Gérer l'exception SQL ici
                        showAlert("Erreur", "Erreur lors de la suppression de l'invité : " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert("Erreur", "Aucun invité trouvé avec l'ID " + idInvitee);
        }
    }





    // Modifier un invité sélectionné
    private void handleModifierInvitee(ActionEvent event) {
        if (inviteeSelectionnee != null) {
            String nomInvitee = txtNomInvitee.getText();
            String emailInvitee = txtEmailInvitee.getText();
            String tlfInvitee = txtTlfInvitee.getText();
            String adresseInvitee = txtAdresseInvitee.getText();

            // Vérification des champs
            if (nomInvitee.isEmpty() || emailInvitee.isEmpty() || tlfInvitee.isEmpty() || adresseInvitee.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }


            // Vérification email et numéro
            boolean emailValide = emailInvitee.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            boolean numeroValide = tlfInvitee.matches("^\\+?[0-9]{8,15}$");

            if (!emailValide && !numeroValide) {
                showAlert("Erreur", "Email et numéro de téléphone invalides.");
                return;
            } else if (!emailValide) {
                showAlert("Erreur", "Email invalide.");
                return;
            } else if (!numeroValide) {
                showAlert("Erreur", "Numéro de téléphone invalide.");
                return;
            }


            inviteeSelectionnee.setNomInvitee(nomInvitee);
            inviteeSelectionnee.setEmail(emailInvitee);
            inviteeSelectionnee.setTlf(tlfInvitee);
            inviteeSelectionnee.setAdresse(adresseInvitee);

            try {
                boolean success = serviceInvitee.modifierInvitee(inviteeSelectionnee);
                if (success) {
                    showAlert("Succès", "Invitee modifié avec succès.");
                    loadInvitees();
                    clearFields();
                } else {
                    showAlert("Erreur", "Erreur lors de la modification.");
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Aucun invité sélectionné.");
        }
    }




    @FXML
    private void handleRetourInviteeAffichage(ActionEvent event) {
        try {
            // Charger l'interface principale (dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et changer la scène
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetourInviteeAffichage.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // Afficher une alerte en cas d'erreur
            showAlert("Erreur", "Impossible de revenir à l'interface principale : " + e.getMessage());
        }
    }






    // Clear les champs
    private void clearFields() {
        txtNomInvitee.clear();
        txtEmailInvitee.clear();
        txtTlfInvitee.clear();
        txtAdresseInvitee.clear();
        txtIdSupprimer.clear();
        inviteeSelectionnee = null;
    }




    // Afficher une alerte
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
