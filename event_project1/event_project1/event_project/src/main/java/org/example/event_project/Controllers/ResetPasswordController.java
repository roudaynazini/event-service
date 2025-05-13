package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;
import jakarta.mail.MessagingException;

public class ResetPasswordController {

    @FXML
    private TextField emailField;

    private String resetCode;

    @FXML
    private void handleSendCode() {
        String email = emailField.getText();

        // Vérifier que l'email est valide
        if (email == null || email.isEmpty()) {
            System.out.println("L'email ne peut pas être vide.");
            return;
        }

        // Générer un code à 6 chiffres
        resetCode = String.format("%06d", new Random().nextInt(999999));

        // Envoyer l'e-mail
        try {
            // Envoi du code de réinitialisation à l'utilisateur
            JakartaMailUtil.sendEmail(email, "Code de réinitialisation",
                    "Voici votre code de réinitialisation : " + resetCode);

            // Charger l'interface de vérification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/codeVerification.fxml"));
            Parent root = loader.load();

            // Passer le code attendu et l'email à l'écran de vérification
            CodeVerificationController controller = loader.getController();
            controller.setExpectedCode(resetCode);
            controller.setUserEmail(email);

            // Changer la scène pour l'écran de vérification
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.out.println("Échec de l'envoi du code.");
        }
    }
}
