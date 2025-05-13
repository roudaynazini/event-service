package org.example.event_project.service;



import org.example.event_project.Utils.DataBaseConnection;
import org.example.event_project.entities.Evenement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {
    
    // Méthode pour ajouter un événement
    public boolean add(Evenement evenement) {
        String query = "INSERT INTO evenement (titre, date_creation, categorie, date_debut, date_fin, description, status, lieu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, evenement.getTitre());
            pstmt.setDate(2, Date.valueOf(evenement.getDateCreation()));
            pstmt.setInt(3, evenement.getCategorie());
            pstmt.setDate(4, Date.valueOf(evenement.getDateDebut()));
            pstmt.setDate(5, Date.valueOf(evenement.getDateFin()));
            pstmt.setString(6, evenement.getDescription());
            pstmt.setString(7, evenement.getStatus());
            pstmt.setString(8, evenement.getLieu());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Méthode pour mettre à jour un événement
    public boolean update(Evenement evenement) {
        String query = "UPDATE evenement SET titre = ?, date_creation = ?, categorie = ?, date_debut = ?, date_fin = ?, description = ?, status = ?, lieu = ? WHERE id_even = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, evenement.getTitre());
            pstmt.setDate(2, Date.valueOf(evenement.getDateCreation()));
            pstmt.setInt(3, evenement.getCategorie());
            pstmt.setDate(4, Date.valueOf(evenement.getDateDebut()));
            pstmt.setDate(5, Date.valueOf(evenement.getDateFin()));
            pstmt.setString(6, evenement.getDescription());
            pstmt.setString(7, evenement.getStatus());
            pstmt.setString(8, evenement.getLieu());
            pstmt.setInt(9, evenement.getIdEven());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Méthode pour supprimer un événement
    public boolean delete(int id) {
        String query = "DELETE FROM evenement WHERE id_even = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Méthode pour obtenir tous les événements
    public List<Evenement> getAll() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Evenement evenement = new Evenement(
                    rs.getInt("id_even"),
                    rs.getString("titre"),
                    rs.getDate("date_creation").toLocalDate(),
                    rs.getInt("categorie"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("lieu")
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return evenements;
    }
    
    // Méthode pour obtenir un événement par ID
    public Evenement getById(int id) {
        String query = "SELECT * FROM evenement WHERE id_even = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Evenement(
                        rs.getInt("id_even"),
                        rs.getString("titre"),
                        rs.getDate("date_creation").toLocalDate(),
                        rs.getInt("categorie"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("lieu")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}

