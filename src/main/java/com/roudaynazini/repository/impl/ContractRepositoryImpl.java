package com.roudaynazini.repository.impl;

import com.roudaynazini.model.Contract;
import com.roudaynazini.repository.ContractRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContractRepositoryImpl implements ContractRepository {
    private final Connection connection;

    public ContractRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Contract save(Contract contract) {
        String sql = "INSERT INTO contracts (id, reservation_id, contract_number, contract_type, " +
                    "status, start_date, end_date, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, contract.getId());
            stmt.setInt(2, contract.getReservationId());
            stmt.setString(3, contract.getContractNumber());
            stmt.setString(4, contract.getContractType());
            stmt.setString(5, contract.getStatus());
            stmt.setDate(6, Date.valueOf(contract.getStartDate()));
            stmt.setDate(7, Date.valueOf(contract.getEndDate()));
            stmt.setDate(8, Date.valueOf(contract.getCreatedAt()));
            stmt.setDate(9, Date.valueOf(contract.getUpdatedAt()));
            
            stmt.executeUpdate();
            return contract;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving contract", e);
        }
    }

    @Override
    public Optional<Contract> findById(int id) {
        String sql = "SELECT * FROM contracts WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToContract(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contract by id", e);
        }
    }

    @Override
    public List<Contract> findAll() {
        String sql = "SELECT * FROM contracts";
        List<Contract> contracts = new ArrayList<>();
        
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
    public Contract update(Contract contract) {
        String sql = "UPDATE contracts SET reservation_id = ?, contract_number = ?, " +
                    "contract_type = ?, status = ?, start_date = ?, end_date = ?, " +
                    "updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, contract.getReservationId());
            stmt.setString(2, contract.getContractNumber());
            stmt.setString(3, contract.getContractType());
            stmt.setString(4, contract.getStatus());
            stmt.setDate(5, Date.valueOf(contract.getStartDate()));
            stmt.setDate(6, Date.valueOf(contract.getEndDate()));
            stmt.setDate(7, Date.valueOf(contract.getUpdatedAt()));
            stmt.setInt(8, contract.getId());
            
            stmt.executeUpdate();
            return contract;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contract", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM contracts WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contract", e);
        }
    }

    @Override
    public List<Contract> findByReservationId(int reservationId) {
        String sql = "SELECT * FROM contracts WHERE reservation_id = ?";
        List<Contract> contracts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
            return contracts;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contracts by reservation id", e);
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

    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setId(rs.getInt("id"));
        contract.setReservationId(rs.getInt("reservation_id"));
        contract.setContractNumber(rs.getString("contract_number"));
        contract.setContractType(rs.getString("contract_type"));
        contract.setStatus(rs.getString("status"));
        contract.setStartDate(rs.getDate("start_date").toLocalDate());
        contract.setEndDate(rs.getDate("end_date").toLocalDate());
        contract.setCreatedAt(rs.getDate("created_at").toLocalDate());
        contract.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        return contract;
    }
} 