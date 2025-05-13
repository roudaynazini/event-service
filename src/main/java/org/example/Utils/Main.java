package org.example.Utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger la vue d'accueil (accueil.fxml)
        FXMLLoader loaderAvis = new FXMLLoader(getClass().getResource("/accueil.fxml"));
        Parent accueilView = loaderAvis.load();
        Scene sceneAccueil = new Scene(accueilView);

        // Application title and scene setup
        primaryStage.setTitle("Gestion des Avis et Réclamations");
        primaryStage.setScene(sceneAccueil);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
