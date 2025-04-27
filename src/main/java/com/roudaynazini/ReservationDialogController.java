package com.roudaynazini;

import com.roudaynazini.model.Reservation;
import com.roudaynazini.repository.ReservationRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ReservationDialogController {

    @FXML
    private TextField clientNameField;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;

    private ReservationRepository reservationRepository;
    private Stage dialogStage;
    private Reservation reservation;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        // Initialize status options
        statusComboBox.getItems().addAll("Pending", "Confirmed", "Cancelled", "Completed");
    }

    public void setReservationRepository(ReservationRepository repository) {
        this.reservationRepository = repository;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        
        // Populate fields with reservation data
        clientNameField.setText(reservation.getClientName());
        eventDatePicker.setValue(reservation.getEventDate());
        statusComboBox.setValue(reservation.getStatus());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            if (reservation == null) {
                reservation = new Reservation();
                // Set creation date for new reservations
                reservation.setCreatedAt(LocalDate.now());
            }
            
            // Update reservation with form data
            reservation.setClientName(clientNameField.getText());
            reservation.setEventDate(eventDatePicker.getValue());
            reservation.setStatus(statusComboBox.getValue());
            // Always update the updatedAt date
            reservation.setUpdatedAt(LocalDate.now());
            
            try {
                if (reservation.getId() == 0) {
                    // New reservation
                    reservationRepository.save(reservation);
                } else {
                    // Existing reservation
                    reservationRepository.update(reservation);
                }
                okClicked = true;
                dialogStage.close();
            } catch (Exception e) {
                showError("Database Error", "Error saving reservation: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (clientNameField.getText() == null || clientNameField.getText().trim().isEmpty()) {
            errorMessage += "Client name is required!\n";
        }
        
        if (eventDatePicker.getValue() == null) {
            errorMessage += "Event date is required!\n";
        } else if (eventDatePicker.getValue().isBefore(LocalDate.now())) {
            errorMessage += "Event date cannot be in the past!\n";
        }
        
        if (statusComboBox.getValue() == null) {
            errorMessage += "Status is required!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showError("Invalid Fields", errorMessage);
            return false;
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 