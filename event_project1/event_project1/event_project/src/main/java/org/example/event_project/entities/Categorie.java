package org.example.event_project.entities;

public class Categorie {
    private int idCate;
    private String type;
    private String description;
    private String status;
    
    // Constructeurs
    public Categorie() {}
    
    public Categorie(int idCate, String type, String description, String status) {
        this.idCate = idCate;
        this.type = type;
        this.description = description;
        this.status = status;
    }
    
    // Getters/Setters
    public int getIdCate() {
        return idCate;
    }
    
    public void setIdCate(int idCate) {
        this.idCate = idCate;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // toString method
    @Override
    public String toString() {
        return type; // Returning type for ComboBox display
    }
}
