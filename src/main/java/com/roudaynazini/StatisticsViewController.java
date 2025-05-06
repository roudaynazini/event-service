package com.roudaynazini;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.roudaynazini.repository.ReservationRepository;
import com.roudaynazini.model.Reservation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsViewController {
    @FXML private Label totalReservationsLabel;
    @FXML private Label confirmedReservationsLabel;
    @FXML private Label pendingReservationsLabel;
    @FXML private Label cancelledReservationsLabel;
    @FXML private Label completedReservationsLabel;
    @FXML private PieChart reservationPieChart;
    @FXML private BarChart<String, Number> reservationBarChart;

    private ReservationRepository reservationRepository;
    private Stage dialogStage;

    public void setReservationRepository(ReservationRepository repository) {
        this.reservationRepository = repository;
        updateStatistics();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    private void updateStatistics() {
        List<Reservation> reservations = reservationRepository.findAll();
        
        // Update summary labels
        totalReservationsLabel.setText("Total: " + reservations.size());
        confirmedReservationsLabel.setText("Confirmed: " + countByStatus(reservations, "Confirmed"));
        pendingReservationsLabel.setText("Pending: " + countByStatus(reservations, "Pending"));
        cancelledReservationsLabel.setText("Cancelled: " + countByStatus(reservations, "Cancelled"));
        completedReservationsLabel.setText("Completed: " + countByStatus(reservations, "Completed"));

        // Update Pie Chart
        reservationPieChart.getData().clear();
        Map<String, Long> statusCounts = reservations.stream()
            .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.counting()));
        
        statusCounts.forEach((status, count) -> {
            PieChart.Data slice = new PieChart.Data(status, count);
            reservationPieChart.getData().add(slice);
        });

        // Update Bar Chart
        reservationBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations by Status");
        
        statusCounts.forEach((status, count) -> {
            series.getData().add(new XYChart.Data<>(status, count));
        });
        
        reservationBarChart.getData().add(series);

        // Set bar colors to match PieChart
        for (XYChart.Data<String, Number> data : series.getData()) {
            String status = data.getXValue();
            final String color;
            switch (status) {
                case "Completed":
                    color = "#FF8C69"; // Orange
                    break;
                case "Cancelled":
                    color = "#FFD700"; // Yellow
                    break;
                case "Confirmed":
                    color = "#3CB371"; // Green
                    break;
                case "Pending":
                    color = "#40BFFF"; // Blue
                    break;
                default:
                    color = "#FF8C69"; // Default to Completed color
            }
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            }
        }
    }

    private long countByStatus(List<Reservation> reservations, String status) {
        return reservations.stream()
            .filter(r -> status.equals(r.getStatus()))
            .count();
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
} 