package org.example.event_project.repository;


import org.example.event_project.entities.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    // Create
    Reservation save(Reservation reservation);
    
    // Read
    Optional<Reservation> findById(Long id);
    List<Reservation> findAll();
    List<Reservation> findByClientName(String clientName);
    List<Reservation> findByEventDate(LocalDate eventDate);
    List<Reservation> findByStatus(String status);
    
    // Update
    void update(Reservation reservation);
    
    // Delete
    void deleteById(Long id);
    
    // Search and Filter
    List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Reservation> findByPaymentStatus(String paymentStatus);
    List<Reservation> findByVenue(String venue);
    
    // Dynamic Search
    List<Reservation> search(String query);
    
    // Advanced Filtering
    List<Reservation> filter(String clientName, String eventType, LocalDate startDate, 
                            LocalDate endDate, String status, String paymentStatus, 
                            String venue, Double minAmount, Double maxAmount);
} 