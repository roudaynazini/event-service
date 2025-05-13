package org.example.event_project;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;




import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.control.Tooltip;
import org.example.event_project.entities.Contract;
import org.example.event_project.entities.Reservation;
import org.example.event_project.entities.User;
import org.example.event_project.repository.*;
import org.example.event_project.repository.impl.MySQLContractRepository;
import org.example.event_project.repository.impl.NotificationRepositoryImpl;
import org.example.event_project.service.UserService;

public class MainViewController {

    @FXML
    private VBox reservationsSection;
    @FXML
    private VBox contractsSection;
    @FXML
    private Text statusText;
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
    private TableColumn<Reservation, String> notesColumn;
    @FXML
    private TextField reservationSearchField;
    @FXML
    private ComboBox<String> reservationStatusFilter;
    @FXML
    private DatePicker reservationDateFilter;

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
    private TableColumn<Contract, String> contractReservationIdColumn;
    @FXML
    private TableColumn<Contract, String> contractTermsColumn;
    @FXML
    private TextField contractSearchField;
    @FXML
    private ComboBox<String> contractTypeFilter;
    @FXML
    private ComboBox<String> contractStatusFilter;

    private ReservationRepository reservationRepository;
    private ContractRepository contractRepository;
    private UserRepository userRepository;

    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ObservableList<Contract> contractList = FXCollections.observableArrayList();
    
    private FilteredList<Reservation> filteredReservations;
    private FilteredList<Contract> filteredContracts;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private User currentUser;

    @FXML
    private TableView<User> userTable;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;

    private UserService userService;

    @FXML
    private VBox menuVBox;

    @FXML
    private ListView<String> notificationListView;
    @FXML
    private VBox notificationBox;

    @FXML
    private StackPane notificationIconPane;
    @FXML
    private ImageView notificationBell;
    @FXML
    private Circle notificationBadge;
    @FXML
    private Label notificationBadgeLabel;

    private int unreadNotifications = 0;
    private final ObservableList<String> notifications = FXCollections.observableArrayList();

    @FXML
    private PieChart reservationPieChart;
    @FXML
    private BarChart<String, Number> reservationBarChart;

    private NotificationRepository notificationRepository = new NotificationRepositoryImpl();

    @FXML
    public void initialize() {
        // Initialize repositories
        userRepository = new MySQLUserRepository();
        contractRepository = new MySQLContractRepository();
        
        // Create default users if none exist
        createDefaultUser();
        
        // Initialize filtered lists
        filteredReservations = new FilteredList<>(reservationList);
        filteredContracts = new FilteredList<>(contractList);
        
        // Bind filtered lists to tables
        reservationTable.setItems(filteredReservations);
        contractTable.setItems(filteredContracts);
        
        // Initialize reservation table columns
        idColumn.setCellValueFactory(cellData -> {
            Reservation reservation = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(reservation.getId()));
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getEventDate();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Initialize contract table columns
        contractIdColumn.setCellValueFactory(cellData -> {
            Contract contract = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(contract.getId()));
        });
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
        contractReservationIdColumn.setCellValueFactory(cellData -> {
            Contract contract = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(contract.getReservationId()));
        });
        contractTermsColumn.setCellValueFactory(new PropertyValueFactory<>("terms"));

        // Initialize filter ComboBoxes with values
        reservationStatusFilter.setItems(FXCollections.observableArrayList(
            "En attente", "Confirmé", "Annulé", "Terminé"
        ));
        
        contractTypeFilter.setItems(FXCollections.observableArrayList(
            "Standard", "Premium", "VIP", "Personnalisé"
        ));
        
        contractStatusFilter.setItems(FXCollections.observableArrayList(
            "Brouillon", "Actif", "Expiré", "Résilié"
        ));

        // Set up search field listeners with immediate filtering
        reservationSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateReservationFilters();
        });

        contractSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateContractFilters();
        });

        // Set up filter listeners with immediate filtering
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

        // Show reservations section by default
        reservationsSection.setVisible(true);
        contractsSection.setVisible(false);

        // Add a listener to update UI when the scene is ready
        reservationsSection.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                updateUIForUserRole();
            }
        });

        if (notificationIconPane != null) {
            notificationIconPane.setOnMouseClicked(event -> showNotificationPopup());
        }
        updateNotificationBadge();
    }

    private void createDefaultUser() {
        System.out.println("Starting createDefaultUser...");
        try {
            // Check if admin user exists
            User adminUser = userRepository.findByUsername("admin");
            System.out.println("Admin user exists: " + (adminUser != null));
            
            if (adminUser == null) {
                adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword("admin123");
                adminUser.setEmail("admin@example.com");
                adminUser.setRole("ADMIN");
                adminUser = userRepository.save(adminUser);
                System.out.println("Created admin user with ID: " + (adminUser != null ? adminUser.getId() : "null"));
            }

            // Check if regular user exists
            User regularUser = userRepository.findByUsername("user");
            System.out.println("Regular user exists: " + (regularUser != null));
            
            if (regularUser == null) {
                regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setPassword("user123");
                regularUser.setEmail("user@example.com");
                regularUser.setRole("USER");
                regularUser = userRepository.save(regularUser);
                System.out.println("Created regular user with ID: " + (regularUser != null ? regularUser.getId() : "null"));
            }

            // Verify all users in database
            List<User> allUsers = userRepository.findAll();
            System.out.println("Total users in database: " + allUsers.size());
            for (User user : allUsers) {
                System.out.println("User: " + user.getUsername() + ", Role: " + user.getRole());
            }

            // Set current user to admin by default
            currentUser = adminUser;
            updateStatusBar("Default users created successfully");
            System.out.println("Default user creation completed successfully");
        } catch (Exception e) {
            System.err.println("Error in createDefaultUser: " + e.getMessage());
            e.printStackTrace();
            updateStatusBar("Error creating default users: " + e.getMessage());
        }
    }

    public void setRepositories(ReservationRepository reservationRepository, ContractRepository contractRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
        loadData();
    }

    private void loadData() {
        if (reservationRepository != null) {
            loadReservations();
        }
        if (contractRepository != null) {
            loadContracts();
        }
    }

    private void loadReservations() {
        // Clear existing items first
        reservationList.clear();
        
        // Load new items
        List<Reservation> reservations = reservationRepository.findAll();
        reservationList.setAll(reservations);
        
        // Initialize filtered list
        if (filteredReservations == null) {
            filteredReservations = new FilteredList<>(reservationList, p -> true);
            SortedList<Reservation> sortedData = new SortedList<>(filteredReservations);
            sortedData.comparatorProperty().bind(reservationTable.comparatorProperty());
            reservationTable.setItems(sortedData);
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
        final String searchText = reservationSearchField.getText().toLowerCase();
        final String selectedStatus = reservationStatusFilter.getValue();
        final LocalDate selectedDate = reservationDateFilter.getValue();
        
        // Map French status to English
        final String englishStatus;
        if (selectedStatus != null) {
            switch (selectedStatus) {
                case "En attente":
                    englishStatus = "Pending";
                    break;
                case "Confirmé":
                    englishStatus = "Confirmed";
                    break;
                case "Annulé":
                    englishStatus = "Cancelled";
                    break;
                case "Terminé":
                    englishStatus = "Completed";
                    break;
                default:
                    englishStatus = null;
            }
        } else {
            englishStatus = null;
        }
        
        filteredReservations.setPredicate(reservation -> {
            boolean matchesSearch = searchText.isEmpty() ||
                reservation.getClientName().toLowerCase().contains(searchText) ||
                String.valueOf(reservation.getId()).contains(searchText);
                
            boolean matchesStatus = englishStatus == null || 
                englishStatus.isEmpty() || 
                reservation.getStatus().equals(englishStatus);
                
            boolean matchesDate = selectedDate == null ||
                reservation.getEventDate().equals(selectedDate);
                
            return matchesSearch && matchesStatus && matchesDate;
        });
    }

    private void updateContractFilters() {
        if (filteredContracts == null) return;
        
        final String searchText = contractSearchField.getText().toLowerCase();
        final String selectedType = contractTypeFilter.getValue();
        final String selectedStatus = contractStatusFilter.getValue();
        
        // Map French contract type to English
        final String englishType;
        if (selectedType != null) {
            switch (selectedType) {
                case "Standard":
                    englishType = "STD";
                    break;
                case "Premium":
                    englishType = "PRM";
                    break;
                case "VIP":
                    englishType = "VIP";
                    break;
                case "Personnalisé":
                    englishType = "CST";
                    break;
                default:
                    englishType = null;
            }
        } else {
            englishType = null;
        }
        
        filteredContracts.setPredicate(contract -> {
            // Search text filter
            boolean matchesSearch = searchText.isEmpty() ||
                                  contract.getContractNumber().toLowerCase().contains(searchText);
            
            // Type filter
            boolean matchesType = englishType == null || englishType.isEmpty() ||
                                contract.getContractType().equals(englishType);
            
            // Status filter
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
        // Clear all filter fields
        reservationSearchField.clear();
        reservationStatusFilter.setValue(null);
        reservationDateFilter.setValue(null);
        
        // Reset the filtered list to show all items
        filteredReservations.setPredicate(reservation -> true);
        
        // Reload the data to ensure we have the latest state
        loadReservations();
        
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
        contractStatusFilter.setValue(null);
        updateContractFilters();
        updateStatusBar("Filtres effacés");
    }

    @FXML
    private void handleNewReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation-dialog.fxml"));

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Reservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reservationTable.getScene().getWindow());

            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            ReservationDialogController controller = loader.getController();
            controller.setReservationRepository(reservationRepository);
            controller.setDialogStage(dialogStage);
            controller.setCurrentUser(currentUser);
            controller.setMainViewController(this);

            // Show the dialog and wait for it to close
            dialogStage.showAndWait();

            // Only reload if a reservation was actually added
            if (controller.isOkClicked()) {
                loadReservations();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the dialog window.");
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
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            showAlert("Access Denied", "Only administrators can add contracts.");
            return;
        }

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
            showAlert("Error", "Error opening add contract dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditContract() {
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            showAlert("Access Denied", "Only administrators can edit contracts.");
            return;
        }
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
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            showAlert("Access Denied", "Only administrators can delete contracts.");
            return;
        }
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
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            showAlert("Access Denied", "Only administrators can view contracts.");
            return;
        }

        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("No Selection", "Please select a reservation to view its contracts.");
            return;
        }
        
        try {
            // Switch to contracts tab
            contractsSection.setVisible(true);
            reservationsSection.setVisible(false);
            
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
            reservationsSection.setVisible(true);
            contractsSection.setVisible(false);
            
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

    @FXML
    private void handleShowReservations() {
        reservationsSection.setVisible(true);
        contractsSection.setVisible(false);
        updateStatusBar("Showing Reservations");
    }

    @FXML
    private void handleShowContracts() {
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            try {
                loadContracts();
                reservationsSection.setVisible(false);
                contractsSection.setVisible(true);
                updateStatusBar("Showing Contracts");
            } catch (Exception e) {
                updateStatusBar("Error loading contracts: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("Access Denied", "Only administrators can view contracts.");
        }
    }

    @FXML
    private void handleSwitchRole() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-dialog.fxml"));
            Parent root = loader.load();
            LoginDialogController controller = loader.getController();
            controller.setUserRepository(userRepository);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Switch User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            if (controller.isLoginSuccessful()) {
                User newUser = controller.getLoggedInUser();
                setCurrentUser(newUser);
                String roleMessage = "ADMIN".equals(newUser.getRole()) ? "Admin Connected" : "User Connected";
                System.out.println(roleMessage + " - " + newUser.getUsername());
                updateStatusBar("Logged in as: " + newUser.getUsername() + " (" + newUser.getRole() + ")");
            }
        } catch (IOException e) {
            updateStatusBar("Error opening login dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowCalendar() {
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            showAlert("Access Denied", "Only administrators can access the calendar view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("calendar-view.fxml"));
            Parent root = loader.load();
            CalendarViewController controller = loader.getController();
            controller.setReservationRepository(reservationRepository);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reservation Calendar");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reservationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the calendar view: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowStatistics() {
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            showAlert("Access Denied", "Only administrators can access the statistics view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("statistics-view.fxml"));
            Parent root = loader.load();
            StatisticsViewController controller = loader.getController();
            controller.setReservationRepository(reservationRepository);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reservation Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reservationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the statistics view: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportContractToPDF() {
        Contract selectedContract = contractTable.getSelectionModel().getSelectedItem();
        if (selectedContract == null) {
            showAlert("No Selection", "Please select a contract to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("contract_" + selectedContract.getId() + ".pdf");

        File file = fileChooser.showSaveDialog(contractTable.getScene().getWindow());
        if (file != null) {
            try {
                PDFExporter.exportContractToPDF(selectedContract, file.getAbsolutePath());
                updateStatusBar("Contract exported to PDF successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to export contract: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void updateStatusBar(String message) {
        if (statusText != null) {
            statusText.setText(message);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUIForUserRole();
        // Load notifications for admin
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            loadAdminNotifications();
        }
    }

    private void updateUIForUserRole() {
        if (currentUser == null) {
            // Hide all sections if no user is logged in
            reservationsSection.setVisible(false);
            contractsSection.setVisible(false);
            if (notificationIconPane != null) notificationIconPane.setVisible(false);
            return;
        }

        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        
        // Always show reservations section
        reservationsSection.setVisible(true);
        
        // Show/hide contracts section based on user role
        contractsSection.setVisible(false); // Always start with contracts hidden
        
        // Update menu buttons visibility
        if (menuVBox != null) {
            for (Node node : menuVBox.getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    if ("Contracts".equals(button.getText())) {
                        button.setVisible(isAdmin);
                        button.setManaged(isAdmin);
                    }
                }
            }
        }

        // Hide admin-only buttons for non-admin users
        if (reservationsSection != null) {
            reservationsSection.lookupAll(".admin-only").forEach(node -> {
                node.setVisible(isAdmin);
                node.setManaged(isAdmin); // This prevents the space from being reserved
            });
        }
        
        // Update status bar
        updateStatusBar("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        // Show/hide notification icon based on admin role
        if (notificationIconPane != null) {
            notificationIconPane.setVisible(isAdmin);
            notificationIconPane.setManaged(isAdmin);
            if (!isAdmin) {
                unreadNotifications = 0;
                updateNotificationBadge();
            }
        }
    }

    private void generateContractForReservation(Reservation reservation) {
        Contract contract = new Contract();
        contract.setReservationId(Long.valueOf(reservation.getId()));
        contract.setContractNumber("CON-" + System.currentTimeMillis());
        contract.setContractType("Standard");
        contract.setStatus("Brouillon");
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(reservation.getEventDate());
        contract.setTotalAmount(0.0);
        contract.setTerms("");
        contract.setNotes("Generated from reservation " + reservation.getId());
        
        if (showContractDialog(contract)) {
            try {
                contractRepository.save(contract);
                loadData();
                updateStatusBar("Contract generated successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to generate contract: " + e.getMessage());
            }
        }
    }

    private void showContractDetails(Contract contract) {
        if (contract != null) {
            StringBuilder details = new StringBuilder();
            details.append("Contract Number: ").append(contract.getContractNumber()).append("\n");
            details.append("Status: ").append(contract.getStatus()).append("\n");
            details.append("Start Date: ").append(contract.getStartDate()).append("\n");
            details.append("End Date: ").append(contract.getEndDate()).append("\n");
            details.append("Total Amount: ").append(contract.getTotalAmount()).append("\n");
            details.append("Notes: ").append(contract.getNotes());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contract Details");
            alert.setHeaderText(null);
            alert.setContentText(details.toString());
            alert.showAndWait();
        }
    }

    private boolean showContractDialog(Contract contract) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("contract-dialog.fxml"));
            Parent root = loader.load();
            ContractDialogController controller = loader.getController();
            controller.setContractRepository(contractRepository);
            controller.setReservationRepository(reservationRepository);
            controller.setContract(contract);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(contract.getId() == null ? "Add Contract" : "Edit Contract");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            
            return controller.isOkClicked();
        } catch (IOException e) {
            showAlert("Error", "Failed to open contract dialog: " + e.getMessage());
            return false;
        }
    }

    private void refreshContractsTable() {
        // Implementation of refreshContractsTable method
    }

    public void setContractRepository(ContractRepository repository) {
        this.contractRepository = repository;
    }

    public void setReservationRepository(ReservationRepository repository) {
        this.reservationRepository = repository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void addNotification(String message) {
        notificationRepository.addNotification(message);
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            notifications.add(0, message);
            unreadNotifications++;
            updateNotificationBadge();
        }
    }

    private void updateNotificationBadge() {
        if (notificationBadge != null && notificationBadgeLabel != null) {
            boolean hasUnread = unreadNotifications > 0;
            notificationBadge.setVisible(hasUnread);
            notificationBadgeLabel.setVisible(hasUnread);
            notificationBadgeLabel.setText(String.valueOf(unreadNotifications));
        }
    }

    private void showNotificationPopup() {
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return;
        }
        ListView<String> popupList = new ListView<>(notifications);
        popupList.setPrefWidth(300);
        popupList.setPrefHeight(200);
        Popup popup = new Popup();
        popup.getContent().add(popupList);
        popup.setAutoHide(true);
        popup.show(notificationIconPane.getScene().getWindow());
        unreadNotifications = 0;
        updateNotificationBadge();
        notificationRepository.markAllAsRead();
    }

    private void loadAdminNotifications() {
        notifications.clear();
        List<String> dbNotifications = notificationRepository.getUnreadNotifications();
        notifications.addAll(dbNotifications);
        unreadNotifications = dbNotifications.size();
        updateNotificationBadge();
    }
}