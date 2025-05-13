package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.entities.avis;
import org.example.sevice.serviceavis;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiquesController {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private PieChart pieChart;

    private serviceavis sa = new serviceavis();
    private ObservableList<avis> avisList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Charger les avis depuis la base de données
        loadAvis();

        // Afficher les statistiques
        afficherStatistiques();
    }

    private void loadAvis() {
        try {
            List<avis> list = sa.getAll();
            avisList.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur de chargement des avis", "Impossible de charger les avis depuis la base de données.");
        }
    }

    private void afficherStatistiques() {
        if (avisList.isEmpty()) {
            showError("Aucun avis", "Il n'y a pas d'avis à afficher.");
            return;
        }

        // Graphique à barres : Nombre d'avis par jour
        Map<LocalDate, Long> avisParJour = avisList.stream()
                .map(a -> new Date(a.getDateAvis().getTime()).toLocalDate())  // Conversion en LocalDate
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()));

        // Mettre à jour le graphique à barres
        updateBarChart(avisParJour);

        // Graphique circulaire : Répartition des notes
        Map<Integer, Long> repartitionNotes = avisList.stream()
                .collect(Collectors.groupingBy(avis::getNote, Collectors.counting()));

        // Mettre à jour le graphique circulaire
        updatePieChart(repartitionNotes);
    }

    private void updateBarChart(Map<LocalDate, Long> avisParJour) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Ajouter les données dans la série
        for (Map.Entry<LocalDate, Long> entry : avisParJour.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        // Ajouter la série au graphique
        barChart.getData().clear();
        barChart.getData().add(series);
    }

    private void updatePieChart(Map<Integer, Long> repartitionNotes) {
        pieChart.getData().clear();

        // Ajouter les données dans le graphique circulaire
        for (Map.Entry<Integer, Long> entry : repartitionNotes.entrySet()) {
            PieChart.Data data = new PieChart.Data("Note " + entry.getKey(), entry.getValue());
            pieChart.getData().add(data);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void passerAavis() {
        try {
            // Charger le fichier FXML pour les avis
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/avis.fxml"));
            AnchorPane root = loader.load(); // Charge le layout Avis
            Scene scene = new Scene(root);

            // Récupérer le Stage actuel et changer la scène
            Stage stage = (Stage) barChart.getScene().getWindow(); // Utilisation d'un composant de la scène actuelle
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la navigation", "Impossible de charger la vue des avis.");
        }
    }
}
