package com.roudaynazini;

import com.roudaynazini.model.Contract;
import com.roudaynazini.model.Reservation;
import com.roudaynazini.repository.ContractRepository;
import com.roudaynazini.repository.ReservationRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ContractDialogController {

    @FXML
    private ComboBox<Reservation> reservationComboBox;
    @FXML
    private TextField contractNumberField;
    @FXML
    private ComboBox<String> contractTypeComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private ContractRepository contractRepository;
    private ReservationRepository reservationRepository;
    private Stage dialogStage;
    private Contract contract;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        // Initialize contract type options
        contractTypeComboBox.getItems().addAll("Standard", "Premium", "Custom");
        
        // Initialize status options
        statusComboBox.getItems().addAll("Draft", "Active", "Expired", "Terminated");
    }

    public void setContractRepository(ContractRepository repository) {
        this.contractRepository = repository;
    }

    public void setReservationRepository(ReservationRepository repository) {
        this.reservationRepository = repository;
        
        // Load reservations into combo box
        try {
            List<Reservation> reservations = reservationRepository.findAll();
            reservationComboBox.setItems(javafx.collections.FXCollections.observableArrayList(reservations));
            
            // Set cell factory to display client name
            reservationComboBox.setCellFactory(lv -> new ListCell<Reservation>() {
                @Override
                protected void updateItem(Reservation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getClientName() + " (" + item.getEventDate() + ")");
                }
            });
        } catch (Exception e) {
            showError("Database Error", "Error loading reservations: " + e.getMessage());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
        
        // Populate fields with contract data
        if (contract.getReservationId() > 0) {
            try {
                Optional<Reservation> reservationOpt = reservationRepository.findById(contract.getReservationId());
                reservationOpt.ifPresent(reservation -> reservationComboBox.setValue(reservation));
            } catch (Exception e) {
                // Ignore if reservation not found
            }
        }
        
        contractNumberField.setText(contract.getContractNumber());
        contractTypeComboBox.setValue(contract.getContractType());
        statusComboBox.setValue(contract.getStatus());
        startDatePicker.setValue(contract.getStartDate());
        endDatePicker.setValue(contract.getEndDate());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            if (contract == null) {
                contract = new Contract();
                // Set creation date for new contracts
                contract.setCreatedAt(LocalDate.now());
            }
            
            // Update contract with form data
            Reservation selectedReservation = reservationComboBox.getValue();
            if (selectedReservation != null) {
                contract.setReservationId(selectedReservation.getId());
            }
            
            contract.setContractNumber(contractNumberField.getText());
            contract.setContractType(contractTypeComboBox.getValue());
            contract.setStatus(statusComboBox.getValue());
            contract.setStartDate(startDatePicker.getValue());
            contract.setEndDate(endDatePicker.getValue());
            // Always update the updatedAt date
            contract.setUpdatedAt(LocalDate.now());
            
            try {
                if (contract.getId() == 0) {
                    // New contract
                    contractRepository.save(contract);
                } else {
                    // Existing contract
                    contractRepository.update(contract);
                }
                okClicked = true;
                dialogStage.close();
            } catch (Exception e) {
                showError("Database Error", "Error saving contract: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (reservationComboBox.getValue() == null) {
            errorMessage += "Reservation is required!\n";
        }
        
        if (contractNumberField.getText() == null || contractNumberField.getText().trim().isEmpty()) {
            errorMessage += "Contract number is required!\n";
        }
        
        if (contractTypeComboBox.getValue() == null) {
            errorMessage += "Contract type is required!\n";
        }
        
        if (statusComboBox.getValue() == null) {
            errorMessage += "Status is required!\n";
        }
        
        if (startDatePicker.getValue() == null) {
            errorMessage += "Start date is required!\n";
        }
        
        if (endDatePicker.getValue() == null) {
            errorMessage += "End date is required!\n";
        } else if (startDatePicker.getValue() != null && 
                  endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            errorMessage += "End date cannot be before start date!\n";
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