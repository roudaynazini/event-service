package com.roudaynazini.repository.impl;

import com.roudaynazini.model.Reservation;
import com.roudaynazini.config.DatabaseConfig;
import com.roudaynazini.repository.ReservationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepositoryImpl implements ReservationRepository {
    private final Connection connection;

    public ReservationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservations (id, client_name, event_date, status, " +
                    "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getId());
            stmt.setString(2, reservation.getClientName());
            stmt.setDate(3, Date.valueOf(reservation.getEventDate()));
            stmt.setString(4, reservation.getStatus());
            stmt.setDate(5, Date.valueOf(reservation.getCreatedAt()));
            stmt.setDate(6, Date.valueOf(reservation.getUpdatedAt()));
            
            stmt.executeUpdate();
            return reservation;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving reservation", e);
        }
    }

    @Override
    public Optional<Reservation> findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToReservation(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservation by id", e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reservations", e);
        }
    }

    @Override
    public List<Reservation> findByClientName(String clientName) {
        String sql = "SELECT * FROM reservations WHERE client_name LIKE ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + clientName + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by client name", e);
        }
    }

    @Override
    public List<Reservation> findByEventDate(LocalDate eventDate) {
        String sql = "SELECT * FROM reservations WHERE event_date = ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(eventDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by event date", e);
        }
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        String sql = "SELECT * FROM reservations WHERE status = ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by status", e);
        }
    }

    @Override
    public Reservation update(Reservation reservation) {
        String sql = "UPDATE reservations SET client_name = ?, event_date = ?, " +
                    "status = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reservation.getClientName());
            stmt.setDate(2, Date.valueOf(reservation.getEventDate()));
            stmt.setString(3, reservation.getStatus());
            stmt.setDate(4, Date.valueOf(reservation.getUpdatedAt()));
            stmt.setInt(5, reservation.getId());
            
            stmt.executeUpdate();
            return reservation;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation", e);
        }
    }

    @Override
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM reservations WHERE event_date BETWEEN ? AND ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by date range", e);
        }
    }

    @Override
    public List<Reservation> findByPaymentStatus(String paymentStatus) {
        // Since the model doesn't have payment status, return an empty list
        return new ArrayList<>();
    }

    @Override
    public List<Reservation> findByVenue(String venue) {
        // Since the model doesn't have venue, return an empty list
        return new ArrayList<>();
    }

    @Override
    public List<Reservation> search(String query) {
        String sql = "SELECT * FROM reservations WHERE client_name LIKE ? OR status LIKE ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error searching reservations", e);
        }
    }

    @Override
    public List<Reservation> filter(String clientName, String eventType, LocalDate startDate, 
                                   LocalDate endDate, String status, String paymentStatus, 
                                   String venue, Double minAmount, Double maxAmount) {
        StringBuilder sql = new StringBuilder("SELECT * FROM reservations WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (clientName != null && !clientName.isEmpty()) {
            sql.append(" AND client_name LIKE ?");
            params.add("%" + clientName + "%");
        }
        
        if (startDate != null) {
            sql.append(" AND event_date >= ?");
            params.add(Date.valueOf(startDate));
        }
        
        if (endDate != null) {
            sql.append(" AND event_date <= ?");
            params.add(Date.valueOf(endDate));
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        List<Reservation> reservations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering reservations", e);
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setClientName(rs.getString("client_name"));
        reservation.setEventDate(rs.getDate("event_date").toLocalDate());
        reservation.setStatus(rs.getString("status"));
        reservation.setCreatedAt(rs.getDate("created_at").toLocalDate());
        reservation.setUpdatedAt(rs.getDate("updated_at").toLocalDate());
        return reservation;
    }
} 