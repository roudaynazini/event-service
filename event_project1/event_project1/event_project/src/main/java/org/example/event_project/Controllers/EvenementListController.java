package org.example.event_project.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

// Imports pour Ikonli
import org.example.event_project.entities.Categorie;
import org.example.event_project.entities.Evenement;
import org.example.event_project.service.CategorieService;
import org.example.event_project.service.EvenementService;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignL;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;

public class EvenementListController implements Initializable {
    @FXML
    private FlowPane evenementsContainer;

    @FXML
    private Button btnAjouter;

    private EvenementService evenementService;
    private CategorieService categorieService;
    private Map<Integer, Categorie> categoriesMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        evenementService = new EvenementService();
        categorieService = new CategorieService();

        // Load categories into map for quick lookup
        loadCategoriesMap();

        // Load events
        loadEvenements();
    }

    private void loadCategoriesMap() {
        List<Categorie> categories = categorieService.getAll();
        for (Categorie categorie : categories) {
            categoriesMap.put(categorie.getIdCate(), categorie);
        }
    }

    @FXML
    private void handleAddAction() {
        openEvenementForm(null);
    }

    public void loadEvenements() {
        evenementsContainer.getChildren().clear();
        List<Evenement> evenements = evenementService.getAll();

        for (Evenement evenement : evenements) {
            evenementsContainer.getChildren().add(createEvenementCard(evenement));
        }
    }

    private VBox createEvenementCard(Evenement evenement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(320);

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Create header with status badge
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Create status badge
        Label statusBadge = new Label(evenement.getStatus());
        statusBadge.getStyleClass().add("card-badge");
        if ("Actif".equals(evenement.getStatus())) {
            statusBadge.setStyle("-fx-background-color: linear-gradient(to right, #4daaff, #4dffaa);");
        } else {
            statusBadge.setStyle("-fx-background-color: linear-gradient(to right, #ff4d4d, #ff4da6);");
        }

        headerBox.getChildren().add(statusBadge);

        // Title of event
        Label lblTitre = new Label(evenement.getTitre());
        lblTitre.getStyleClass().add("card-title");
        lblTitre.setWrapText(true);

        // Get category name
        String categoryName = "Catégorie inconnue";
        Categorie categorie = categoriesMap.get(evenement.getCategorie());
        if (categorie != null) {
            categoryName = categorie.getType();
        }

        Label lblCategorie = new Label(categoryName);
        lblCategorie.getStyleClass().add("card-subtitle");

        // Create date info with icon using Ikonli
        HBox dateBox = new HBox(5);
        dateBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon calendarIcon = new FontIcon(FontAwesomeSolid.CALENDAR_ALT);
        calendarIcon.setIconSize(16);
        calendarIcon.setIconColor(javafx.scene.paint.Color.valueOf("#9b4dff"));
        dateBox.getChildren().add(calendarIcon);

        Label lblDates = new Label("Du " + evenement.getDateDebut().format(formatter) +
                " au " + evenement.getDateFin().format(formatter));
        lblDates.getStyleClass().add("card-text");
        dateBox.getChildren().add(lblDates);

        // Create location info with icon using Ikonli
        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon locationIcon = new FontIcon(FontAwesomeSolid.MAP_MARKER_ALT);
        locationIcon.setIconSize(16);
        locationIcon.setIconColor(javafx.scene.paint.Color.valueOf("#ff3377"));
        locationBox.getChildren().add(locationIcon);

        Label lblLieu = new Label(evenement.getLieu());
        lblLieu.getStyleClass().add("card-text");
        lblLieu.setWrapText(true);
        locationBox.getChildren().add(lblLieu);

        // Description with max length
        String shortDescription = evenement.getDescription();
        if (shortDescription.length() > 100) {
            shortDescription = shortDescription.substring(0, 97) + "...";
        }

        Label lblDescription = new Label(shortDescription);
        lblDescription.getStyleClass().add("card-text");
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(300);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox buttonBar = new HBox(10);

        // Bouton "Voir" avec icône
        Button btnView = new Button("Voir");
        btnView.getStyleClass().add("view-btn");
        FontIcon viewIcon = new FontIcon(FontAwesomeSolid.EYE);
        viewIcon.setIconSize(14);
        btnView.setGraphic(viewIcon);
        btnView.setGraphicTextGap(5);
        btnView.setOnAction(e -> showEventDetails(evenement));

        // Bouton "Modifier" avec icône
        Button btnEdit = new Button("Modifier");
        btnEdit.getStyleClass().add("edit-btn");
        FontIcon editIcon = new FontIcon(FontAwesomeSolid.EDIT);
        editIcon.setIconSize(14);
        btnEdit.setGraphic(editIcon);
        btnEdit.setGraphicTextGap(5);
        btnEdit.setOnAction(e -> openEvenementForm(evenement));

        // Bouton "Supprimer" avec icône
        Button btnDelete = new Button("Supprimer");
        btnDelete.getStyleClass().add("delete-btn");
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
        deleteIcon.setIconSize(14);
        btnDelete.setGraphic(deleteIcon);
        btnDelete.setGraphicTextGap(5);
        btnDelete.setOnAction(e -> handleDeleteAction(evenement));

        buttonBar.getChildren().addAll(btnView, btnEdit, btnDelete);

        // Add all components to card
        card.getChildren().addAll(
                headerBox, lblTitre, lblCategorie, dateBox, locationBox,
                lblDescription, spacer, buttonBar);

        return card;
    }

    private void openEvenementForm(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/event_project/AdminView/EvenementForm.fxml"));
            Parent root = loader.load();

            EvenementFormController controller = loader.getController();
            if (evenement != null) {
                controller.setEvenement(evenement);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(evenement == null ? "Ajouter un Événement" : "Modifier un Événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh list after form is closed
            loadEvenements();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteAction(Evenement evenement) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet événement ?");

        ButtonType buttonTypeOui = new ButtonType("Oui");
        ButtonType buttonTypeNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmAlert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOui) {
                if (evenementService.delete(evenement.getIdEven())) {
                    loadEvenements();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Impossible de supprimer cet événement.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    // Méthode pour afficher les détails d'un événement
    private void showEventDetails(Evenement evenement) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails de l'événement");
        dialog.setHeaderText(evenement.getTitre());
        dialog.getDialogPane().getStyleClass().add("map-dialog");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/org/example/event_project/css/admin.css").toExternalForm());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Get category name
        String categoryName = "Catégorie inconnue";
        Categorie categorie = categoriesMap.get(evenement.getCategorie());
        if (categorie != null) {
            categoryName = categorie.getType();
        }

        // Add information sections
        Label lblID = new Label("ID: " + evenement.getIdEven());
        lblID.setStyle("-fx-font-weight: bold;");

        Label lblCategorie = new Label("Catégorie: " + categoryName);
        lblCategorie.setStyle("-fx-font-weight: bold; -fx-text-fill: #9b4dff;");

        // HBox pour les dates avec icône
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon calendarIcon = new FontIcon(FontAwesomeSolid.CALENDAR_ALT);
        calendarIcon.setIconSize(16);
        calendarIcon.setIconColor(javafx.scene.paint.Color.valueOf("#9b4dff"));
        Label lblDates = new Label("Période: Du " + evenement.getDateDebut().format(formatter) +
                " au " + evenement.getDateFin().format(formatter));
        dateBox.getChildren().addAll(calendarIcon, lblDates);

        // HBox pour la date de création avec icône
        HBox creationDateBox = new HBox(8);
        creationDateBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon creationIcon = new FontIcon(MaterialDesignC.CLOCK);
        creationIcon.setIconSize(16);
        creationIcon.setIconColor(javafx.scene.paint.Color.valueOf("#9b4dff"));
        Label lblCreationDate = new Label("Date de création: " + evenement.getDateCreation().format(formatter));
        creationDateBox.getChildren().addAll(creationIcon, lblCreationDate);

        // HBox pour le lieu avec icône
        HBox lieuBox = new HBox(8);
        lieuBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon locationIcon = new FontIcon(FontAwesomeSolid.MAP_MARKER_ALT);
        locationIcon.setIconSize(16);
        locationIcon.setIconColor(javafx.scene.paint.Color.valueOf("#ff3377"));
        Label lblLieu = new Label("Lieu: " + evenement.getLieu());
        lieuBox.getChildren().addAll(locationIcon, lblLieu);

        // HBox pour le statut avec icône
        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        FontIcon statusIcon = new FontIcon(
                evenement.getStatus().equals("Actif") ? MaterialDesignM.MEDAL : MaterialDesignL.LOCK);
        statusIcon.setIconSize(16);
        statusIcon.setIconColor(
                evenement.getStatus().equals("Actif") ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
        Label lblStatus = new Label("Statut: " + evenement.getStatus());
        lblStatus.setStyle(evenement.getStatus().equals("Actif") ? "-fx-text-fill: green; -fx-font-weight: bold;"
                : "-fx-text-fill: red; -fx-font-weight: bold;");
        statusBox.getChildren().addAll(statusIcon, lblStatus);

        Label lblDescriptionTitle = new Label("Description");
        lblDescriptionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblDescription = new Label(evenement.getDescription());
        lblDescription.setWrapText(true);

        // Bouton voir sur la carte avec icône
        Button btnViewMap = new Button("Voir sur la carte");
        btnViewMap.setStyle(
                "-fx-background-color: linear-gradient(to right, #4daaff, #4dffaa); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-padding: 8 15; -fx-background-radius: 20;");
        FontIcon mapIcon = new FontIcon(FontAwesomeSolid.MAP);
        mapIcon.setIconSize(16);
        btnViewMap.setGraphic(mapIcon);
        btnViewMap.setGraphicTextGap(8);
        btnViewMap.setOnAction(e -> showLocationOnMap(evenement.getLieu()));

        content.getChildren().addAll(
                lblID, lblCategorie, dateBox, creationDateBox, lieuBox,
                statusBox, btnViewMap, lblDescriptionTitle, lblDescription);

        dialog.getDialogPane().setContent(content);

        ButtonType buttonTypeOk = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.showAndWait();
    }

    private void showLocationOnMap(String location) {
        Stage mapStage = new Stage();
        mapStage.initModality(Modality.APPLICATION_MODAL);
        mapStage.setTitle("Localisation: " + location);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        String mapHtml = createViewMapHtml(location);
        webEngine.loadContent(mapHtml);

        mapStage.setScene(new Scene(webView, 800, 600));
        mapStage.show();
    }

    private String createViewMapHtml(String address) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Localisation</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; }\n" +
                "        #map { width: 100%; height: 100vh; }\n" +
                "        .location-title { position: absolute; top: 10px; left: 50%; transform: translateX(-50%); z-index: 999; background-color: white; padding: 10px 20px; border-radius: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }\n"
                +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"location-title\">" + address.replace("'", "\\'") + "</div>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        let map = L.map('map').setView([33.8869, 9.5375], 6);\n" +
                "        let addressToFind = '" + address.replace("'", "\\'") + "';\n" +
                "\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n"
                +
                "        }).addTo(map);\n" +
                "\n" +
                "        // Search for the location\n" +
                "        fetch(`https://geocode.xyz/${encodeURIComponent(addressToFind)}?json=1&auth=516978462101196299454x78616`)\n"
                +
                "            .then(response => response.json())\n" +
                "            .then(data => {\n" +
                "                if (data.latt && data.longt) {\n" +
                "                    let lat = parseFloat(data.latt);\n" +
                "                    let lng = parseFloat(data.longt);\n" +
                "                    \n" +
                "                    if (!isNaN(lat) && !isNaN(lng)) {\n" +
                "                        // Add marker\n" +
                "                        let marker = L.marker([lat, lng]).addTo(map);\n" +
                "                        marker.bindPopup(addressToFind).openPopup();\n" +
                "                        \n" +
                "                        // Center map on location\n" +
                "                        map.setView([lat, lng], 15);\n" +
                "                    } else {\n" +
                "                        alert('Coordonnées du lieu non trouvées');\n" +
                "                    }\n" +
                "                } else {\n" +
                "                    alert('Lieu non trouvé');\n" +
                "                }\n" +
                "            })\n" +
                "            .catch(error => {\n" +
                "                console.error('Error searching address:', error);\n" +
                "                alert('Erreur lors de la recherche du lieu');\n" +
                "            });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    @FXML
    private void handleExportAction() {
        try {
            List<Evenement> evenements = evenementService.getAll();
            File htmlFile = org.example.event_project.Utils.PdfExportService.exportEventsToHtml(evenements, categoriesMap);
            if (htmlFile != null && htmlFile.exists()) {
                // Ouvrir le fichier avec le navigateur par défaut
                java.awt.Desktop.getDesktop().browse(htmlFile.toURI());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText(null);
                alert.setContentText("La liste des événements a été exportée avec succès.\n" +
                        "Le fichier s'ouvrira dans votre navigateur par défaut.");
                alert.showAndWait();
            } else {
                throw new Exception("Erreur lors de la création du fichier d'export");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'exportation");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'exportation des événements.");
            alert.showAndWait();
        }
    }
}
