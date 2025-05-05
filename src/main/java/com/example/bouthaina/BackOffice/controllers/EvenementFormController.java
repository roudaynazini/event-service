package com.example.bouthaina.BackOffice.controllers;

import com.example.bouthaina.BackOffice.models.Categorie;
import com.example.bouthaina.BackOffice.models.Evenement;
import com.example.bouthaina.BackOffice.services.CategorieService;
import com.example.bouthaina.BackOffice.services.EvenementService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.web.WebEngine;
import javafx.stage.Modality;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

public class EvenementFormController implements Initializable {
    @FXML
    private TextField titreField;

    @FXML
    private ComboBox<Categorie> categorieComboBox;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField lieuField;

    @FXML
    private Button mapButton;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    private final String API_KEY = "516978462101196299454x78616";
    private final String GEOCODE_URL = "https://geocode.xyz/";

    private EvenementService evenementService;
    private CategorieService categorieService;
    private Evenement currentEvenement;
    private boolean isEditMode = false;
    private Map<String, Integer> categorieMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        evenementService = new EvenementService();
        categorieService = new CategorieService();

        // Initialize dateDebutPicker with current date
        dateDebutPicker.setValue(LocalDate.now());

        // Initialize dateFinPicker with one day after current date
        dateFinPicker.setValue(LocalDate.now().plusDays(1));

        // Add listeners for date validation
        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateDateRange();
        });

        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateDateRange();
        });

        // Initialize statusComboBox
        statusComboBox.setItems(FXCollections.observableArrayList("Actif", "Inactif"));
        statusComboBox.getSelectionModel().selectFirst();

        // Load categories in ComboBox
        loadCategories();
    }

    private void validateDateRange() {
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut != null && dateFin != null) {
            if (dateFin.isBefore(dateDebut)) {
                // Set date fin to date début + 1 day automatically
                dateFinPicker.setValue(dateDebut.plusDays(1));

                // Show warning to user
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation des dates");
                alert.setHeaderText(null);
                alert.setContentText(
                        "La date de fin ne peut pas être avant la date de début. La date de fin a été ajustée automatiquement.");
                alert.showAndWait();
            }
        }
    }

    private void loadCategories() {
        List<Categorie> categories = categorieService.getAll();
        categorieComboBox.setItems(FXCollections.observableArrayList(categories));

        // Set converter to display the category type in ComboBox
        categorieComboBox.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie != null ? categorie.getType() : "";
            }

            @Override
            public Categorie fromString(String string) {
                return null; // Not needed for ComboBox
            }
        });

        if (!categories.isEmpty()) {
            categorieComboBox.getSelectionModel().selectFirst();
        }
    }

    public void setEvenement(Evenement evenement) {
        this.currentEvenement = evenement;
        this.isEditMode = true;

        // Fill the form with the event data
        titreField.setText(evenement.getTitre());
        dateDebutPicker.setValue(evenement.getDateDebut());
        dateFinPicker.setValue(evenement.getDateFin());
        lieuField.setText(evenement.getLieu());
        descriptionField.setText(evenement.getDescription());
        statusComboBox.setValue(evenement.getStatus());

        // Select the correct category
        for (Categorie categorie : categorieComboBox.getItems()) {
            if (categorie.getIdCate() == evenement.getCategorie()) {
                categorieComboBox.getSelectionModel().select(categorie);
                break;
            }
        }
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

        String titre = titreField.getText();
        LocalDate dateCreation = LocalDate.now(); // Current date for new events
        Categorie selectedCategorie = categorieComboBox.getValue();
        int categorieId = selectedCategorie.getIdCate();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        String lieu = lieuField.getText();
        String description = descriptionField.getText();
        String status = statusComboBox.getValue();

        boolean success;

        if (isEditMode) {
            currentEvenement.setTitre(titre);
            // Keep original creation date for edits
            currentEvenement.setCategorie(categorieId);
            currentEvenement.setDateDebut(dateDebut);
            currentEvenement.setDateFin(dateFin);
            currentEvenement.setLieu(lieu);
            currentEvenement.setDescription(description);
            currentEvenement.setStatus(status);
            success = evenementService.update(currentEvenement);
        } else {
            Evenement newEvenement = new Evenement(
                    titre, dateCreation, categorieId, dateDebut, dateFin, description, status, lieu);
            success = evenementService.add(newEvenement);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    isEditMode ? "Événement mis à jour avec succès" : "Événement ajouté avec succès");
            closeForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite lors de l'enregistrement");
        }
    }

    @FXML
    private void handleOpenMap() {
        Stage mapStage = new Stage();
        mapStage.initModality(Modality.APPLICATION_MODAL);
        mapStage.setTitle("Sélectionner un lieu");

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Load HTML content with Leaflet map
        String mapHtml = createMapHtml();
        webEngine.loadContent(mapHtml);

        // Add event listener to receive location from JS
        webEngine.setOnAlert(event -> {
            String data = event.getData();
            if (data.startsWith("LOCATION:")) {
                String[] parts = data.substring(9).split("\\|");
                if (parts.length >= 3) {
                    String address = parts[0];
                    lieuField.setText(address);
                    mapStage.close();
                }
            }
        });

        // Search current location if available
        if (!lieuField.getText().isEmpty()) {
            String location = lieuField.getText();
            CompletableFuture.runAsync(() -> {
                try {
                    geocodeAddress(location, webEngine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        mapStage.setScene(new javafx.scene.Scene(webView, 800, 600));
        mapStage.showAndWait();
    }

    private String createMapHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Carte de sélection de lieu</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; }\n" +
                "        #map { width: 100%; height: 100vh; }\n" +
                "        #search-container { position: absolute; top: 10px; left: 50px; right: 10px; z-index: 1000; }\n"
                +
                "        #address-search { width: 60%; padding: 8px; border-radius: 4px; border: 1px solid #ccc; }\n" +
                "        #search-btn { padding: 8px 15px; margin-left: 10px; cursor: pointer; background: linear-gradient(to right, #9b4dff, #ff3377); color: white; border: none; border-radius: 4px; }\n"
                +
                "        #select-btn { padding: 8px 15px; margin-left: 10px; cursor: pointer; background: linear-gradient(to right, #4daaff, #4dffaa); color: white; border: none; border-radius: 4px; }\n"
                +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"search-container\">\n" +
                "        <input type=\"text\" id=\"address-search\" placeholder=\"Rechercher une adresse\">\n" +
                "        <button id=\"search-btn\">Rechercher</button>\n" +
                "        <button id=\"select-btn\">Sélectionner ce lieu</button>\n" +
                "    </div>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        let map = L.map('map').setView([33.8869, 9.5375], 6);\n" +
                "        let currentMarker = null;\n" +
                "        let currentAddress = '';\n" +
                "        let currentLat = 0;\n" +
                "        let currentLng = 0;\n" +
                "\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n"
                +
                "        }).addTo(map);\n" +
                "\n" +
                "        function updateMarker(lat, lng, address) {\n" +
                "            if (currentMarker) {\n" +
                "                map.removeLayer(currentMarker);\n" +
                "            }\n" +
                "            currentMarker = L.marker([lat, lng]).addTo(map);\n" +
                "            currentMarker.bindPopup(address).openPopup();\n" +
                "            currentLat = lat;\n" +
                "            currentLng = lng;\n" +
                "            currentAddress = address;\n" +
                "            map.setView([lat, lng], 15);\n" +
                "        }\n" +
                "\n" +
                "        // Handle map click\n" +
                "        map.on('click', function(e) {\n" +
                "            let lat = e.latlng.lat;\n" +
                "            let lng = e.latlng.lng;\n" +
                "            \n" +
                "            fetch(`https://geocode.xyz/${lat},${lng}?json=1&auth=516978462101196299454x78616`)\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    let address = '';\n" +
                "                    if (data.staddress) address += data.staddress + ', ';\n" +
                "                    if (data.city) address += data.city + ', ';\n" +
                "                    if (data.country) address += data.country;\n" +
                "                    if (address === '') address = `Latitude: ${lat}, Longitude: ${lng}`;\n" +
                "                    \n" +
                "                    updateMarker(lat, lng, address);\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('Error fetching address:', error);\n" +
                "                    updateMarker(lat, lng, `Latitude: ${lat}, Longitude: ${lng}`);\n" +
                "                });\n" +
                "        });\n" +
                "\n" +
                "        // Handle search button click\n" +
                "        document.getElementById('search-btn').addEventListener('click', function() {\n" +
                "            let address = document.getElementById('address-search').value;\n" +
                "            if (address.trim() !== '') {\n" +
                "                searchAddress(address);\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        // Handle Enter key in search input\n" +
                "        document.getElementById('address-search').addEventListener('keypress', function(e) {\n" +
                "            if (e.key === 'Enter') {\n" +
                "                let address = document.getElementById('address-search').value;\n" +
                "                if (address.trim() !== '') {\n" +
                "                    searchAddress(address);\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        // Handle select button click\n" +
                "        document.getElementById('select-btn').addEventListener('click', function() {\n" +
                "            if (currentMarker) {\n" +
                "                alert('LOCATION:' + currentAddress + '|' + currentLat + '|' + currentLng);\n" +
                "            } else {\n" +
                "                alert('Veuillez sélectionner un lieu sur la carte d\\'abord');\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        function searchAddress(address) {\n" +
                "            fetch(`https://geocode.xyz/${encodeURIComponent(address)}?json=1&auth=516978462101196299454x78616`)\n"
                +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    if (data.latt && data.longt) {\n" +
                "                        let lat = parseFloat(data.latt);\n" +
                "                        let lng = parseFloat(data.longt);\n" +
                "                        \n" +
                "                        if (!isNaN(lat) && !isNaN(lng)) {\n" +
                "                            updateMarker(lat, lng, address);\n" +
                "                        } else {\n" +
                "                            alert('Lieu non trouvé');\n" +
                "                        }\n" +
                "                    } else {\n" +
                "                        alert('Lieu non trouvé');\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('Error searching address:', error);\n" +
                "                    alert('Erreur lors de la recherche');\n" +
                "                });\n" +
                "        }\n" +
                "\n" +
                "        // Function to be called from Java\n" +
                "        function setLocationFromJava(address, lat, lng) {\n" +
                "            document.getElementById('address-search').value = address;\n" +
                "            if (lat && lng) {\n" +
                "                updateMarker(lat, lng, address);\n" +
                "            } else {\n" +
                "                searchAddress(address);\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private void geocodeAddress(String address, WebEngine webEngine) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            URL url = new URL(GEOCODE_URL + encodedAddress + "?json=1&auth=" + API_KEY);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            if (jsonResponse.has("latt") && jsonResponse.has("longt")) {
                double lat = Double.parseDouble(jsonResponse.getString("latt"));
                double lng = Double.parseDouble(jsonResponse.getString("longt"));

                // Call JavaScript function to set the location on the map
                webEngine.executeScript(
                        "setLocationFromJava('" + address.replace("'", "\\'") + "', " + lat + ", " + lng + ");");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (titreField.getText().isEmpty()) {
            errorMessage.append("- Le titre est requis\n");
        }

        if (categorieComboBox.getValue() == null) {
            errorMessage.append("- La catégorie est requise\n");
        }

        if (dateDebutPicker.getValue() == null) {
            errorMessage.append("- La date de début est requise\n");
        }

        if (dateFinPicker.getValue() == null) {
            errorMessage.append("- La date de fin est requise\n");
        } else if (dateDebutPicker.getValue() != null
                && dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            errorMessage.append("- La date de fin doit être après la date de début\n");
        }

        if (lieuField.getText().isEmpty()) {
            errorMessage.append("- Le lieu est requis\n");
        }

        if (descriptionField.getText().isEmpty()) {
            errorMessage.append("- La description est requise\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage.toString());
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
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
