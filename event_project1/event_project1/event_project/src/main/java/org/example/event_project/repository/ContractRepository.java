package org.example.event_project.repository;

import org.example.event_project.entities.Contract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository {
    // Create
    Contract save(Contract contract);
    
    // Read
    Optional<Contract> findById(Long id);
    List<Contract> findAll();
    List<Contract> findByReservationId(Long reservationId);
    List<Contract> findByContractNumber(String contractNumber);
    List<Contract> findByStatus(String status);
    
    // Update
    Contract update(Contract contract);
    
    // Delete
    void deleteById(Long id);
    
    // Search and Filter
    List<Contract> findByDateRange(LocalDate startDate, LocalDate endDate);
} 