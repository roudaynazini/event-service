package org.example.entities;

import java.util.Date;

public class avis {
    private int idAvis;
    private int idUtilisateur;
    private int idEvenement;
    private String commentaire;
    private int note;
    private Date dateAvis;



    public avis(int idUtilisateur, int idEvenement, String commentaire,int note, java.sql.Date dateAvis) {
        this.idAvis = idAvis;
        this.idUtilisateur = idUtilisateur;
        this.idEvenement = idEvenement;
        this.note = note;
        this.commentaire = commentaire;
        this.dateAvis = dateAvis;
    }

    public int getIdAvis() {
        return idAvis;
    }

    public void setIdAvis(int idAvis) {
        this.idAvis = idAvis;
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

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Date getDateAvis() {
        return dateAvis;
    }

    public void setDateAvis(Date dateAvis) {
        this.dateAvis = dateAvis;
    }

    @Override
    public String toString() {
        return "avis{" +
                "idAvis=" + idAvis +
                ", idUtilisateur=" + idUtilisateur +
                ", idEvenement=" + idEvenement +
                ", commentaire='" + commentaire + '\'' +
                ", note=" + note +
                ", dateAvis=" + dateAvis +
                '}';
    }
}

