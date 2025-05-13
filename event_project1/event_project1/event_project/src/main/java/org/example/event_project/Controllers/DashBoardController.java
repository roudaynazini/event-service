package org.example.event_project.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class DashBoardController {

    @FXML
    void goToInvitation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/Invitation.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de Invitation.fxml : " + e.getMessage());
        }
    }

    @FXML
    void goToInvitee(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/event_project/Invitee.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de Invitee.fxml : " + e.getMessage());
        }
    }
}
