package org.example.event_project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {


    @FXML private ImageView compteImage;
    private String userEmail; // Stockage de l'email

    public void setUserEmail(String email) {
        this.userEmail = email;
        System.out.println("Email reçu dans Accueil: " + userEmail); // Debug
    }

    @FXML
    private void goToCompte(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/compte.fxml"));
            Parent root = loader.load();

            compte compteController = loader.getController();
            compteController.setUserEmail(this.userEmail); // Transmission de l'email

            Stage stage = (Stage) compteImage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Compte");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void go_to_Reclamation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/accueil_avis.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }    }

    @FXML
    void go_to_event(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/ClientView/ClientDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }      }

    @FXML
    void go_to_ivitation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/DashBoard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void go_to_media(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/Menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void go_to_reservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/main-view.fxml"));
            Parent root = loader.load();
            
            // Initialize repositories
            org.example.event_project.repository.UserRepository userRepository = new org.example.event_project.repository.MySQLUserRepository();
            org.example.event_project.repository.ReservationRepository reservationRepository = new org.example.event_project.repository.impl.ReservationRepositoryImpl();
            org.example.event_project.repository.ContractRepository contractRepository = new org.example.event_project.repository.impl.ContractRepositoryImpl();
            
            // Get the controller and initialize repositories and services
            org.example.event_project.MainViewController controller = loader.getController();
            controller.setRepositories(reservationRepository, contractRepository, userRepository);
            
            // Initialize services
            org.example.event_project.service.UserService userService = new org.example.event_project.service.UserService(userRepository);
            controller.setUserService(userService);
            
            // Set default user
            controller.setCurrentUser(userRepository.findByUsername("admin"));
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void go_to_sponsor(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/manage_Sponsor.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
