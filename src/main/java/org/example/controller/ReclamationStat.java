package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.sevice.servicereclamatin;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

public class ReclamationStat {

    @FXML
    private LineChart<String, Number> chartReclamations;

    private final servicereclamatin service = new servicereclamatin();

    @FXML
    public void initialize() {
        try {
            Map<Date, Integer> stats = service.getReclamationsParJour();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Réclamations par jour");

            for (Map.Entry<Date, Integer> entry : stats.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
            }

            chartReclamations.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

