package org.example.event_project.entities;

import java.time.LocalDate;

public class Badge {
    private String nom;
    private String description;
    private String emoji;
    private LocalDate dateAttribution;
    private String critere;
    private int idUtilisateur;  // Attribut idUtilisateur

    // Constructeur
    public Badge(String nom, String description, String emoji, LocalDate dateAttribution, String critere, int idUtilisateur) {
        this.nom = nom;
        this.description = description;
        this.emoji = emoji;
        this.dateAttribution = dateAttribution;
        this.critere = critere;
        this.idUtilisateur = idUtilisateur;
    }


    // Getters et setters pour les attributs
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public LocalDate getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(LocalDate dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public String getCritere() {
        return critere;
    }

    public void setCritere(String critere) {
        this.critere = critere;
    }

    // Getter et setter pour idUtilisateur
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    // Méthode toString pour une représentation en texte
    @Override
    public String toString() {
        return "Badge{" +
                "nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", emoji='" + emoji + '\'' +
                ", dateAttribution=" + dateAttribution +
                ", critere='" + critere + '\'' +
                ", idUtilisateur=" + idUtilisateur +
                '}';
    }
}
