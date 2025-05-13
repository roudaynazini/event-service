package org.example.event_project.entities;

import java.time.LocalDate;

public class Evenement {
    private int idEven;
    private String titre;
    private LocalDate dateCreation;
    private int categorie; // FK vers Categorie
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private String status;
    private String lieu;
    
    // Constructeurs
    public Evenement() {}
    
    public Evenement(int idEven, String titre, LocalDate dateCreation, int categorie, 
                    LocalDate dateDebut, LocalDate dateFin, String description, 
                    String status, String lieu) {
        this.idEven = idEven;
        this.titre = titre;
        this.dateCreation = dateCreation;
        this.categorie = categorie;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.status = status;
        this.lieu = lieu;
    }
    
    // Constructeur sans ID pour les insertions
    public Evenement(String titre, LocalDate dateCreation, int categorie, 
                    LocalDate dateDebut, LocalDate dateFin, String description, 
                    String status, String lieu) {
        this.titre = titre;
        this.dateCreation = dateCreation;
        this.categorie = categorie;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.status = status;
        this.lieu = lieu;
    }
    
    // Getters/Setters
    public int getIdEven() {
        return idEven;
    }
    
    public void setIdEven(int idEven) {
        this.idEven = idEven;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public int getCategorie() {
        return categorie;
    }
    
    public void setCategorie(int categorie) {
        this.categorie = categorie;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
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
    
    public String getLieu() {
        return lieu;
    }
    
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
    
    // toString method
    @Override
    public String toString() {
        return "Evenement{" +
                "idEven=" + idEven +
                ", titre='" + titre + '\'' +
                ", dateCreation=" + dateCreation +
                ", categorie=" + categorie +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
