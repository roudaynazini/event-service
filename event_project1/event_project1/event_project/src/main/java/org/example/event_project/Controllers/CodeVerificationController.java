package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CodeVerificationController {

    @FXML
    private TextField codeField;

    private String expectedCode;
    private String userEmail;

    @FXML
    private void handleVerifyCode() {
        String inputCode = codeField.getText();
        if (inputCode.equals(expectedCode)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/newPassword.fxml"));
                Parent root = loader.load();

                // Passer l'e-mail à NewPasswordController
               NewPasswordController controller = loader.getController();
                controller.setUserEmail(userEmail);

                Stage stage = (Stage) codeField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Code incorrect.");
        }
    }

    public void setExpectedCode(String code) {
        this.expectedCode = code;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }
}
