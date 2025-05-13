package org.example.event_project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.event_project.entities.EventSponsor;
import org.example.event_project.service.ServiceEventSponsor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ManageEventSponsor {

    @FXML private Button button_create;
    @FXML private Button button_delete;
    @FXML private Button button_search;
    @FXML private Button button_update;
    @FXML private TableColumn<EventSponsor, Integer> column_eventId;
    @FXML private TableColumn<EventSponsor, Integer> column_sponsorId;
    @FXML private TableColumn<EventSponsor, String> column_sponsorshipLevel;
    @FXML private ComboBox<String> combobox_sponsorshipLevel;
    @FXML private TableView<EventSponsor> tableview_eventSponsor;
    @FXML private TextField textfield_eventId;
    @FXML private TextField textfield_search;
    @FXML private TextField textfield_sponsorId;
    @FXML private ComboBox<Integer> eventIdComboBox;
    @FXML private BarChart<String, Number> reportChart;
    @FXML private Label searchLabel; // Supprimez si non utilisé dans le FXML

    private final ServiceEventSponsor serviceEventSponsor = new ServiceEventSponsor();
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/project_event";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "";

    @FXML
    void initialize() {
        combobox_sponsorshipLevel.setItems(FXCollections.observableArrayList("Gold", "Silver", "Bronze"));

        column_eventId.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        column_sponsorId.setCellValueFactory(new PropertyValueFactory<>("sponsorId"));
        column_sponsorshipLevel.setCellValueFactory(new PropertyValueFactory<>("sponsorshipLevel"));

        loadEventIds();

        tableview_eventSponsor.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            } else {
                clearInputs();
            }
        });

        refreshTable();

        if (searchLabel != null) {
            searchLabel.setText("Rechercher par niveau");
        }
    }

    private void loadEventIds() {
        ObservableList<Integer> eventIds = FXCollections.observableArrayList();
        try (Connection conn = getConnection();
             PreparedStatement pstm = conn.prepareStatement("SELECT event_id FROM events");
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                eventIds.add(rs.getInt("event_id"));
            }
            eventIdComboBox.setItems(eventIds);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les IDs des événements: " + e.getMessage());
        }
    }

    @FXML
    void addEventSponsor(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            int eventId = Integer.parseInt(textfield_eventId.getText().trim());
            int sponsorId = Integer.parseInt(textfield_sponsorId.getText().trim());
            String level = combobox_sponsorshipLevel.getValue();

            if (!eventIdExists(eventId)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID de l'événement n'existe pas.");
                return;
            }

            if (!sponsorIdExists(sponsorId)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID du sponsor n'existe pas.");
                return;
            }

            EventSponsor es = new EventSponsor(eventId, sponsorId, level);
            serviceEventSponsor.ajouterPstm(es);
            refreshTable();
            clearInputs();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor ajouté avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le sponsor: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "Les IDs doivent être des nombres entiers.");
        }
    }

    @FXML
    void deleteEventSponsor(ActionEvent event) {
        EventSponsor selected = tableview_eventSponsor.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un sponsor à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le sponsor");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce sponsor?");

        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            try {
                serviceEventSponsor.delete(selected);
                refreshTable();
                clearInputs();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor supprimé avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    void updateEventSponsor(ActionEvent event) {
        EventSponsor selected = tableview_eventSponsor.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un sponsor à mettre à jour.");
            return;
        }

        String newLevel = combobox_sponsorshipLevel.getValue();
        if (newLevel == null || newLevel.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un niveau de sponsoring.");
            return;
        }

        selected.setSponsorshipLevel(newLevel);
        try {
            serviceEventSponsor.update(selected);
            refreshTable();
            clearInputs();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Sponsor mis à jour avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour: " + e.getMessage());
        }
    }

    @FXML
    void searchEventSponsor(ActionEvent event) {
        String search = textfield_search.getText().trim();
        try {
            List<EventSponsor> list = search.isEmpty()
                    ? serviceEventSponsor.getAll()
                    : serviceEventSponsor.rechercherParLevel(search);
            tableview_eventSponsor.setItems(FXCollections.observableArrayList(list));
            if (list.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Résultat", "Aucun sponsor trouvé pour la recherche.");
            }
            if (searchLabel != null) {
                searchLabel.setText(list.isEmpty() ? "Aucun résultat" : "Résultats de la recherche");
            }
            clearInputs();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    @FXML
    private void generateSponsorReport() {
        Integer eventId = eventIdComboBox.getValue();
        String query = "SELECT sponsorship_level, COUNT(*) as count FROM event_sponsors";
        if (eventId != null) {
            query += " WHERE event_id = ?";
        }
        query += " GROUP BY sponsorship_level";

        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();
        try (Connection conn = getConnection();
             PreparedStatement pstm = conn.prepareStatement(query)) {
            if (eventId != null) {
                pstm.setInt(1, eventId);
            }
            ResultSet rs = pstm.executeQuery();
            int totalSponsors = 0;
            while (rs.next()) {
                String level = rs.getString("sponsorship_level");
                String normalizedLevel = normalizeLevel(level);
                int count = rs.getInt("count");
                totalSponsors += count;
                chartData.add(new XYChart.Data<>(normalizedLevel, count));
            }
            if (totalSponsors == 0) {
                reportChart.setTitle("Aucun sponsor trouvé.");
                reportChart.getData().clear();
                return;
            }
            reportChart.setTitle("Répartition des sponsors (Total : " + totalSponsors + ")");
            XYChart.Series<String, Number> series = new XYChart.Series<>(chartData);
            reportChart.getData().setAll(series);

            applyCustomColors();

            CategoryAxis xAxis = (CategoryAxis) reportChart.getXAxis();
            xAxis.setCategories(FXCollections.observableArrayList("Bronze", "Silver", "Gold"));
            xAxis.setLabel("Niveau de parrainage");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du rapport: " + e.getMessage());
            reportChart.setTitle("Erreur : " + e.getMessage());
            reportChart.getData().clear();
        } finally {
            clearInputs();
        }
    }

    private String normalizeLevel(String level) {
        if (level == null) return "Unknown";
        switch (level.toLowerCase()) {
            case "gold":
                return "Gold";
            case "silver":
            case "lver":
                return "Silver";
            case "bronze":
            case "bronz":
                return "Bronze";
            default:
                return level;
        }
    }

    private void applyCustomColors() {
        for (XYChart.Series<String, Number> series : reportChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                String level = data.getXValue();
                String color = getColorForLevel(level);
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            }
        }
    }

    private String getColorForLevel(String level) {
        switch (level.toLowerCase()) {
            case "gold":
                return "#DAA520";
            case "silver":
            case "lver":
                return "#C0C0C0";
            case "bronze":
            case "bronz":
                return "#B87333";
            default:
                return "#bf7fff";
        }
    }

    private boolean validateInputs() {
        String eventIdText = textfield_eventId.getText().trim();
        String sponsorIdText = textfield_sponsorId.getText().trim();
        String level = combobox_sponsorshipLevel.getValue();

        if (eventIdText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ requis", "L'ID de l'événement est requis.");
            textfield_eventId.requestFocus();
            return false;
        }

        if (!INTEGER_PATTERN.matcher(eventIdText).matches()) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "L'ID de l'événement doit être un nombre entier positif.");
            textfield_eventId.requestFocus();
            return false;
        }

        if (sponsorIdText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ requis", "L'ID du sponsor est requis.");
            textfield_sponsorId.requestFocus();
            return false;
        }

        if (!INTEGER_PATTERN.matcher(sponsorIdText).matches()) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "L'ID du sponsor doit être un nombre entier positif.");
            textfield_sponsorId.requestFocus();
            return false;
        }

        if (level == null || level.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ requis", "Le niveau de sponsoring est requis.");
            combobox_sponsorshipLevel.requestFocus();
            return false;
        }

        return true;
    }

    private boolean eventIdExists(int eventId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstm = conn.prepareStatement("SELECT COUNT(*) FROM events WHERE event_id = ?")) {
            pstm.setInt(1, eventId);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private boolean sponsorIdExists(int sponsorId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstm = conn.prepareStatement("SELECT COUNT(*) FROM sponsors WHERE sponsor_id = ?")) {
            pstm.setInt(1, sponsorId);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private void populateFields(EventSponsor sponsor) {
        textfield_eventId.setText(String.valueOf(sponsor.getEventId()));
        textfield_sponsorId.setText(String.valueOf(sponsor.getSponsorId()));
        combobox_sponsorshipLevel.setValue(sponsor.getSponsorshipLevel());
    }

    private void clearInputs() {
        textfield_eventId.clear(); // Vider Event ID
        textfield_sponsorId.clear(); // Vider Sponsor ID
        textfield_search.clear(); // Vider champ de recherche
        combobox_sponsorshipLevel.setValue(null); // Vider Sponsorship Level
        eventIdComboBox.setValue(null); // Vider filtre par ID d'événement
        tableview_eventSponsor.getSelectionModel().clearSelection();
        if (searchLabel != null) {
            searchLabel.setText("Rechercher par niveau"); // Vider/reset label de recherche
        }
    }

    private void refreshTable() {
        try {
            List<EventSponsor> list = serviceEventSponsor.getAll();
            tableview_eventSponsor.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }
}