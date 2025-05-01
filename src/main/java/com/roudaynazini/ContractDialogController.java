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
    @FXML
    private TextField totalAmountField;
    @FXML
    private TextArea notesArea;

    private ContractRepository contractRepository;
    private ReservationRepository reservationRepository;
    private Stage dialogStage;
    private Contract contract;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        // Initialize status options
        statusComboBox.getItems().addAll("Brouillon", "Actif", "Expiré", "Résilié");
        
        // Initialize contract type options
        contractTypeComboBox.getItems().addAll("Standard", "Premium", "VIP", "Personnalisé");
        
        // Set default values
        statusComboBox.setValue("Brouillon");
        contractTypeComboBox.setValue("Standard");
        startDatePicker.setValue(LocalDate.now());
        
        // Add listener for contract type changes to update total amount
        contractTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateTotalAmount(newVal);
            }
        });
        
        // Add listener for reservation selection to auto-generate contract number and set end date
        reservationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                generateContractNumber(newVal);
                endDatePicker.setValue(newVal.getEventDate());
            }
        });
    }

    private void updateTotalAmount(String contractType) {
        double baseAmount = switch (contractType) {
            case "Standard" -> 1000.0;
            case "Premium" -> 2000.0;
            case "VIP" -> 3000.0;
            case "Personnalisé" -> 1500.0;
            default -> 1000.0;
        };
        totalAmountField.setText(String.format("%.2f", baseAmount));
    }

    private void generateContractNumber(Reservation reservation) {
        String contractType = contractTypeComboBox.getValue();
        String prefix = switch (contractType) {
            case "Standard" -> "STD";
            case "Premium" -> "PRE";
            case "VIP" -> "VIP";
            case "Personnalisé" -> "CUS";
            default -> "CON";
        };
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        contractNumberField.setText(prefix + "-" + reservation.getId() + "-" + timestamp);
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
        if (contract.getReservationId() != null && contract.getReservationId() > 0) {
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
        totalAmountField.setText(String.valueOf(contract.getTotalAmount()));
        notesArea.setText(contract.getNotes());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            if (contract == null) {
                contract = new Contract();
            }
            Reservation selectedReservation = reservationComboBox.getValue();
            if (selectedReservation != null) {
                contract.setReservationId(Long.valueOf(selectedReservation.getId()));
            }
            contract.setContractNumber(contractNumberField.getText());
            contract.setContractType(contractTypeComboBox.getValue());
            contract.setStatus(statusComboBox.getValue());
            contract.setStartDate(startDatePicker.getValue());
            contract.setEndDate(endDatePicker.getValue());
            contract.setTotalAmount(Double.parseDouble(totalAmountField.getText()));
            contract.setNotes(notesArea.getText());
            try {
                if (contract.getId() == null || contract.getId() == 0) {
                    contractRepository.save(contract);
                } else {
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
        if (totalAmountField.getText() == null || totalAmountField.getText().trim().isEmpty()) {
            errorMessage += "Total amount is required!\n";
        } else {
            try {
                Double.parseDouble(totalAmountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Total amount must be a number!\n";
            }
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