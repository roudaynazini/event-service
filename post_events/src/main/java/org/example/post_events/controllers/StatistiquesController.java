package org.example.post_events.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import org.example.post_events.service.ServiceCommentaire;
import org.example.post_events.service.ServicePost;

import java.util.*;

public class StatistiquesController {

    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label totalPostsLabel;
    @FXML
    private Label totalCommentairesLabel;
    @FXML
    private PieChart pieChart;

    private final ServicePost servicePost = new ServicePost();
    private final ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    @FXML
    public void initialize() {
        // Récupérer les données temporelles des posts et des commentaires
        Map<String, Integer> postsParMois = servicePost.getNombrePostsParMois();
        Map<String, Integer> commentairesParMois = serviceCommentaire.getNombreCommentairesParMois();

        // Calcul du total des posts et commentaires
        int totalPosts = postsParMois.values().stream().mapToInt(Integer::intValue).sum();
        int totalCommentaires = commentairesParMois.values().stream().mapToInt(Integer::intValue).sum();

        // Mise à jour des labels avec les totaux
        totalPostsLabel.setText("Nombre total de posts : " + formatNombre(totalPosts));
        totalCommentairesLabel.setText("Nombre total de commentaires : " + formatNombre(totalCommentaires));

        // Création des séries pour le LineChart
        XYChart.Series<String, Number> postsSeries = new XYChart.Series<>();
        postsSeries.setName("Posts");

        XYChart.Series<String, Number> commentairesSeries = new XYChart.Series<>();
        commentairesSeries.setName("Commentaires");
        // Collecter tous les mois (uniquement ceux présents dans les données de posts ou commentaires)
        Set<String> tousLesMois = new TreeSet<>();
        tousLesMois.addAll(postsParMois.keySet());
        tousLesMois.addAll(commentairesParMois.keySet());
        // Remplir les séries avec les données de posts et commentaires
        for (String mois : tousLesMois) {
            postsSeries.getData().add(new XYChart.Data<>(mois, postsParMois.getOrDefault(mois, 0)));
            commentairesSeries.getData().add(new XYChart.Data<>(mois, commentairesParMois.getOrDefault(mois, 0)));
        }
        // Ajouter les séries au graphique
        lineChart.getData().addAll(postsSeries, commentairesSeries);
        // Préparer les données pour le PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Posts", totalPosts),
                new PieChart.Data("Commentaires", totalCommentaires)
        );
        pieChart.setData(pieChartData);
    }
    // Méthode pour formater les nombres avec une virgule comme séparateur
    private String formatNombre(int nombre) {
        return String.format("%,d", nombre);
    }
}
