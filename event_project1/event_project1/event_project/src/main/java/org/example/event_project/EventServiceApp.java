package org.example.event_project;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.event_project.repository.ContractRepository;
import org.example.event_project.repository.MySQLUserRepository;
import org.example.event_project.repository.ReservationRepository;
import org.example.event_project.repository.UserRepository;
import org.example.event_project.repository.impl.ContractRepositoryImpl;
import org.example.event_project.repository.impl.ReservationRepositoryImpl;
import org.example.event_project.service.UserService;

public class EventServiceApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Test database connection first
        System.out.println("Database Connected Successfully");

        // Initialize repositories
        UserRepository userRepository = new MySQLUserRepository();
        ReservationRepository reservationRepository = new ReservationRepositoryImpl();
        ContractRepository contractRepository = new ContractRepositoryImpl();
        
        // Initialize services
        UserService userService = new UserService(userRepository);

        // Load main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();
        
        // Get controller and set repositories and services
        MainViewController controller = loader.getController();
        controller.setUserService(userService);
        controller.setCurrentUser(userRepository.findByUsername("admin")); // Set admin as default user
        controller.setRepositories(reservationRepository, contractRepository, userRepository);

        // Set up and show the stage
        primaryStage.setTitle("Event Service Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/roudaynazini/images/logo.jpg"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        
        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}