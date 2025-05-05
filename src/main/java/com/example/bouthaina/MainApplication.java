package com.example.bouthaina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load admin dashboard by default
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bouthaina/AdminView/Dashboard.fxml"));
        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("ConnectArt - Administration");
        stage.setScene(scene);

        // Ajouter une transition d'entrée élégante
        root.setOpacity(0);
        stage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    public static void main(String[] args) {
        launch();
    }
}
