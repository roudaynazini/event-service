package com.roudaynazini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.roudaynazini.repository.ReservationRepository;
import com.roudaynazini.repository.ContractRepository;
import com.roudaynazini.repository.impl.ReservationRepositoryImpl;
import com.roudaynazini.repository.impl.ContractRepositoryImpl;
import com.roudaynazini.config.DatabaseConfig;
import com.roudaynazini.config.DatabaseInitializer;

import java.io.IOException;
import java.sql.Connection;

public class EventServiceApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EventServiceApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        
        // Get the controller and inject repositories
        MainViewController controller = fxmlLoader.getController();
        try {
            // Get database connection
            Connection connection = DatabaseConfig.getConnection();
            
            // Initialize database tables
            DatabaseInitializer.initializeDatabase(connection);
            
            // Create repositories
            ReservationRepository reservationRepository = new ReservationRepositoryImpl(connection);
            ContractRepository contractRepository = new ContractRepositoryImpl(connection);
            
            // Set repositories in controller
            controller.setRepositories(reservationRepository, contractRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        stage.setTitle("Event Service Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
} 