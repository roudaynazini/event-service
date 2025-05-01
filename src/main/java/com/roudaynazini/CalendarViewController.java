package com.roudaynazini;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.roudaynazini.model.Reservation;
import com.roudaynazini.repository.ReservationRepository;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CalendarViewController {
    @FXML private VBox root;
    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private ListView<Reservation> reservationList;

    private ReservationRepository reservationRepository;
    private YearMonth currentYearMonth;
    private LocalDate selectedDate;

    @FXML
    private void initialize() {
        currentYearMonth = YearMonth.now();
        setupCalendarGrid();
        updateCalendar();
        reservationList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getClientName() + " (" + item.getStatus() + ")");
                }
            }
        });
    }

    private void setupCalendarGrid() {
        // Add day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #C71585;");
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, i, 0);
        }
    }

    private void updateCalendar() {
        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        
        // Clear existing calendar cells
        calendarGrid.getChildren().removeIf(node -> 
            GridPane.getRowIndex(node) == null || GridPane.getRowIndex(node) > 0);

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        // Create calendar cells
        for (int i = 1; i <= currentYearMonth.lengthOfMonth(); i++) {
            LocalDate date = currentYearMonth.atDay(i);
            int row = ((i + dayOfWeek - 1) / 7) + 1;
            int col = (i + dayOfWeek - 1) % 7;
            createCalendarCell(date, row, col);
        }
    }

    private void createCalendarCell(LocalDate date, int row, int col) {
        VBox cell = new VBox(5);
        cell.setAlignment(Pos.TOP_CENTER);
        cell.setStyle("-fx-border-color: #9370DB; -fx-border-width: 0.5px; -fx-padding: 5; -fx-background-color: white;");
        cell.setPrefHeight(100);
        
        // Date label with reservation count
        HBox dateHeader = new HBox(5);
        dateHeader.setAlignment(Pos.CENTER);
        dateHeader.setMaxWidth(Double.MAX_VALUE);
        
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #C71585;");
        
        // Get reservations for this date
        final List<Reservation> dateReservations = reservationRepository != null ? 
            reservationRepository.findByEventDate(date) : new ArrayList<>();
            
        // Add reservation count if there are any
        if (!dateReservations.isEmpty()) {
            Label countLabel = new Label("(" + dateReservations.size() + ")");
            countLabel.setStyle("-fx-text-fill: #9370DB; -fx-font-size: 11px;");
            dateHeader.getChildren().addAll(dateLabel, countLabel);
        } else {
            dateHeader.getChildren().add(dateLabel);
        }
        
        VBox reservationsBox = new VBox(2);
        reservationsBox.setAlignment(Pos.TOP_CENTER);
        reservationsBox.setMaxWidth(Double.MAX_VALUE);
        
        // Show up to 3 reservations in the cell
        int maxDisplay = Math.min(dateReservations.size(), 3);
        for (int i = 0; i < maxDisplay; i++) {
            Reservation res = dateReservations.get(i);
            Label resLabel = new Label(res.getClientName());
            resLabel.setStyle("-fx-text-fill: #9370DB; -fx-padding: 2 5; -fx-background-color: #FFF0F5; " +
                            "-fx-background-radius: 3; -fx-font-size: 11px;");
            resLabel.setMaxWidth(Double.MAX_VALUE);
            resLabel.setAlignment(Pos.CENTER);
            reservationsBox.getChildren().add(resLabel);
        }
        
        // If there are more reservations, add a "+X more" label
        if (dateReservations.size() > 3) {
            Label moreLabel = new Label("+" + (dateReservations.size() - 3) + " more");
            moreLabel.setStyle("-fx-text-fill: #C71585; -fx-font-size: 10px; -fx-font-style: italic; -fx-cursor: hand;");
            moreLabel.setAlignment(Pos.CENTER);
            moreLabel.setOnMouseClicked(e -> {
                showAllReservationsDialog(date, dateReservations);
            });
            reservationsBox.getChildren().add(moreLabel);
        }
        
        cell.getChildren().addAll(dateHeader, reservationsBox);
        
        // Handle cell click
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            updateReservationList(dateReservations);
            
            // Highlight selected cell
            calendarGrid.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    node.setStyle("-fx-border-color: #9370DB; -fx-border-width: 0.5px; -fx-padding: 5; -fx-background-color: white;");
                }
            });
            cell.setStyle("-fx-border-color: #C71585; -fx-border-width: 2px; -fx-padding: 5; -fx-background-color: #FFF0F5;");
        });
        
        calendarGrid.add(cell, col, row);
    }

    private void updateReservationList(List<Reservation> reservations) {
        reservationList.getItems().clear();
        if (reservations.isEmpty()) {
            reservationList.setPlaceholder(new Label("No reservations for this date"));
            reservationList.refresh();
            return;
        }
        reservationList.getItems().addAll(reservations);
        reservationList.refresh();
    }

    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleViewReservation() {
        Reservation selectedReservation = reservationList.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation-dialog.fxml"));
            Parent root = loader.load();
            ReservationDialogController controller = loader.getController();
            controller.setReservationRepository(reservationRepository);
            controller.setReservation(selectedReservation);
            controller.setViewOnly(true);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("View Reservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.root.getScene().getWindow());
            controller.setDialogStage(dialogStage);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the reservation dialog: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setReservationRepository(ReservationRepository repository) {
        this.reservationRepository = repository;
        updateCalendar();
    }

    private void showAllReservationsDialog(LocalDate date, List<Reservation> reservations) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("All Reservations for " + date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(root.getScene().getWindow());

        ListView<Reservation> listView = new ListView<>();
        listView.getItems().addAll(reservations);
        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getClientName() + " (" + item.getStatus() + ")");
                }
            }
        });

        Button viewButton = new Button("View Details");
        viewButton.setDisable(true);
        viewButton.setOnAction(e -> {
            Reservation selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation-dialog.fxml"));
                    Parent rootDialog = loader.load();
                    ReservationDialogController controller = loader.getController();
                    controller.setReservationRepository(reservationRepository);
                    controller.setReservation(selected);
                    controller.setViewOnly(true);
                    Stage resStage = new Stage();
                    resStage.setTitle("View Reservation");
                    resStage.initModality(Modality.WINDOW_MODAL);
                    resStage.initOwner(dialogStage);
                    controller.setDialogStage(resStage);
                    resStage.setScene(new Scene(rootDialog));
                    resStage.showAndWait();
                } catch (IOException ex) {
                    showAlert("Error", "Could not load the reservation dialog: " + ex.getMessage());
                }
            }
        });
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewButton.setDisable(newVal == null);
        });

        VBox vbox = new VBox(10, new Label("Reservations for this date:"), listView, viewButton);
        vbox.setStyle("-fx-padding: 15;");
        dialogStage.setScene(new Scene(vbox, 350, 350));
        dialogStage.showAndWait();
    }
}