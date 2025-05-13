package org.example.entities;

import java.util.Date;

public class reclamation {
    private int idReclamation;
    private int idUtilisateur;
    private int idEvenement;
    private String description;
    private String etat;
    private Date dateReclamation;

    // Constructeur pour lecture depuis la base (inclut idReclamation)
    public reclamation(int idUtilisateur, int idReclamation, int idEvenement, String description, Date dateReclamation, String etat) {
        this.idUtilisateur = idUtilisateur;
        this.idReclamation = idReclamation;
        this.idEvenement = idEvenement;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.etat = etat;
    }

    // Constructeur pour insertion (sans idReclamation)
    public reclamation(int idUtilisateur, int idEvenement, String description, Date dateReclamation, String etat) {
        this.idUtilisateur = idUtilisateur;
        this.idEvenement = idEvenement;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.etat = etat;
    }

    public int getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Date getDateReclamation() {
        return dateReclamation;
    }

    public void setDateReclamation(Date dateReclamation) {
        this.dateReclamation = dateReclamation;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", idUtilisateur=" + idUtilisateur +
                ", idEvenement=" + idEvenement +
                ", description='" + description + '\'' +
                ", etat='" + etat + '\'' +
                ", dateReclamation=" + dateReclamation +
                '}';
    }
}
