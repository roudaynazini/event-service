package com.roudaynazini;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainViewController {
    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, String> idColumn;
    @FXML
    private TableColumn<Reservation, String> nameColumn;
    @FXML
    private TableColumn<Reservation, String> dateColumn;
    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    @FXML
    private void handleAddReservation() {
        // TODO: Implement add reservation functionality
        showAlert("Add Reservation", "This feature will be implemented soon.");
    }

    @FXML
    private void handleEditReservation() {
        // TODO: Implement edit reservation functionality
        showAlert("Edit Reservation", "This feature will be implemented soon.");
    }

    @FXML
    private void handleDeleteReservation() {
        // TODO: Implement delete reservation functionality
        showAlert("Delete Reservation", "This feature will be implemented soon.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 