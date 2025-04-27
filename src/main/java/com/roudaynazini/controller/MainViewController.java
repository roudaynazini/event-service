package com.roudaynazini.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import com.roudaynazini.model.Reservation;
import com.roudaynazini.repository.ReservationRepository;

public class MainViewController {
    @FXML private TextField reservationSearchField;
    @FXML private ComboBox<String> reservationStatusFilter;
    @FXML private DatePicker reservationDateFilter;
    @FXML private TableView<Reservation> reservationTable;
    
    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private FilteredList<Reservation> filteredReservations;
    
    private void updateReservationFilters() {
        String searchText = reservationSearchField.getText().toLowerCase();
        String selectedStatus = reservationStatusFilter.getValue();
        LocalDate selectedDate = reservationDateFilter.getValue();

        reservationTable.setItems(reservationList.filtered(reservation -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    reservation.getClientName().toLowerCase().contains(searchText) ||
                    String.valueOf(reservation.getId()).contains(searchText);

            boolean matchesStatus = selectedStatus == null || selectedStatus.isEmpty() ||
                    reservation.getStatus().equals(selectedStatus);

            boolean matchesDate = selectedDate == null ||
                    reservation.getEventDate().equals(selectedDate);

            return matchesSearch && matchesStatus && matchesDate;
        }));
    } 
} 