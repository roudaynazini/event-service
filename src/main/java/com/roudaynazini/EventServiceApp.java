package com.roudaynazini;

import com.roudaynazini.config.DatabaseConfig;
import com.roudaynazini.repository.ContractRepository;
import com.roudaynazini.repository.MySQLUserRepository;
import com.roudaynazini.repository.ReservationRepository;
import com.roudaynazini.repository.UserRepository;
import com.roudaynazini.repository.impl.ContractRepositoryImpl;
import com.roudaynazini.repository.impl.ReservationRepositoryImpl;
import com.roudaynazini.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EventServiceApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Test database connection first
        DatabaseConfig.testConnection();
        System.out.println("Database Connected Successfully");

        // Initialize repositories
        UserRepository userRepository = new MySQLUserRepository();
        ReservationRepository reservationRepository = new ReservationRepositoryImpl();
        ContractRepository contractRepository = new ContractRepositoryImpl();
        UserService userService = new UserService(userRepository);

        // Load main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();
        
        // Get controller and set repositories and services
        MainViewController controller = loader.getController();
        controller.setUserService(userService);
        controller.setRepositories(reservationRepository, contractRepository, userRepository);

        // Set up and show the stage
        primaryStage.setTitle("Event Service Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        
        // Show the stage first
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 