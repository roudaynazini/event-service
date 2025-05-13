package org.example.event_project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.event_project.entities.Reservation;
import org.example.event_project.entities.User;
import org.example.event_project.repository.ReservationRepository;
import org.example.event_project.service.SmsService;

public class ReservationDialogController {

    @FXML
    private TextField clientNameField;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea notesArea;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Button showQrButton;
    @FXML
    private Label qrCodeLabel;

    private ReservationRepository reservationRepository;
    private Stage dialogStage;
    private Reservation reservation;
    private boolean okClicked = false;
    private User currentUser;
    private MainViewController mainViewController;

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
        clientNameField.setText(reservation.getClientName());
        eventDatePicker.setValue(reservation.getEventDate());
        statusComboBox.setValue(reservation.getStatus());
        notesArea.setText(reservation.getNotes());
        // Hide QR code by default
        qrCodeImageView.setVisible(false);
        qrCodeLabel.setVisible(false);
    }

    public void setViewOnly(boolean viewOnly) {
        // Disable all fields in view mode
        clientNameField.setEditable(!viewOnly);
        eventDatePicker.setDisable(viewOnly);
        statusComboBox.setDisable(viewOnly);
        notesArea.setEditable(!viewOnly);
        
        // Hide action buttons in view mode
        if (viewOnly) {
            okButton.setVisible(false);
            cancelButton.setText("Close");
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            if (reservation == null) {
                reservation = new Reservation();
                reservation.setCreatedAt(LocalDate.now());
            }
            reservation.setClientName(clientNameField.getText());
            reservation.setEventDate(eventDatePicker.getValue());
            reservation.setStatus(statusComboBox.getValue());
            reservation.setNotes(notesArea.getText());
            reservation.setUpdatedAt(LocalDate.now());
            try {
                boolean isNew = reservation.getId() == null;
                if (isNew) {
                    reservationRepository.save(reservation);
                } else {
                    reservationRepository.update(reservation);
                }
                if (isNew) {
                    // Always notify admin in-app
                    if (mainViewController != null) {
                        String who = (currentUser != null) ? currentUser.getUsername() : "Unknown";
                        mainViewController.addNotification("User " + who + " added a reservation for " + reservation.getClientName());
                    }
                    // Optionally, only send SMS to admin if not admin
                    if (currentUser != null && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                        StringBuilder sms = new StringBuilder();
                        sms.append("A new reservation was added by user: ").append(currentUser.getUsername()).append("\n");
                        sms.append("Client: ").append(reservation.getClientName()).append("\n");
                        sms.append("Date: ").append(reservation.getEventDate()).append("\n");
                        sms.append("Status: ").append(reservation.getStatus()).append("\n");
                        if (reservation.getNotes() != null && !reservation.getNotes().isEmpty()) {
                            sms.append("Notes: ").append(reservation.getNotes());
                        }
                        SmsService.sendSms("+21621355366", sms.toString());
                    }
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

    @FXML
    private void handleShowQrCode() {
        generateAndShowQrCode(reservation);
        qrCodeImageView.setVisible(true);
        qrCodeLabel.setVisible(true);
    }
    
    @FXML
    private void handleExportToPdf() {
        if (reservation == null || reservation.getId() == null) {
            showError("Export Error", "Please save the reservation before exporting to PDF.");
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("reservation_" + reservation.getId() + ".pdf");
            
            File file = fileChooser.showSaveDialog(dialogStage);
            if (file != null) {
                PDFExporter.exportReservationToPDF(reservation, file.getAbsolutePath());
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Reservation has been exported to PDF successfully.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            showError("Export Error", "Failed to export reservation to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isAddMode() {
        return reservation == null || reservation.getId() == null;
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (clientNameField.getText() == null || clientNameField.getText().trim().isEmpty()) {
            errorMessage += "Client name is required!\n";
        }
        if (eventDatePicker.getValue() == null) {
            errorMessage += "Event date is required!\n";
        } else if (isAddMode() && eventDatePicker.getValue().isBefore(LocalDate.now())) {
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

    private void generateAndShowQrCode(Reservation reservation) {
        if (reservation == null) {
            qrCodeImageView.setImage(null);
            return;
        }
        String qrContent = "Reservation ID: " + reservation.getId() + "\nClient: " + reservation.getClientName() + "\nDate: " + reservation.getEventDate();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 150, 150);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            Image fxImage = new Image(is);
            qrCodeImageView.setImage(fxImage);
        } catch (WriterException | IOException e) {
            qrCodeImageView.setImage(null);
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
