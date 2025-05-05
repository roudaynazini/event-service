package com.example.bouthaina.BackOffice.controllers;

import com.example.bouthaina.BackOffice.models.Categorie;
import com.example.bouthaina.BackOffice.models.Evenement;
import com.example.bouthaina.BackOffice.services.CategorieService;
import com.example.bouthaina.BackOffice.services.EvenementService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsController implements Initializable {

    // Native JavaFX charts instead of WebViews
    @FXML
    private PieChart eventsStatusChart;

    @FXML
    private PieChart eventsDistributionChart;

    @FXML
    private BarChart<String, Number> categoriesChart;

    @FXML
    private LineChart<String, Number> timelineChart;

    @FXML
    private TableView<Map.Entry<String, String>> eventStatsTable;

    @FXML
    private TableColumn<Map.Entry<String, String>, String> statLabelColumn;

    @FXML
    private TableColumn<Map.Entry<String, String>, String> statValueColumn;

    @FXML
    private TableView<CategoryStat> categoryStatsTable;

    @FXML
    private TableColumn<CategoryStat, String> categoryNameColumn;

    @FXML
    private TableColumn<CategoryStat, Integer> categoryCountColumn;

    @FXML
    private TableColumn<CategoryStat, String> categoryPercentColumn;

    @FXML
    private ComboBox<String> periodComboBox;

    @FXML
    private Button closeButton;

    @FXML
    private Button refreshButton;

    private EvenementService evenementService;
    private CategorieService categorieService;
    private List<Evenement> allEvents;
    private List<Categorie> allCategories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        evenementService = new EvenementService();
        categorieService = new CategorieService();

        // Initialize table columns
        statLabelColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        statValueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));

        categoryNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCategoryName()));
        categoryCountColumn
                .setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getEventCount()).asObject());
        categoryPercentColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPercentage()));

        // Setup charts appearance
        setupChartsAppearance();

        // Setup period combo box options
        setupPeriodComboBox();

        // Load data and generate charts
        loadData();
        generateAllCharts();
    }

    private void setupChartsAppearance() {
        // Configure pie charts
        eventsStatusChart.setLabelsVisible(true);
        eventsStatusChart.setLegendSide(Side.RIGHT);
        eventsStatusChart.setStartAngle(90);

        eventsDistributionChart.setLabelsVisible(true);
        eventsDistributionChart.setLegendSide(Side.RIGHT);
        eventsDistributionChart.setStartAngle(90);

        // Configure bar chart
        categoriesChart.setAnimated(true);
        categoriesChart.setLegendVisible(false);

        // Configure y-axis to display integer values only (no decimals)
        NumberAxis yAxis = (NumberAxis) categoriesChart.getYAxis();
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        // Configure line chart
        timelineChart.setAnimated(true);
        timelineChart.setCreateSymbols(true);
    }

    private void setupPeriodComboBox() {
        ObservableList<String> periods = FXCollections.observableArrayList(
                "30 derniers jours",
                "3 derniers mois",
                "6 derniers mois",
                "Année courante",
                "Toutes les données");

        periodComboBox.setItems(periods);
        periodComboBox.getSelectionModel().select(2); // Select 6 months by default
    }

    private void loadData() {
        allEvents = evenementService.getAll();
        allCategories = categorieService.getAll();

        // Populate event stats table
        populateEventStatsTable();

        // Populate category stats table
        populateCategoryStatsTable();
    }

    private void populateEventStatsTable() {
        Map<String, String> eventStats = generateEventStats();
        ObservableList<Map.Entry<String, String>> items = FXCollections.observableArrayList(eventStats.entrySet());
        eventStatsTable.setItems(items);
    }

    private Map<String, String> generateEventStats() {
        Map<String, String> stats = new LinkedHashMap<>();

        int totalEvents = allEvents.size();
        long activeEvents = allEvents.stream().filter(e -> e.getStatus().equals("Actif")).count();
        long inactiveEvents = allEvents.stream().filter(e -> e.getStatus().equals("Inactif")).count();
        long pastEvents = allEvents.stream()
                .filter(e -> e.getDateFin().isBefore(LocalDate.now()))
                .count();
        long upcomingEvents = allEvents.stream()
                .filter(e -> e.getDateDebut().isAfter(LocalDate.now()))
                .count();
        long currentEvents = allEvents.stream()
                .filter(e -> !e.getDateFin().isBefore(LocalDate.now()) && !e.getDateDebut().isAfter(LocalDate.now()))
                .count();

        stats.put("Nombre total d'événements", String.valueOf(totalEvents));
        stats.put("Événements actifs", String.valueOf(activeEvents));
        stats.put("Événements inactifs", String.valueOf(inactiveEvents));
        stats.put("Événements passés", String.valueOf(pastEvents));
        stats.put("Événements à venir", String.valueOf(upcomingEvents));
        stats.put("Événements en cours", String.valueOf(currentEvents));

        return stats;
    }

    private void populateCategoryStatsTable() {
        List<CategoryStat> categoryStats = generateCategoryStats();
        categoryStatsTable.setItems(FXCollections.observableArrayList(categoryStats));
    }

    private List<CategoryStat> generateCategoryStats() {
        List<CategoryStat> stats = new ArrayList<>();
        Map<Integer, Long> categoryCountMap = allEvents.stream()
                .collect(Collectors.groupingBy(Evenement::getCategorie, Collectors.counting()));

        int totalEvents = allEvents.size();

        for (Categorie category : allCategories) {
            Long count = categoryCountMap.getOrDefault(category.getIdCate(), 0L);
            double percentage = totalEvents > 0 ? (count * 100.0) / totalEvents : 0;
            String formattedPercentage = String.format("%.1f%%", percentage);

            stats.add(new CategoryStat(
                    category.getType(),
                    count.intValue(),
                    formattedPercentage));
        }

        return stats;
    }

    private void generateAllCharts() {
        generateEventsStatusChart();
        generateEventsDistributionChart();
        generateCategoriesChart();
        generateTimelineChart();
    }

    private void generateEventsStatusChart() {
        // Clear existing data
        eventsStatusChart.getData().clear();

        // Get data
        long activeEvents = allEvents.stream().filter(e -> e.getStatus().equals("Actif")).count();
        long inactiveEvents = allEvents.stream().filter(e -> e.getStatus().equals("Inactif")).count();

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Actifs", activeEvents),
                new PieChart.Data("Inactifs", inactiveEvents));

        // Set data to chart
        eventsStatusChart.setData(pieChartData);

        // Style the pie chart slices
        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #4CAF50;");
        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #F44336;");

        // Add percentage labels
        addPercentageLabels(eventsStatusChart);
    }

    private void generateEventsDistributionChart() {
        // Clear existing data
        eventsDistributionChart.getData().clear();

        // Get data
        long pastEvents = allEvents.stream()
                .filter(e -> e.getDateFin().isBefore(LocalDate.now()))
                .count();
        long upcomingEvents = allEvents.stream()
                .filter(e -> e.getDateDebut().isAfter(LocalDate.now()))
                .count();
        long currentEvents = allEvents.stream()
                .filter(e -> !e.getDateFin().isBefore(LocalDate.now()) && !e.getDateDebut().isAfter(LocalDate.now()))
                .count();

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Passés", pastEvents),
                new PieChart.Data("À venir", upcomingEvents),
                new PieChart.Data("En cours", currentEvents));

        // Set data to chart
        eventsDistributionChart.setData(pieChartData);

        // Style the pie chart slices
        pieChartData.get(0).getNode().setStyle("-fx-pie-color: #607D8B;");
        pieChartData.get(1).getNode().setStyle("-fx-pie-color: #2196F3;");
        pieChartData.get(2).getNode().setStyle("-fx-pie-color: #FF9800;");

        // Add percentage labels
        addPercentageLabels(eventsDistributionChart);
    }

    private void addPercentageLabels(PieChart chart) {
        double total = chart.getData().stream()
                .mapToDouble(PieChart.Data::getPieValue)
                .sum();

        for (PieChart.Data data : chart.getData()) {
            double percentage = total > 0 ? (data.getPieValue() / total) * 100 : 0;
            String text = String.format("%s: %.1f%%", data.getName(), percentage);

            // Set the text for the data label
            data.setName(text);
        }
    }

    private void generateCategoriesChart() {
        // Clear existing data
        categoriesChart.getData().clear();

        // Get data
        Map<Integer, Long> categoryCountMap = allEvents.stream()
                .collect(Collectors.groupingBy(Evenement::getCategorie, Collectors.counting()));

        // Create bar chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'événements");

        // Add data points for each category
        for (Categorie category : allCategories) {
            Long count = categoryCountMap.getOrDefault(category.getIdCate(), 0L);
            series.getData().add(new XYChart.Data<>(category.getType(), count));
        }

        // Add the series to the chart
        categoriesChart.getData().add(series);

        // Apply custom colors to bars
        int colorIndex = 0;
        String[] colors = generateRandomColors(allCategories.size());

        for (XYChart.Data<String, Number> item : series.getData()) {
            item.getNode().setStyle("-fx-bar-fill: " + colors[colorIndex % colors.length] + ";");
            colorIndex++;
        }
    }

    private String[] generateRandomColors(int count) {
        String[] colors = new String[count];
        for (int i = 0; i < count; i++) {
            // Generate colors in HSL format for better visual distinction
            int hue = i * (360 / Math.max(1, count));
            colors[i] = String.format("hsl(%d, 70%%, 60%%)", hue);
        }
        return colors;
    }

    @FXML
    private void refreshTimelineStats() {
        generateTimelineChart();
    }

    private void generateTimelineChart() {
        // Clear existing data
        timelineChart.getData().clear();

        // Get selected period
        String period = periodComboBox.getValue();
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        // Determine start date based on selected period
        switch (period) {
            case "30 derniers jours":
                startDate = endDate.minusDays(30);
                break;
            case "3 derniers mois":
                startDate = endDate.minusMonths(3);
                break;
            case "6 derniers mois":
                startDate = endDate.minusMonths(6);
                break;
            case "Année courante":
                startDate = LocalDate.of(endDate.getYear(), 1, 1);
                break;
            case "Toutes les données":
            default:
                startDate = allEvents.stream()
                        .map(Evenement::getDateDebut)
                        .min(LocalDate::compareTo)
                        .orElse(endDate.minusYears(1));
                break;
        }

        // Group events by month
        Map<YearMonth, Long> eventsByMonth = new TreeMap<>();

        // Initialize all months in the range with zero count
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!current.isAfter(end)) {
            eventsByMonth.put(current, 0L);
            current = current.plusMonths(1);
        }

        // Count events by month
        for (Evenement event : allEvents) {
            YearMonth eventMonth = YearMonth.from(event.getDateDebut());
            if (!eventMonth.isBefore(YearMonth.from(startDate)) && !eventMonth.isAfter(YearMonth.from(endDate))) {
                eventsByMonth.put(eventMonth, eventsByMonth.getOrDefault(eventMonth, 0L) + 1);
            }
        }

        // Create line chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'événements");

        // Format month labels and add data points
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (Map.Entry<YearMonth, Long> entry : eventsByMonth.entrySet()) {
            String monthLabel = entry.getKey().format(monthFormatter);
            series.getData().add(new XYChart.Data<>(monthLabel, entry.getValue()));
        }

        // Add the series to the chart
        timelineChart.getData().add(series);

        // Style the line and symbols
        String lineColor = "#ff8a00";
        series.getNode().setStyle("-fx-stroke: " + lineColor + "; -fx-stroke-width: 2px;");

        // Style the symbols
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-background-color: " + lineColor
                        + ", white; -fx-background-insets: 0, 2; -fx-background-radius: 5px; -fx-padding: 5px;");
            }
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // CategoryStat class for the category statistics table
    public static class CategoryStat {
        private final String categoryName;
        private final int eventCount;
        private final String percentage;

        public CategoryStat(String categoryName, int eventCount, String percentage) {
            this.categoryName = categoryName;
            this.eventCount = eventCount;
            this.percentage = percentage;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public int getEventCount() {
            return eventCount;
        }

        public String getPercentage() {
            return percentage;
        }
    }
}
