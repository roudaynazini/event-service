package org.example.event_project.Controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.event_project.entities.Invitation;
import org.example.event_project.service.EmailService;
import org.example.event_project.service.ServiceInvitation;
import org.example.event_project.service.ServiceInvitee;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import java.sql.Date;
import java.time.LocalDate;

public class InvitationController {

    @FXML
    private ComboBox<String> comboBoxEmails;

    @FXML
    private TextField txtNomEvenement;

    @FXML
    private DatePicker datePickerEvenement;

    @FXML
    private TextArea txtMessage;

    @FXML
    private TextField txtIdSupprimer;

    @FXML
    private Label statusMessage; // Label to show status messages

    @FXML
    private TableView<Invitation> tableViewInvitation;

    @FXML
    private TableColumn<Invitation, Integer> colIdInvitation;

    @FXML
    private TableColumn<Invitation, String> colNomEvenement;

    @FXML
    private TableColumn<Invitation, String> colDateEvenement;

    @FXML
    private TableColumn<Invitation, String> colEmailInvitee;

    @FXML
    private TableColumn<Invitation, String> colMessage;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button retourButton;


    @FXML
    private ServiceInvitation serviceInvitation = new ServiceInvitation();
    private ServiceInvitee serviceInvitee = new ServiceInvitee();
    private Invitation invitationSelectionnee = null;

    @FXML
    public void initialize() {
        colIdInvitation.setCellValueFactory(new PropertyValueFactory<>("idInvitation"));
        colNomEvenement.setCellValueFactory(new PropertyValueFactory<>("nomEvenement"));
        colDateEvenement.setCellValueFactory(new PropertyValueFactory<>("dateEvenement"));
        colEmailInvitee.setCellValueFactory(new PropertyValueFactory<>("emailInvitee"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));

        loadEmails();
        loadInvitations();

        ajouterButton.setOnAction(this::handleAjouterInvitation);
        modifierButton.setOnAction(this::handleModifierInvitation);
        supprimerButton.setOnAction(this::handleSupprimerInvitation);

        tableViewInvitation.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                invitationSelectionnee = newVal;
                remplirChampsDepuisTable(newVal);
            }
        });
    }

    private void loadEmails() {
        List<String> emails = serviceInvitee.getAllEmails();
        comboBoxEmails.setItems(FXCollections.observableArrayList(emails));
    }

    private void loadInvitations() {
        try {
            List<Invitation> list = serviceInvitation.getAllInvitations();
            ObservableList<Invitation> observableList = FXCollections.observableArrayList(list);
            tableViewInvitation.setItems(observableList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des invitations : " + e.getMessage());
        }
    }

    private void remplirChampsDepuisTable(Invitation invitation) {
        txtNomEvenement.setText(invitation.getNomEvenement());
        comboBoxEmails.setValue(invitation.getEmailInvitee());

        // Conversion de java.sql.Date en LocalDate
        LocalDate localDate = invitation.getDateEvenement().toLocalDate();
        datePickerEvenement.setValue(localDate);
        txtMessage.setText(invitation.getMessage());
    }


    private void handleAjouterInvitation(ActionEvent event) {
        String nom = txtNomEvenement.getText();
        String email = comboBoxEmails.getValue();
        LocalDate localDate = datePickerEvenement.getValue();
        String message = txtMessage.getText();

        if (nom.isEmpty() || email == null || localDate == null || message.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Conversion de LocalDate en java.sql.Date
        Date date = Date.valueOf(localDate);

        Invitation invitation = new Invitation(nom, date, email, message);
        try {
            boolean success = serviceInvitation.ajouterInvitation(invitation);

            if (success) {
                // ✅ Envoi de l'e-mail
                String subject = "Invitation à l'événement : " + nom;
                String content = "Bonjour,\n\nVous êtes invité à participer à l'événement suivant :\n"
                        + "Nom : " + nom + "\n"
                        + "Date : " + date + "\n"
                        + "Message : " + message + "\n\nÀ bientôt !";



                loadInvitations();
                clearFields();
                showAlert("Succès", "Invitation ajoutée avec succès.");
            } else {
                showAlert("Erreur", "Erreur lors de l'ajout de l'invitation.");
            }
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'envoi de l'e-mail: " + e.getMessage());
        }
    }





    private void handleModifierInvitation(ActionEvent event) {
        if (invitationSelectionnee == null) {
            showAlert("Erreur", "Veuillez sélectionner une invitation à modifier.");
            return;
        }

        String nom = txtNomEvenement.getText();
        String email = comboBoxEmails.getValue();
        LocalDate localDate = datePickerEvenement.getValue();
        String message = txtMessage.getText();

        if (nom.isEmpty() || email == null || localDate == null || message.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        Date date = Date.valueOf(localDate);

        invitationSelectionnee.setNomEvenement(nom);
        invitationSelectionnee.setEmailInvitee(email);
        invitationSelectionnee.setDateEvenement(date);
        invitationSelectionnee.setMessage(message);

        boolean success = serviceInvitation.modifierInvitation(invitationSelectionnee);

        if (success) {
            loadInvitations();
            clearFields();
            showAlert("Succès", "Invitation modifiée avec succès.");
        } else {
            showAlert("Erreur", "Erreur lors de la modification.");
        }
    }

    private Invitation rechercherInvitationParId(int id) {
        try {
            // Cette méthode appelle getInvitationById dans ServiceInvitation
            return serviceInvitation.getInvitationById(id);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la recherche de l'invitation : " + e.getMessage());
            return null;
        }
    }

    private void handleSupprimerInvitation(ActionEvent event) {
        String idText = txtIdSupprimer.getText();

        if (idText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un ID d'invitation à supprimer.");
            return;
        }

        int idInvitation;
        try {
            idInvitation = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID d'invitation doit être un nombre valide.");
            return;
        }

        // Rechercher l'invitation par ID avant de la supprimer
        Invitation invitation = rechercherInvitationParId(idInvitation);

        if (invitation != null) {
            // Si l'invitation est trouvée, demander confirmation
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer l'invitation \"" + invitation.getNomEvenement() +
                            "\" pour " + invitation.getEmailInvitee() + " ?",
                    ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Suppression d'invitation");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    boolean success = serviceInvitation.supprimerInvitation(idInvitation);

                    if (success) {
                        loadInvitations();
                        clearFields();
                        showAlert("Succès", "Invitation supprimée avec succès.");
                    } else {
                        showAlert("Erreur", "Erreur lors de la suppression de l'invitation.");
                    }
                }
            });
        } else {
            showAlert("Erreur", "Aucune invitation trouvée avec l'ID " + idInvitation);
        }
    }


    @FXML
    public void handleRetour(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashBoard.fxml"));
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void clearFields() {
        txtNomEvenement.clear();
        comboBoxEmails.setValue(null);
        datePickerEvenement.setValue(null);
        txtMessage.clear();
        invitationSelectionnee = null;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
