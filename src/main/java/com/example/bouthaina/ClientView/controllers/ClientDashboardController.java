package com.example.bouthaina.ClientView.controllers;

import com.example.bouthaina.BackOffice.models.Categorie;
import com.example.bouthaina.BackOffice.models.Evenement;
import com.example.bouthaina.BackOffice.services.CategorieService;
import com.example.bouthaina.BackOffice.services.EvenementService;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ClientDashboardController implements Initializable {
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Categorie> filterComboBox;

    @FXML
    private FlowPane evenementsContainer;

    private EvenementService evenementService;
    private CategorieService categorieService;
    private Map<Integer, Categorie> categoriesMap = new HashMap<>();
    private List<Evenement> allEvenements;
    private Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        evenementService = new EvenementService();
        categorieService = new CategorieService();

        // Create search icon
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(Color.web("#ff8a00"));

        // Create a search container using an HBox
        HBox searchContainer = new HBox(5);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPadding(new Insets(0, 10, 0, 10));

        // Configure the text field
        searchField.setPromptText("Rechercher un événement...");
        searchField.getStyleClass().add("search-box");
        searchField.setStyle("-fx-padding: 5 5 5 30; -fx-background-insets: 0;");

        // Add the icon and place it in position
        StackPane iconContainer = new StackPane(searchIcon);
        iconContainer.setPadding(new Insets(0, 5, 0, 5));
        iconContainer.setPickOnBounds(false); // Allow clicks to go through

        // Replace search field with container (get its parent and index)
        if (searchField.getParent() != null) {
            Parent parent = searchField.getParent();
            int index = -1;

            if (parent instanceof Pane) {
                Pane pane = (Pane) parent;
                index = pane.getChildren().indexOf(searchField);
                if (index >= 0) {
                    pane.getChildren().remove(searchField);

                    // Add icon and text field to container, then add container to parent
                    searchContainer.getChildren().addAll(iconContainer, searchField);
                    searchContainer.getStyleClass().add("search-container");
                    pane.getChildren().add(index, searchContainer);
                }
            }
        }

        // Style the ComboBox
        filterComboBox.getStyleClass().add("filter-combo");

        // Load categories into map and combobox
        loadCategoriesMap();

        // Setup search functionality
        setupSearch();

        // Load events with animation
        loadEvenementsWithAnimation();
    }

    private void loadCategoriesMap() {
        List<Categorie> categories = categorieService.getAll();

        // Add "Toutes les catégories" option
        Categorie allCategories = new Categorie(0, "Toutes les catégories", "", "");

        // Create ObservableList for ComboBox
        ObservableList<Categorie> categoriesList = FXCollections.observableArrayList();
        categoriesList.add(allCategories);
        categoriesList.addAll(categories);

        // Set items in ComboBox
        filterComboBox.setItems(categoriesList);

        // Set converter to display category type
        filterComboBox.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie != null ? categorie.getType() : "";
            }

            @Override
            public Categorie fromString(String string) {
                return null;
            }
        });

        // Select "All categories" by default
        filterComboBox.getSelectionModel().selectFirst();

        // Add categories to map for quick lookup
        for (Categorie categorie : categories) {
            categoriesMap.put(categorie.getIdCate(), categorie);
        }

        // Add filter listener
        filterComboBox.setOnAction(event -> filterEvenements());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvenements();
        });
    }

    public void loadEvenements() {
        allEvenements = evenementService.getAll();
        filterEvenements();
    }

    private void loadEvenementsWithAnimation() {
        allEvenements = evenementService.getAll();
        filterEvenementsWithAnimation();
    }

    private void filterEvenements() {
        String searchText = searchField.getText().toLowerCase();
        Categorie selectedCategorie = filterComboBox.getValue();

        List<Evenement> filteredEvenements = allEvenements.stream()
                .filter(e -> e.getStatus().equals("Actif")) // Only show active events
                .filter(e -> e.getDateFin().isAfter(LocalDate.now())) // Only show upcoming/current events
                .filter(e -> {
                    // Filter by category if not "All categories"
                    if (selectedCategorie != null && selectedCategorie.getIdCate() != 0) {
                        return e.getCategorie() == selectedCategorie.getIdCate();
                    }
                    return true;
                })
                .filter(e -> {
                    // Filter by search text
                    if (!searchText.isEmpty()) {
                        return e.getTitre().toLowerCase().contains(searchText) ||
                                e.getLieu().toLowerCase().contains(searchText) ||
                                e.getDescription().toLowerCase().contains(searchText);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        displayEvenements(filteredEvenements);
    }

    private void filterEvenementsWithAnimation() {
        String searchText = searchField.getText().toLowerCase();
        Categorie selectedCategorie = filterComboBox.getValue();

        List<Evenement> filteredEvenements = allEvenements.stream()
                .filter(e -> e.getStatus().equals("Actif")) // Only show active events
                .filter(e -> e.getDateFin().isAfter(LocalDate.now())) // Only show upcoming/current events
                .filter(e -> {
                    // Filter by category if not "All categories"
                    if (selectedCategorie != null && selectedCategorie.getIdCate() != 0) {
                        return e.getCategorie() == selectedCategorie.getIdCate();
                    }
                    return true;
                })
                .filter(e -> {
                    // Filter by search text
                    if (!searchText.isEmpty()) {
                        return e.getTitre().toLowerCase().contains(searchText) ||
                                e.getLieu().toLowerCase().contains(searchText) ||
                                e.getDescription().toLowerCase().contains(searchText);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        displayEvenementsWithAnimation(filteredEvenements);
    }

    private void displayEvenements(List<Evenement> evenements) {
        evenementsContainer.getChildren().clear();

        if (evenements.isEmpty()) {
            VBox noEventsBox = new VBox(10);
            noEventsBox.setAlignment(Pos.CENTER);

            FontIcon icon = new FontIcon(FontAwesomeSolid.CALENDAR_TIMES);
            icon.setIconSize(50);
            icon.setIconColor(Color.web("#ff8a00"));

            Label noEventsLabel = new Label("Aucun événement ne correspond à votre recherche");
            noEventsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");

            noEventsBox.getChildren().addAll(icon, noEventsLabel);
            evenementsContainer.getChildren().add(noEventsBox);
            return;
        }

        for (Evenement evenement : evenements) {
            evenementsContainer.getChildren().add(createEvenementCard(evenement));
        }
    }

    private void displayEvenementsWithAnimation(List<Evenement> evenements) {
        evenementsContainer.getChildren().clear();

        if (evenements.isEmpty()) {
            VBox noEventsBox = new VBox(10);
            noEventsBox.setAlignment(Pos.CENTER);

            FontIcon icon = new FontIcon(FontAwesomeSolid.CALENDAR_TIMES);
            icon.setIconSize(50);
            icon.setIconColor(Color.web("#ff8a00"));

            Label noEventsLabel = new Label("Aucun événement ne correspond à votre recherche");
            noEventsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");

            noEventsBox.getChildren().addAll(icon, noEventsLabel);
            evenementsContainer.getChildren().add(noEventsBox);

            // Animation pour l'icône
            RotateTransition rotate = new RotateTransition(Duration.seconds(1), icon);
            rotate.setFromAngle(0);
            rotate.setToAngle(360);
            rotate.setCycleCount(1);
            rotate.setInterpolator(Interpolator.EASE_OUT);
            rotate.play();

            return;
        }

        // Ajouter avec animation séquentielle
        for (int i = 0; i < evenements.size(); i++) {
            VBox card = createEvenementCard(evenements.get(i));
            card.setOpacity(0);
            evenementsContainer.getChildren().add(card);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), card);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDelay(Duration.millis(i * 100)); // Délai progressif
            fadeIn.play();

            TranslateTransition translateY = new TranslateTransition(Duration.millis(400), card);
            translateY.setFromY(20);
            translateY.setToY(0);
            translateY.setDelay(Duration.millis(i * 100));
            translateY.play();
        }
    }

    private VBox createEvenementCard(Evenement evenement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("event-card");
        card.setPrefWidth(320);

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Créer une barre d'en-tête avec les badges
        HBox headerBox = new HBox(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Badge de statut (à venir, en cours)
        Label statusBadge = new Label();
        statusBadge.getStyleClass().addAll("event-badge");

        if (evenement.getDateDebut().isAfter(LocalDate.now())) {
            statusBadge.setText("À VENIR");
            statusBadge.getStyleClass().add("event-coming-badge");

            // Ajouter un compte à rebours pour les événements proches
            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), evenement.getDateDebut());
            if (daysUntil <= 30) {
                statusBadge.setText("J-" + daysUntil);
            }
        } else {
            statusBadge.setText("EN COURS");
            statusBadge.getStyleClass().add("event-live-badge");
        }

        headerBox.getChildren().add(statusBadge);

        // Ajouter un badge "populaire" aléatoirement
        if (random.nextInt(3) == 0) { // 1 chance sur 3
            Label popularBadge = new Label("POPULAIRE");
            popularBadge.getStyleClass().addAll("event-badge", "event-popular-badge");
            headerBox.getChildren().add(popularBadge);
        }

        Label lblTitre = new Label(evenement.getTitre());
        lblTitre.getStyleClass().add("event-title");
        lblTitre.setWrapText(true);

        // Get category name
        String categoryName = "Catégorie inconnue";
        Categorie categorie = categoriesMap.get(evenement.getCategorie());
        if (categorie != null) {
            categoryName = categorie.getType();
        }

        // HBox pour la catégorie avec icône
        HBox categoryBox = new HBox(5);
        categoryBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon categoryIcon = new FontIcon(FontAwesomeSolid.TAG);
        categoryIcon.setIconSize(14);
        categoryIcon.setIconColor(Color.web("#ff8a00"));

        Label lblCategorie = new Label(categoryName);
        lblCategorie.getStyleClass().add("event-category");

        categoryBox.getChildren().addAll(categoryIcon, lblCategorie);

        // HBox pour les dates avec icône
        HBox dateBox = new HBox(5);
        dateBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon calendarIcon = new FontIcon(FontAwesomeSolid.CALENDAR_ALT);
        calendarIcon.setIconSize(14);
        calendarIcon.setIconColor(Color.web("#ff8a00"));

        Label lblDates = new Label("Du " + evenement.getDateDebut().format(formatter) +
                " au " + evenement.getDateFin().format(formatter));
        lblDates.getStyleClass().add("event-date");

        dateBox.getChildren().addAll(calendarIcon, lblDates);

        // HBox pour le lieu avec icône
        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon locationIcon = new FontIcon(FontAwesomeSolid.MAP_MARKER_ALT);
        locationIcon.setIconSize(14);
        locationIcon.setIconColor(Color.web("#ff8a00"));

        Label lblLieu = new Label(evenement.getLieu());
        lblLieu.getStyleClass().add("event-location");
        lblLieu.setWrapText(true);

        locationBox.getChildren().addAll(locationIcon, lblLieu);

        // Description avec longueur maximale
        String shortDescription = evenement.getDescription();
        if (shortDescription.length() > 100) {
            shortDescription = shortDescription.substring(0, 97) + "...";
        }

        Label lblDescription = new Label(shortDescription);
        lblDescription.getStyleClass().add("event-description");
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(300);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        // Bouton détails avec icône
        Button btnDetails = new Button("Détails");
        btnDetails.getStyleClass().add("details-btn");
        FontIcon detailsIcon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        detailsIcon.setIconSize(14);
        btnDetails.setGraphic(detailsIcon);
        btnDetails.setGraphicTextGap(5);
        btnDetails.setOnAction(e -> showEventDetails(evenement));

        // Bouton inscription avec icône
        Button btnRegister = new Button("S'inscrire");
        btnRegister.getStyleClass().add("register-btn");
        FontIcon registerIcon = new FontIcon(FontAwesomeSolid.USER_PLUS);
        registerIcon.setIconSize(14);
        btnRegister.setGraphic(registerIcon);
        btnRegister.setGraphicTextGap(5);
        btnRegister.setOnAction(e -> registerForEvent(evenement));

        buttonBar.getChildren().addAll(btnDetails, btnRegister);

        // Séparateur horizontal avant les boutons
        Region divider = new Region();
        divider.getStyleClass().add("divider");
        divider.setPrefHeight(2);
        divider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(divider, Priority.ALWAYS);

        card.getChildren().addAll(
                headerBox,
                lblTitre,
                categoryBox,
                dateBox,
                locationBox,
                lblDescription,
                spacer,
                divider,
                buttonBar);

        return card;
    }

    private void showEventDetails(Evenement evenement) {
        // Créer une fenêtre personnalisée au lieu d'une boîte de dialogue standard
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.initStyle(StageStyle.UNDECORATED);

        // Conteneur principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("event-details-dialog");

        // En-tête avec titre et bouton de fermeture
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(evenement.getTitre());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button closeBtn = new Button();
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.TIMES);
        closeIcon.setIconSize(16);
        closeIcon.setIconColor(Color.WHITE);
        closeBtn.setGraphic(closeIcon);
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> detailsStage.close());

        header.getChildren().addAll(title, headerSpacer, closeBtn);
        header.setPadding(new Insets(15));

        // Contenu principal
        ScrollPane scrollContent = new ScrollPane();
        scrollContent.setFitToWidth(true);
        scrollContent.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollContent.getStyleClass().add("transparent-scroll-pane");

        VBox content = new VBox(15);
        content.getStyleClass().add("content");
        content.setPadding(new Insets(20));

        // Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Bannière de l'événement avec effet de dégradé
        VBox eventBanner = new VBox(5);
        eventBanner.setStyle(
                "-fx-background-color: linear-gradient(to right, #ff8a0022, #ff525222); -fx-background-radius: 10; -fx-padding: 15;");

        // Get category name
        String categoryName = "Catégorie inconnue";
        Categorie categorie = categoriesMap.get(evenement.getCategorie());
        if (categorie != null) {
            categoryName = categorie.getType();
        }

        // Section Catégorie
        HBox categorySection = new HBox(10);
        categorySection.setAlignment(Pos.CENTER_LEFT);

        FontIcon categoryIcon = new FontIcon(FontAwesomeSolid.TAG);
        categoryIcon.setIconSize(16);
        categoryIcon.setIconColor(Color.web("#ff8a00"));

        Label lblCategorie = new Label("Catégorie: " + categoryName);
        lblCategorie.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #ff8a00;");

        categorySection.getChildren().addAll(categoryIcon, lblCategorie);

        // Section Date
        HBox dateSection = new HBox(10);
        dateSection.setAlignment(Pos.CENTER_LEFT);

        FontIcon dateIcon = new FontIcon(FontAwesomeSolid.CALENDAR_ALT);
        dateIcon.setIconSize(16);
        dateIcon.setIconColor(Color.web("#ff8a00"));

        Label lblDates = new Label("Du " + evenement.getDateDebut().format(formatter) +
                " au " + evenement.getDateFin().format(formatter));
        lblDates.setStyle("-fx-font-size: 14px;");

        dateSection.getChildren().addAll(dateIcon, lblDates);

        // Section Lieu
        HBox locationSection = new HBox(10);
        locationSection.setAlignment(Pos.CENTER_LEFT);

        FontIcon locationIcon = new FontIcon(FontAwesomeSolid.MAP_MARKER_ALT);
        locationIcon.setIconSize(16);
        locationIcon.setIconColor(Color.web("#ff8a00"));

        Label lblLieu = new Label(evenement.getLieu());
        lblLieu.setStyle("-fx-font-size: 14px;");

        locationSection.getChildren().addAll(locationIcon, lblLieu);

        // Ajout des sections à la bannière
        eventBanner.getChildren().addAll(categorySection, dateSection, locationSection);

        // Titre de description
        Label lblDescriptionTitle = new Label("Description");
        lblDescriptionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #ff8a00;");

        // Contenu de la description
        Label lblDescription = new Label(evenement.getDescription());
        lblDescription.setWrapText(true);
        lblDescription.setStyle("-fx-font-size: 14px;");

        // Bouton pour voir la carte
        Button btnViewMap = new Button("Voir sur la carte");
        btnViewMap.setStyle(
                "-fx-background-color: linear-gradient(to right, #ff8a00, #ff5252); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-padding: 10 20; -fx-background-radius: 20;");
        FontIcon mapIcon = new FontIcon(FontAwesomeSolid.MAP);
        mapIcon.setIconSize(16);
        btnViewMap.setGraphic(mapIcon);
        btnViewMap.setGraphicTextGap(8);
        btnViewMap.setOnAction(e -> showLocationOnMap(evenement.getLieu()));

        // Section des boutons d'action
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);

        Button btnRegisterFromDetails = new Button("S'inscrire à cet événement");
        btnRegisterFromDetails.getStyleClass().add("register-btn");
        btnRegisterFromDetails.setStyle("-fx-padding: 10 20; -fx-font-size: 14px;");
        FontIcon registerIcon = new FontIcon(FontAwesomeSolid.USER_PLUS);
        registerIcon.setIconSize(16);
        btnRegisterFromDetails.setGraphic(registerIcon);
        btnRegisterFromDetails.setGraphicTextGap(8);
        btnRegisterFromDetails.setOnAction(e -> {
            detailsStage.close();
            registerForEvent(evenement);
        });

        actionButtons.getChildren().add(btnRegisterFromDetails);

        // Ajouter tous les éléments au contenu principal
        content.getChildren().addAll(
                eventBanner,
                new Separator(),
                lblDescriptionTitle,
                lblDescription,
                new Separator(),
                btnViewMap,
                new Separator(),
                actionButtons);

        scrollContent.setContent(content);

        // Configurer le BorderPane
        root.setTop(header);
        root.setCenter(scrollContent);

        // Configurer la scène
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/com/example/bouthaina/css/client.css").toExternalForm());

        // Permettre de déplacer la fenêtre
        final Delta dragDelta = new Delta();
        header.setOnMousePressed(mouseEvent -> {
            dragDelta.x = detailsStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = detailsStage.getY() - mouseEvent.getScreenY();
        });
        header.setOnMouseDragged(mouseEvent -> {
            detailsStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            detailsStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });

        // Effet d'ombre
        root.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

        detailsStage.setScene(scene);

        // Animation d'entrée
        root.setScaleX(0.9);
        root.setScaleY(0.9);
        root.setOpacity(0);

        detailsStage.show();

        Timeline timeline = new Timeline();
        KeyFrame kf1 = new KeyFrame(Duration.millis(0),
                new KeyValue(root.opacityProperty(), 0),
                new KeyValue(root.scaleXProperty(), 0.9),
                new KeyValue(root.scaleYProperty(), 0.9));
        KeyFrame kf2 = new KeyFrame(Duration.millis(250),
                new KeyValue(root.opacityProperty(), 1),
                new KeyValue(root.scaleXProperty(), 1),
                new KeyValue(root.scaleYProperty(), 1));
        timeline.getKeyFrames().addAll(kf1, kf2);
        timeline.play();
    }

    // Classe helper pour le drag and drop
    private static class Delta {
        double x, y;
    }

    private void showLocationOnMap(String location) {
        Stage mapStage = new Stage();
        mapStage.initModality(Modality.APPLICATION_MODAL);
        mapStage.setTitle("Localisation: " + location);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        String mapHtml = createViewMapHtml(location);
        webEngine.loadContent(mapHtml);

        mapStage.setScene(new javafx.scene.Scene(webView, 800, 600));
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
                "        .location-title { position: absolute; top: 10px; left: 50%; transform: translateX(-50%); z-index: 999; background: linear-gradient(to right, #ff8a00, #ff5252); color: white; padding: 10px 20px; border-radius: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.2); font-family: Arial, sans-serif; font-weight: bold; }\n"
                +
                "        .leaflet-popup-content-wrapper { background: linear-gradient(to bottom right, white, #fff6e6); }\n"
                +
                "        .leaflet-popup-tip { background: #ff8a00; }\n" +
                "        .custom-marker { text-align: center; }\n" +
                "        .custom-marker i { color: #ff5252; font-size: 32px; text-shadow: 2px 2px 4px rgba(0,0,0,0.2); }\n"
                +
                "    </style>\n" +
                "    <link href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css\" rel=\"stylesheet\">\n"
                +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"location-title\">" + address.replace("'", "\\'") + "</div>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        let map = L.map('map').setView([33.8869, 9.5375], 6);\n" +
                "        let addressToFind = '" + address.replace("'", "\\'") + "';\n" +
                "\n" +
                "        // Ajouter un style de carte plus coloré\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n"
                +
                "        }).addTo(map);\n" +
                "\n" +
                "        function createCustomIcon() {\n" +
                "            return L.divIcon({\n" +
                "                html: '<div class=\"custom-marker\"><i class=\"fas fa-map-marker-alt\"></i></div>',\n"
                +
                "                className: '',\n" +
                "                iconSize: [40, 40],\n" +
                "                iconAnchor: [20, 40]\n" +
                "            });\n" +
                "        }\n" +
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
                "                        // Add marker with custom icon\n" +
                "                        let customIcon = createCustomIcon();\n" +
                "                        let marker = L.marker([lat, lng], { icon: customIcon }).addTo(map);\n" +
                "                        \n" +
                "                        // Add popup with rich content\n" +
                "                        let popupContent = `\n" +
                "                            <div style=\"text-align:center; padding: 5px;\">\n" +
                "                                <h3 style=\"margin:5px 0; color:#ff8a00;\">${addressToFind}</h3>\n" +
                "                                <p style=\"margin:5px 0;\">Latitude: ${lat.toFixed(5)}, Longitude: ${lng.toFixed(5)}</p>\n"
                +
                "                            </div>\n" +
                "                        `;\n" +
                "                        marker.bindPopup(popupContent).openPopup();\n" +
                "                        \n" +
                "                        // Ajouter un cercle autour du marqueur\n" +
                "                        L.circle([lat, lng], {\n" +
                "                            color: '#ff5252',\n" +
                "                            fillColor: '#ff8a00',\n" +
                "                            fillOpacity: 0.2,\n" +
                "                            radius: 300\n" +
                "                        }).addTo(map);\n" +
                "                        \n" +
                "                        // Center map on location with better zoom\n" +
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

    private void registerForEvent(Evenement evenement) {
        // Créer une fenêtre personnalisée pour l'inscription
        Stage registerStage = new Stage();
        registerStage.initModality(Modality.APPLICATION_MODAL);
        registerStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.getStyleClass().add("success-pane");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        // Titre et icône
        FontIcon confettiIcon = new FontIcon(MaterialDesignP.PARTY_POPPER);
        confettiIcon.setIconSize(60);
        confettiIcon.setIconColor(Color.web("#ff8a00"));

        Label titleLabel = new Label("Confirmation d'inscription");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ff8a00;");

        // Info de l'événement
        VBox eventInfo = new VBox(10);
        eventInfo.setAlignment(Pos.CENTER);

        Label eventTitle = new Label(evenement.getTitre());
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label dateLabel = new Label("Du " + evenement.getDateDebut().format(formatter) +
                " au " + evenement.getDateFin().format(formatter));

        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER);

        FontIcon locationIcon = new FontIcon(FontAwesomeSolid.MAP_MARKER_ALT);
        locationIcon.setIconSize(14);
        locationIcon.setIconColor(Color.web("#ff8a00"));

        Label lieuLabel = new Label(evenement.getLieu());
        locationBox.getChildren().addAll(locationIcon, lieuLabel);

        eventInfo.getChildren().addAll(eventTitle, dateLabel, locationBox);

        // Message de confirmation
        Label confirmationLabel = new Label("Êtes-vous sûr de vouloir vous inscrire à cet événement ?");

        // Boutons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button btnCancel = new Button("Annuler");
        btnCancel.getStyleClass().add("cancel-btn");
        btnCancel.setOnAction(e -> registerStage.close());

        Button btnConfirm = new Button("Confirmer");
        btnConfirm.getStyleClass().add("register-btn");
        FontIcon checkIcon = new FontIcon(FontAwesomeSolid.CHECK);
        checkIcon.setIconSize(14);
        btnConfirm.setGraphic(checkIcon);
        btnConfirm.setGraphicTextGap(5);

        btnConfirm.setOnAction(e -> {
            registerStage.close();
            showSuccessAnimation(evenement);
        });

        buttons.getChildren().addAll(btnCancel, btnConfirm);

        // Ajouter tous les éléments
        root.getChildren().addAll(confettiIcon, titleLabel, eventInfo, confirmationLabel, buttons);

        // Configurer la scène
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/com/example/bouthaina/css/client.css").toExternalForm());

        registerStage.setScene(scene);

        // Animation d'entrée
        root.setScaleX(0.8);
        root.setScaleY(0.8);
        root.setOpacity(0);

        registerStage.show();

        Timeline timeline = new Timeline();
        KeyFrame kf1 = new KeyFrame(Duration.millis(0),
                new KeyValue(root.opacityProperty(), 0),
                new KeyValue(root.scaleXProperty(), 0.8),
                new KeyValue(root.scaleYProperty(), 0.8));
        KeyFrame kf2 = new KeyFrame(Duration.millis(300),
                new KeyValue(root.opacityProperty(), 1),
                new KeyValue(root.scaleXProperty(), 1),
                new KeyValue(root.scaleYProperty(), 1));
        timeline.getKeyFrames().addAll(kf1, kf2);
        timeline.play();
    }

    private void showSuccessAnimation(Evenement evenement) {
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.initStyle(StageStyle.TRANSPARENT);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("success-pane");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Animation de confettis avec WebView
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webView.setPrefHeight(150);

        String confettiHtml = createConfettiHtml();
        webEngine.loadContent(confettiHtml);

        // Message de succès
        Label successTitle = new Label("Félicitations !");
        successTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ff8a00;");

        Label successMessage = new Label("Votre inscription à " + evenement.getTitre() + " a été confirmée !");
        successMessage.getStyleClass().add("success-message");
        successMessage.setWrapText(true);
        successMessage.setTextAlignment(TextAlignment.CENTER);

        Label thankYouMessage = new Label("Merci de votre participation, nous vous attendons avec impatience !");
        thankYouMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        thankYouMessage.setWrapText(true);
        thankYouMessage.setTextAlignment(TextAlignment.CENTER);

        // Bouton de fermeture
        Button btnClose = new Button("Super !");
        btnClose.getStyleClass().add("register-btn");
        btnClose.setOnAction(e -> successStage.close());

        content.getChildren().addAll(webView, successTitle, successMessage, thankYouMessage, btnClose);
        root.setCenter(content);

        // Configurer la scène
        Scene scene = new Scene(root, 500, 500);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/com/example/bouthaina/css/client.css").toExternalForm());

        successStage.setScene(scene);

        // Animation d'entrée
        root.setScaleX(0.7);
        root.setScaleY(0.7);
        root.setOpacity(0);

        successStage.show();

        Timeline timeline = new Timeline();
        KeyFrame kf1 = new KeyFrame(Duration.millis(0),
                new KeyValue(root.opacityProperty(), 0),
                new KeyValue(root.scaleXProperty(), 0.7),
                new KeyValue(root.scaleYProperty(), 0.7));
        KeyFrame kf2 = new KeyFrame(Duration.millis(500),
                new KeyValue(root.opacityProperty(), 1),
                new KeyValue(root.scaleXProperty(), 1),
                new KeyValue(root.scaleYProperty(), 1));
        timeline.getKeyFrames().addAll(kf1, kf2);
        timeline.play();
    }

    private String createConfettiHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Confetti Celebration</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            margin: 0;\n" +
                "            overflow: hidden;\n" +
                "            background: transparent;\n" +
                "        }\n" +
                "        canvas {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            left: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <canvas id=\"confetti-canvas\"></canvas>\n" +
                "    <script>\n" +
                "        // Confetti animation\n" +
                "        const canvas = document.getElementById('confetti-canvas');\n" +
                "        const ctx = canvas.getContext('2d');\n" +
                "        canvas.width = window.innerWidth;\n" +
                "        canvas.height = window.innerHeight;\n" +
                "\n" +
                "        // Couleurs des confettis\n" +
                "        const colors = ['#ff8a00', '#ff5252', '#ffcc80', '#ff9966', '#ffb347', '#ffd700'];\n" +
                "\n" +
                "        // Création des confettis\n" +
                "        const confettiCount = 200;\n" +
                "        const confetti = [];\n" +
                "\n" +
                "        for (let i = 0; i < confettiCount; i++) {\n" +
                "            confetti.push({\n" +
                "                x: Math.random() * canvas.width,\n" +
                "                y: -Math.random() * canvas.height,\n" +
                "                size: Math.random() * 10 + 5,\n" +
                "                color: colors[Math.floor(Math.random() * colors.length)],\n" +
                "                speed: Math.random() * 3 + 2,\n" +
                "                rotation: Math.random() * 360,\n" +
                "                rotationSpeed: (Math.random() - 0.5) * 5,\n" +
                "                shape: Math.floor(Math.random() * 3) // 0: rectangle, 1: cercle, 2: étoile\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        // Animation des confettis\n" +
                "        function animate() {\n" +
                "            ctx.clearRect(0, 0, canvas.width, canvas.height);\n" +
                "\n" +
                "            for (let i = 0; i < confetti.length; i++) {\n" +
                "                const c = confetti[i];\n" +
                "                c.y += c.speed;\n" +
                "                c.rotation += c.rotationSpeed;\n" +
                "\n" +
                "                ctx.save();\n" +
                "                ctx.translate(c.x, c.y);\n" +
                "                ctx.rotate((c.rotation * Math.PI) / 180);\n" +
                "                ctx.fillStyle = c.color;\n" +
                "\n" +
                "                if (c.shape === 0) { // Rectangle\n" +
                "                    ctx.fillRect(-c.size / 2, -c.size / 2, c.size, c.size / 2);\n" +
                "                } else if (c.shape === 1) { // Cercle\n" +
                "                    ctx.beginPath();\n" +
                "                    ctx.arc(0, 0, c.size / 2, 0, Math.PI * 2);\n" +
                "                    ctx.fill();\n" +
                "                } else { // Étoile/ballon\n" +
                "                    ctx.beginPath();\n" +
                "                    for (let j = 0; j < 5; j++) {\n" +
                "                        ctx.lineTo(\n" +
                "                            Math.cos((j * 4 * Math.PI) / 5) * c.size / 2,\n" +
                "                            Math.sin((j * 4 * Math.PI) / 5) * c.size / 2\n" +
                "                        );\n" +
                "                    }\n" +
                "                    ctx.fill();\n" +
                "                }\n" +
                "\n" +
                "                ctx.restore();\n" +
                "\n" +
                "                // Reset confetti when it goes off screen\n" +
                "                if (c.y > canvas.height) {\n" +
                "                    c.y = -c.size;\n" +
                "                    c.x = Math.random() * canvas.width;\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            requestAnimationFrame(animate);\n" +
                "        }\n" +
                "\n" +
                "        // Start animation\n" +
                "        animate();\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
