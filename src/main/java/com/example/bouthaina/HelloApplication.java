package com.example.bouthaina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Load the client dashboard
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/bouthaina/ClientView/ClientDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            stage.setTitle("ConnectArt - Client");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            // Print detailed error information
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();

            // Try loading admin dashboard as fallback
            try {
                Parent adminRoot = FXMLLoader
                        .load(getClass().getResource("/com/example/bouthaina/AdminView/Dashboard.fxml"));
                Scene adminScene = new Scene(adminRoot, 1200, 800);
                stage.setTitle("ConnectArt - Administration");
                stage.setScene(adminScene);
                stage.show();
            } catch (Exception e2) {
                System.err.println("Fatal error: Could not load any interface");
                e2.printStackTrace();
                throw e2;
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}