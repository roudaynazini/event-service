package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class VerificationController {

    @FXML
    private TextField codeField;

    private String codeAttendu;
    private Runnable onVerified;

    public void setCodeAttendu(String code, Runnable action) {
        this.codeAttendu = code;
        this.onVerified = action;
    }

    @FXML
    void verifierCode(ActionEvent event) {
        if (codeField == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champ codeField non initialisé !");
            return;
        }

        String codeSaisi = codeField.getText().trim();

        if (codeSaisi.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir le code de vérification.");
            return;
        }

        if (codeSaisi.equals(codeAttendu)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vérification réussie !");
            if (onVerified != null) {
                onVerified.run();
            }
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Code incorrect", "Le code de vérification est invalide.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
