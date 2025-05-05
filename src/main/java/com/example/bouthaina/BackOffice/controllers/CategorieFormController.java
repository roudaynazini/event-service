package com.example.bouthaina.BackOffice.controllers;

import com.example.bouthaina.BackOffice.models.Categorie;
import com.example.bouthaina.BackOffice.services.CategorieService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CategorieFormController implements Initializable {
    @FXML
    private TextField typeField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    private CategorieService service;
    private Categorie currentCategorie;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new CategorieService();

        // Initialize the status combo box
        statusComboBox.setItems(FXCollections.observableArrayList("Actif", "Inactif"));
        statusComboBox.getSelectionModel().selectFirst();
    }

    public void setCategorie(Categorie categorie) {
        this.currentCategorie = categorie;
        this.isEditMode = true;

        // Fill the form with the category data
        typeField.setText(categorie.getType());
        descriptionField.setText(categorie.getDescription());
        statusComboBox.setValue(categorie.getStatus());
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        closeForm();
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        String type = typeField.getText();
        String description = descriptionField.getText();
        String status = statusComboBox.getValue();

        boolean success;

        if (isEditMode) {
            currentCategorie.setType(type);
            currentCategorie.setDescription(description);
            currentCategorie.setStatus(status);
            success = service.update(currentCategorie);
        } else {
            Categorie newCategorie = new Categorie(0, type, description, status);
            success = service.add(newCategorie);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    isEditMode ? "Catégorie mise à jour avec succès" : "Catégorie ajoutée avec succès");

            // Refresh the categories list
            closeForm();
            refreshCategoriesList();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite lors de l'enregistrement");
        }
    }

    private boolean validateInputs() {
        if (typeField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le type est requis");
            return false;
        }
        if (descriptionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La description est requise");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le statut est requis");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeForm() {
        // Get the current stage and close it
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void refreshCategoriesList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/bouthaina/AdminView/CategorieList.fxml"));
            Parent root = loader.load();

            CategorieListController controller = loader.getController();
            controller.loadCategories();

            // Find the dashboard content area to update
            Scene scene = btnCancel.getScene();
            if (scene != null && scene.getRoot().lookup("#contentArea") instanceof StackPane) {
                StackPane contentArea = (StackPane) scene.getRoot().lookup("#contentArea");
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
