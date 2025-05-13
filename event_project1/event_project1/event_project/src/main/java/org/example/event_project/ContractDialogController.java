package org.example.event_project;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.File;
import org.example.event_project.entities.Contract;
import org.example.event_project.entities.Reservation;
import org.example.event_project.repository.ContractRepository;
import org.example.event_project.repository.ReservationRepository;
import org.example.event_project.service.SmsService;

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
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Button showQrButton;
    @FXML
    private Button exportPdfButton;
    @FXML
    private Label qrCodeLabel;

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
                boolean isNew = contract.getId() == null || contract.getId() == 0;
                if (isNew) {
                    contract = contractRepository.save(contract);
                } else {
                    contract = contractRepository.update(contract);
                }
                if (isNew) {
                    // Send SMS after adding a new contract
                    SmsService.sendSms("+21621355366", "A new contract has been added: " + contract.getContractNumber());
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

    @FXML
    private void handleShowQrCode() {
        generateAndShowQrCode(contract);
        qrCodeImageView.setVisible(true);
        qrCodeLabel.setVisible(true);
    }

    private void generateAndShowQrCode(Contract contract) {
        if (contract == null) {
            qrCodeImageView.setImage(null);
            return;
        }
        String qrContent = "Contract ID: " + contract.getId() + "\nNumber: " + contract.getContractNumber() + "\nType: " + contract.getContractType();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 150, 150);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            Image fxImage = new Image(is);
            qrCodeImageView.setImage(fxImage);
        } catch (WriterException | java.io.IOException e) {
            qrCodeImageView.setImage(null);
        }
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleExportToPdf() {
        if (contract == null) {
            showError("No Contract", "No contract to export.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("contract_" + contract.getId() + ".pdf");
        File file = fileChooser.showSaveDialog(dialogStage);
        if (file != null) {
            try {
                // Generate QR code as BufferedImage
                String qrContent = "Contract ID: " + contract.getId() + "\nNumber: " + contract.getContractNumber() + "\nType: " + contract.getContractType();
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 150, 150);
                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                PDFExporter.exportContractToPDF(contract, file.getAbsolutePath(), bufferedImage);
                showInfo("Success", "Contract exported to PDF successfully.");
            } catch (Exception e) {
                showError("Error", "Failed to export contract: " + e.getMessage());
            }
        }
    }
}