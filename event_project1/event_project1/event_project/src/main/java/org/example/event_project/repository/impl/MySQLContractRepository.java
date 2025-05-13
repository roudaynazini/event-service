package org.example.event_project.repository.impl;



import org.example.event_project.entities.Contract;
import org.example.event_project.repository.ContractRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLContractRepository implements ContractRepository {
    private final Connection connection;

    public MySQLContractRepository() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Contract save(Contract contract) {
        String sql = "INSERT INTO contracts (reservation_id, contract_number, contract_type, status, " +
                    "start_date, end_date, total_amount, terms, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setContractParameters(stmt, contract);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating contract failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contract.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating contract failed, no ID obtained.");
                }
            }
            
            return contract;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving contract", e);
        }
    }

    @Override
    public Optional<Contract> findById(Long id) {
        String sql = "SELECT * FROM contracts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToContract(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contract by ID", e);
        }
    }

    @Override
    public List<Contract> findAll() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
            return contracts;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all contracts", e);
        }
    }

    @Override
    public List<Contract> findByReservationId(Long reservationId) {
        String sql = "SELECT * FROM contracts WHERE reservation_id = ?";
        List<Contract> contracts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
            return contracts;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contracts by reservation ID", e);
        }
    }

    @Override
    public List<Contract> findByContractNumber(String contractNumber) {
        String sql = "SELECT * FROM contracts WHERE contract_number = ?";
        List<Contract> contracts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, contractNumber);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
            return contracts;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contracts by contract number", e);
        }
    }

    @Override
    public List<Contract> findByStatus(String status) {
        String sql = "SELECT * FROM contracts WHERE status = ?";
        List<Contract> contracts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
            return contracts;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contracts by status", e);
        }
    }

    @Override
    public Contract update(Contract contract) {
        if (contract.getId() == null) {
            throw new IllegalArgumentException("Contract ID cannot be null for update operation");
        }
        
        String sql = "UPDATE contracts SET reservation_id = ?, contract_number = ?, " +
                    "status = ?, start_date = ?, end_date = ?, total_amount = ?, terms = ?, notes = ? " +
                    "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setContractParameters(stmt, contract);
            stmt.setLong(9, contract.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating contract failed, no rows affected.");
            }
            
            return contract;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contract: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM contracts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contract", e);
        }
    }

    @Override
    public List<Contract> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM contracts WHERE start_date >= ? AND end_date <= ?";
        List<Contract> contracts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
            return contracts;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contracts by date range", e);
        }
    }

    private void setContractParameters(PreparedStatement stmt, Contract contract) throws SQLException {
        stmt.setLong(1, contract.getReservationId());
        stmt.setString(2, contract.getContractNumber());
        stmt.setString(3, contract.getContractType());
        stmt.setString(4, contract.getStatus());
        stmt.setDate(5, Date.valueOf(contract.getStartDate()));
        stmt.setDate(6, Date.valueOf(contract.getEndDate()));
        stmt.setDouble(7, contract.getTotalAmount());
        stmt.setString(8, contract.getTerms() != null ? contract.getTerms() : "");
        stmt.setString(9, contract.getNotes() != null ? contract.getNotes() : "");
    }

    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setId(rs.getLong("id"));
        contract.setReservationId(rs.getLong("reservation_id"));
        contract.setContractNumber(rs.getString("contract_number"));
        contract.setContractType(rs.getString("contract_type"));
        contract.setStatus(rs.getString("status"));
        contract.setStartDate(rs.getDate("start_date").toLocalDate());
        contract.setEndDate(rs.getDate("end_date").toLocalDate());
        contract.setTotalAmount(rs.getDouble("total_amount"));
        contract.setTerms(rs.getString("terms"));
        contract.setNotes(rs.getString("notes"));
        return contract;
    }
} 