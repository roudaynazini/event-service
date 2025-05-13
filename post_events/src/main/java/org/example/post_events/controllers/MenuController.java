package org.example.post_events.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuController {
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), welcomeLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(2), welcomeLabel);
        moveUp.setFromY(20);
        moveUp.setToY(0);

        ParallelTransition transition = new ParallelTransition(fadeIn, moveUp);
        transition.play();
    }


    @FXML
    void handlePosts(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/post_events/service-post.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Gérer les Posts");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCommentaires(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/post_events/service-commentaire.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Gérer les Commentaires");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowBadges(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/post_events/Badges.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Mes Badges 🎖");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirCommentairesPosts(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/post_events/PostDetails.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Interactons");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public void Statistique(ActionEvent actionEvent) {
        try {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/post_events/Statistique.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Statistique");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }}
}

