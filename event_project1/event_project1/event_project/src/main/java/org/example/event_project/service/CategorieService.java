package org.example.event_project.service;



import org.example.event_project.Utils.DataBaseConnection;
import org.example.event_project.entities.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService {
    
    // Méthode pour ajouter une catégorie
    public boolean add(Categorie categorie) {
        String query = "INSERT INTO categorie (type, description, status) VALUES (?, ?, ?)";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, categorie.getType());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setString(3, categorie.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Méthode pour mettre à jour une catégorie
    public boolean update(Categorie categorie) {
        String query = "UPDATE categorie SET type = ?, description = ?, status = ? WHERE id_cate = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, categorie.getType());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setString(3, categorie.getStatus());
            pstmt.setInt(4, categorie.getIdCate());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Méthode pour supprimer une catégorie
    public boolean delete(int id) {
        String query = "DELETE FROM categorie WHERE id_cate = ?";
        
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
    
    // Méthode pour obtenir toutes les catégories
    public List<Categorie> getAll() {
        List<Categorie> categories = new ArrayList<>();
        String query = "SELECT * FROM categorie";
        
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Categorie categorie = new Categorie(
                    rs.getInt("id_cate"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }
    
    // Méthode pour obtenir une catégorie par ID
    public Categorie getById(int id) {
        String query = "SELECT * FROM categorie WHERE id_cate = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Categorie(
                        rs.getInt("id_cate"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
