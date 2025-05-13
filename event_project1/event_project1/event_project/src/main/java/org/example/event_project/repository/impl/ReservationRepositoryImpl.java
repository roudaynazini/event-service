package org.example.event_project.repository.impl;

import org.example.event_project.config.DatabaseConfig;
import org.example.event_project.entities.Reservation;
import org.example.event_project.repository.ReservationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ReservationRepositoryImpl implements ReservationRepository {
    private final Connection connection;

    public ReservationRepositoryImpl() {
        try {
            this.connection = DatabaseConfig.getConnection();
            System.out.println("Connected to database successfully");
            createTableIfNotExists();
            System.out.println("Table created/verified successfully");
        } catch (SQLException e) {
            System.err.println("Database initialization error:");
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw new RuntimeException("Failed to initialize ReservationRepository", e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS reservations (
                id bigint(20) NOT NULL AUTO_INCREMENT,
                client_name varchar(100) NOT NULL,
                event_date date NOT NULL,
                status varchar(20) NOT NULL,
                notes text DEFAULT NULL,
                created_at timestamp NOT NULL DEFAULT current_timestamp(),
                updated_at timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                PRIMARY KEY (id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
        """;
        
        try (Statement stmt = connection.createStatement()) {
            // First check if the table exists
            ResultSet tables = connection.getMetaData().getTables(null, null, "reservations", null);
            boolean tableExists = tables.next();
            
            if (!tableExists) {
                // If table doesn't exist, create it
                stmt.execute(createTableSQL);
                System.out.println("Created new reservations table");
            }
        } catch (SQLException e) {
            System.err.println("Error managing table structure: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservations (client_name, event_date, status, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reservation.getClientName());
            pstmt.setDate(2, Date.valueOf(reservation.getEventDate()));
            pstmt.setString(3, reservation.getStatus());
            pstmt.setString(4, reservation.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed, no rows affected.");
            }
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Long generatedId = rs.getLong(1);
                    reservation.setId(generatedId);
                    
                    // Set the timestamps directly without making another database call
                    reservation.setCreatedAt(LocalDate.now());
                    reservation.setUpdatedAt(LocalDate.now());
                    
                    return reservation;
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            String errorMessage = String.format(
                "Failed to save reservation. SQL State: %s, Error Code: %d, Message: %s",
                e.getSQLState(),
                e.getErrorCode(),
                e.getMessage()
            );
            throw new RuntimeException(errorMessage, e);
        }
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reservation by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all reservations", e);
        }
        return reservations;
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
    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET client_name = ?, event_date = ?, status = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getClientName());
            pstmt.setDate(2, Date.valueOf(reservation.getEventDate()));
            pstmt.setString(3, reservation.getStatus());
            pstmt.setString(4, reservation.getNotes());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            pstmt.setLong(6, reservation.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update reservation", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reservation", e);
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
        reservation.setId(rs.getLong("id"));
        reservation.setClientName(rs.getString("client_name"));
        reservation.setEventDate(rs.getDate("event_date").toLocalDate());
        reservation.setStatus(rs.getString("status"));
        reservation.setNotes(rs.getString("notes"));
        
        // Handle created_at field if it exists
        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                reservation.setCreatedAt(createdAt.toLocalDateTime().toLocalDate());
            }
        } catch (SQLException e) {
            // Field doesn't exist, ignore
        }
        
        // Handle updated_at field if it exists
        try {
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                reservation.setUpdatedAt(updatedAt.toLocalDateTime().toLocalDate());
            }
        } catch (SQLException e) {
            // Field doesn't exist, ignore
        }
        
        return reservation;
    }
} 