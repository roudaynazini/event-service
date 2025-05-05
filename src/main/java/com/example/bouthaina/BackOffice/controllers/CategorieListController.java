package com.example.bouthaina.BackOffice.controllers;

import com.example.bouthaina.BackOffice.models.Categorie;
import com.example.bouthaina.BackOffice.services.CategorieService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategorieListController implements Initializable {
    @FXML
    private FlowPane categoriesContainer;

    @FXML
    private Button btnAjouter;

    private CategorieService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new CategorieService();
        loadCategories();
    }

    @FXML
    private void handleAddAction() {
        openCategorieForm(null);
    }

    public void loadCategories() {
        categoriesContainer.getChildren().clear();
        List<Categorie> categories = service.getAll();

        for (Categorie categorie : categories) {
            categoriesContainer.getChildren().add(createCategorieCard(categorie));
        }
    }

    private VBox createCategorieCard(Categorie categorie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);

        Label lblType = new Label(categorie.getType());
        lblType.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblDescription = new Label(categorie.getDescription());
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(260);

        Label lblStatus = new Label("Statut: " + categorie.getStatus());
        lblStatus.setStyle("-fx-text-fill: " + (categorie.getStatus().equals("Actif") ? "green" : "red") + ";");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox buttonBar = new HBox(10);
        Button btnEdit = new Button("Modifier");
        btnEdit.getStyleClass().add("edit-btn");
        btnEdit.setOnAction(e -> openCategorieForm(categorie));

        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-btn");
        btnDelete.setOnAction(e -> handleDeleteAction(categorie));

        buttonBar.getChildren().addAll(btnEdit, btnDelete);

        card.getChildren().addAll(lblType, lblDescription, lblStatus, spacer, buttonBar);
        card.setPadding(new Insets(15));

        return card;
    }

    private void openCategorieForm(Categorie categorie) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/bouthaina/AdminView/CategorieForm.fxml"));
            Parent root = loader.load();

            CategorieFormController controller = loader.getController();
            if (categorie != null) {
                controller.setCategorie(categorie);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(categorie == null ? "Ajouter une Catégorie" : "Modifier une Catégorie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh list after form is closed
            loadCategories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteAction(Categorie categorie) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette catégorie ?");

        ButtonType buttonTypeOui = new ButtonType("Oui");
        ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmAlert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOui) {
                if (service.delete(categorie.getIdCate())) {
                    loadCategories();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Impossible de supprimer cette catégorie.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
}
