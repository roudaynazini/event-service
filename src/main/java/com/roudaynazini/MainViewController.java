package com.roudaynazini;

import com.roudaynazini.model.Contract;
import com.roudaynazini.model.Reservation;
import com.roudaynazini.repository.ContractRepository;
import com.roudaynazini.repository.ReservationRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainViewController {

    @FXML
    private TabPane mainTabPane;

    // Reservation UI elements
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
    private TextField reservationSearchField;
    @FXML
    private ComboBox<String> reservationStatusFilter;
    @FXML
    private DatePicker reservationDateFilter;

    // Contract UI elements
    @FXML
    private TableView<Contract> contractTable;
    @FXML
    private TableColumn<Contract, String> contractIdColumn;
    @FXML
    private TableColumn<Contract, String> contractNumberColumn;
    @FXML
    private TableColumn<Contract, String> contractTypeColumn;
    @FXML
    private TableColumn<Contract, String> contractStatusColumn;
    @FXML
    private TableColumn<Contract, String> contractStartDateColumn;
    @FXML
    private TableColumn<Contract, String> contractEndDateColumn;
    @FXML
    private TextField contractSearchField;
    @FXML
    private ComboBox<String> contractTypeFilter;
    @FXML
    private ComboBox<String> contractStatusFilter;

    // Status bar
    @FXML
    private Text statusText;

    // Repositories
    private ReservationRepository reservationRepository;
    private ContractRepository contractRepository;

    // Observable lists for tables
    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ObservableList<Contract> contractList = FXCollections.observableArrayList();
    
    // Filtered lists for search and filtering
    private FilteredList<Reservation> filteredReservations;
    private FilteredList<Contract> filteredContracts;

    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Initialize reservation table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getEventDate();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize contract table columns
        contractIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contractNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
        contractTypeColumn.setCellValueFactory(new PropertyValueFactory<>("contractType"));
        contractStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        contractStartDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getStartDate();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        contractEndDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getEndDate();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });

        // Set up search field listeners
        reservationSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateReservationFilters();
        });

        contractSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateContractFilters();
        });

        // Set up filter listeners
        reservationStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateReservationFilters();
        });

        reservationDateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateReservationFilters();
        });

        contractTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateContractFilters();
        });

        contractStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateContractFilters();
        });

        // Set up tab change listener
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getText().equals("Reservations")) {
                    loadReservations();
                } else if (newValue.getText().equals("Contracts")) {
                    loadContracts();
                }
            }
        });
    }

    public void setRepositories(ReservationRepository reservationRepository, ContractRepository contractRepository) {
        this.reservationRepository = reservationRepository;
        this.contractRepository = contractRepository;
        
        // Load initial data
        loadData();
    }

    private void loadData() {
        try {
            // Load reservations
            List<Reservation> reservations = reservationRepository.findAll();
            reservationList.setAll(reservations);
            
            // Set up filtered list for reservations
            filteredReservations = new FilteredList<>(reservationList, p -> true);
            SortedList<Reservation> sortedReservations = new SortedList<>(filteredReservations);
            sortedReservations.comparatorProperty().bind(reservationTable.comparatorProperty());
            reservationTable.setItems(sortedReservations);
            
            // Initialize status filter with predefined values
            reservationStatusFilter.setItems(FXCollections.observableArrayList(
                "Pending", "Confirmed", "Cancelled", "Completed"
            ));
            
            // Load contracts
            List<Contract> contracts = contractRepository.findAll();
            contractList.setAll(contracts);
            
            // Set up filtered list for contracts
            filteredContracts = new FilteredList<>(contractList, p -> true);
            SortedList<Contract> sortedContracts = new SortedList<>(filteredContracts);
            sortedContracts.comparatorProperty().bind(contractTable.comparatorProperty());
            contractTable.setItems(sortedContracts);
            
            // Initialize contract type filter with predefined values
            contractTypeFilter.setItems(FXCollections.observableArrayList(
                "Standard", "Premium", "Custom"
            ));
            
            // Initialize contract status filter with predefined values
            contractStatusFilter.setItems(FXCollections.observableArrayList(
                "Draft", "Active", "Expired", "Terminated"
            ));
            
            updateStatusBar("Data loaded successfully");
        } catch (Exception e) {
            updateStatusBar("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationRepository.findAll();
            reservationList.setAll(reservations);
            updateStatusBar("Reservations loaded successfully");
        } catch (Exception e) {
            updateStatusBar("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadContracts() {
        try {
            List<Contract> contracts = contractRepository.findAll();
            contractList.setAll(contracts);
            updateStatusBar("Contracts loaded successfully");
        } catch (Exception e) {
            updateStatusBar("Error loading contracts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateReservationFilters() {
        String searchText = reservationSearchField.getText().toLowerCase();
        String selectedStatus = reservationStatusFilter.getValue();
        LocalDate selectedDate = reservationDateFilter.getValue();
        
        filteredReservations.setPredicate(reservation -> {
            boolean matchesSearch = searchText.isEmpty() ||
                reservation.getClientName().toLowerCase().contains(searchText) ||
                String.valueOf(reservation.getId()).contains(searchText);
                
            boolean matchesStatus = selectedStatus == null || 
                selectedStatus.isEmpty() || 
                reservation.getStatus().equals(selectedStatus);
                
            boolean matchesDate = selectedDate == null ||
                reservation.getEventDate().equals(selectedDate);
                
            return matchesSearch && matchesStatus && matchesDate;
        });
    }

    private void updateContractFilters() {
        if (filteredContracts == null) return;
        
        filteredContracts.setPredicate(contract -> {
            // Search text filter
            String searchText = contractSearchField.getText().toLowerCase();
            boolean matchesSearch = contract.getContractNumber().toLowerCase().contains(searchText);
            
            // Type filter
            String selectedType = contractTypeFilter.getValue();
            boolean matchesType = selectedType == null || selectedType.isEmpty() || 
                                contract.getContractType().equals(selectedType);
            
            // Status filter
            String selectedStatus = contractStatusFilter.getValue();
            boolean matchesStatus = selectedStatus == null || selectedStatus.isEmpty() || 
                                  contract.getStatus().equals(selectedStatus);
            
            return matchesSearch && matchesType && matchesStatus;
        });
    }

    @FXML
    private void handleApplyReservationFilters() {
        updateReservationFilters();
        updateStatusBar("Reservation filters applied");
    }

    @FXML
    private void handleClearReservationFilters() {
        reservationSearchField.clear();
        reservationStatusFilter.setValue(null);
        reservationStatusFilter.setPromptText("Filter by status");
        reservationDateFilter.setValue(null);
        updateReservationFilters();
        updateStatusBar("Reservation filters cleared");
    }

    @FXML
    private void handleApplyContractFilters() {
        updateContractFilters();
        updateStatusBar("Contract filters applied");
    }

    @FXML
    private void handleClearContractFilters() {
        contractSearchField.clear();
        contractTypeFilter.setValue(null);
        contractTypeFilter.setPromptText("Filter by type");
        contractStatusFilter.setValue(null);
        contractStatusFilter.setPromptText("Filter by status");
        updateContractFilters();
        updateStatusBar("Contract filters cleared");
    }

    @FXML
    private void handleAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation-dialog.fxml"));
            Parent root = loader.load();
            ReservationDialogController controller = loader.getController();
            controller.setReservationRepository(reservationRepository);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Reservation");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                loadReservations();
                updateStatusBar("Reservation added successfully");
            }
        } catch (IOException e) {
            updateStatusBar("Error opening add reservation dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("No Selection", "Please select a reservation to edit.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation-dialog.fxml"));
            Parent root = loader.load();
            ReservationDialogController controller = loader.getController();
            controller.setReservationRepository(reservationRepository);
            controller.setReservation(selectedReservation);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Reservation");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                loadReservations();
                updateStatusBar("Reservation updated successfully");
            }
        } catch (IOException e) {
            updateStatusBar("Error opening edit reservation dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("No Selection", "Please select a reservation to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Reservation");
        alert.setContentText("Are you sure you want to delete this reservation? This will also delete all associated contracts.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationRepository.deleteById(selectedReservation.getId());
                loadReservations();
                updateStatusBar("Reservation deleted successfully");
            } catch (Exception e) {
                updateStatusBar("Error deleting reservation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddContract() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("contract-dialog.fxml"));
            Parent root = loader.load();
            ContractDialogController controller = loader.getController();
            controller.setContractRepository(contractRepository);
            controller.setReservationRepository(reservationRepository);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Contract");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                loadContracts();
                updateStatusBar("Contract added successfully");
            }
        } catch (IOException e) {
            updateStatusBar("Error opening add contract dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditContract() {
        Contract selectedContract = contractTable.getSelectionModel().getSelectedItem();
        if (selectedContract == null) {
            showAlert("No Selection", "Please select a contract to edit.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("contract-dialog.fxml"));
            Parent root = loader.load();
            ContractDialogController controller = loader.getController();
            controller.setContractRepository(contractRepository);
            controller.setReservationRepository(reservationRepository);
            controller.setContract(selectedContract);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Contract");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                loadContracts();
                updateStatusBar("Contract updated successfully");
            }
        } catch (IOException e) {
            updateStatusBar("Error opening edit contract dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteContract() {
        Contract selectedContract = contractTable.getSelectionModel().getSelectedItem();
        if (selectedContract == null) {
            showAlert("No Selection", "Please select a contract to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Contract");
        alert.setContentText("Are you sure you want to delete this contract?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                contractRepository.deleteById(selectedContract.getId());
                loadContracts();
                updateStatusBar("Contract deleted successfully");
            } catch (Exception e) {
                updateStatusBar("Error deleting contract: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleViewContractsForReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("No Selection", "Please select a reservation to view its contracts.");
            return;
        }
        
        try {
            // Switch to contracts tab
            mainTabPane.getSelectionModel().select(1); // Index 1 is the Contracts tab
            
            // Apply filter to show only contracts for this reservation
            contractSearchField.clear();
            contractTypeFilter.setValue(null);
            contractStatusFilter.setValue(null);
            
            // Filter contracts by reservation ID
            filteredContracts.setPredicate(contract -> 
                contract.getReservationId() == selectedReservation.getId());
            
            updateStatusBar("Showing contracts for reservation: " + selectedReservation.getClientName());
        } catch (Exception e) {
            updateStatusBar("Error viewing contracts for reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewReservationForContract() {
        Contract selectedContract = contractTable.getSelectionModel().getSelectedItem();
        if (selectedContract == null) {
            showAlert("No Selection", "Please select a contract to view its reservation.");
            return;
        }
        
        try {
            // Switch to reservations tab
            mainTabPane.getSelectionModel().select(0); // Index 0 is the Reservations tab
            
            // Apply filter to show only this reservation
            reservationSearchField.clear();
            reservationStatusFilter.setValue(null);
            reservationDateFilter.setValue(null);
            
            // Filter reservations by ID
            filteredReservations.setPredicate(reservation -> 
                reservation.getId() == selectedContract.getReservationId());
            
            updateStatusBar("Showing reservation for contract: " + selectedContract.getContractNumber());
        } catch (Exception e) {
            updateStatusBar("Error viewing reservation for contract: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateStatusBar(String message) {
        statusText.setText(message);
    }
} 