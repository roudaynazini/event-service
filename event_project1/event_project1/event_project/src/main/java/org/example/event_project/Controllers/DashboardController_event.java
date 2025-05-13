package org.example.event_project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController_event implements Initializable {
    @FXML
    private Button btnCategories;

    @FXML
    private Button btnEvenements;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox sidebarVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load categories view by default
        loadView("/org/example/event_project/AdminView/CategorieList.fxml");

        // Check if sidebarVBox exists in the FXML before trying to add buttons to it
        if (sidebarVBox != null) {
            // Add Statistics Button to the sidebar
            Button statsButton = new Button("Statistiques");
            statsButton.getStyleClass().add("nav-button");

            FontIcon statsIcon = new FontIcon(FontAwesomeSolid.CHART_PIE);
            statsIcon.setIconSize(18);
            statsIcon.setIconColor(Color.WHITE);

            statsButton.setGraphic(statsIcon);
            statsButton.setGraphicTextGap(10);
            statsButton.setPrefWidth(200);
            statsButton.setOnAction(e -> showStatisticsDialog());

            // Add to sidebar VBox
            sidebarVBox.getChildren().add(statsButton);
        } else {
            System.err.println("Warning: sidebarVBox not found in FXML. Statistics button not added.");
        }
    }

    @FXML
    private void handleCategoriesAction() {
        loadView("/org/example/event_project/AdminView/CategorieList.fxml");
    }

    @FXML
    private void handleEvenementsAction() {
        loadView("/org/example/event_project/AdminView/EvenementList.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath);
        }
    }

    /**
     * Opens the statistics dialog with charts
     */
    private void showStatisticsDialog() {
        try {
            // Load the statistics dialog FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/event_project/BackOffice/views/StatisticsDialog.fxml"));
            Parent root = loader.load();

            // Create a new stage for the dialog
            Stage statsStage = new Stage();
            statsStage.initModality(Modality.APPLICATION_MODAL);
            statsStage.initStyle(StageStyle.UNDECORATED);
            statsStage.setTitle("Statistiques");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/event_project/css/admin.css").toExternalForm());
            statsStage.setScene(scene);

            statsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des statistiques.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Shows an alert dialog with the specified title, message and alert type
     *
     * @param title     The title of the alert
     * @param message   The message to display in the alert
     * @param alertType The type of alert (e.g. ERROR, INFORMATION, etc.)
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets()
                .add(getClass().getResource("/org/example/event_project/css/admin.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
}