package org.example.event_project.entities;

import java.sql.Date;
import java.time.LocalDate;

public class Invitation {
    private int idInvitation;
    private String nomEvenement;
    private Date dateEvenement;
    private String emailInvitee;
    private String message;

    public Invitation(int idInvitation, String nomEvenement, Date dateEvenement, String emailInvitee, String message) {
        this.idInvitation = idInvitation;
        this.nomEvenement = nomEvenement;
        this.dateEvenement = dateEvenement;
        this.emailInvitee = emailInvitee;
        this.message = message;
    }

    public Invitation(String nomEvenement, Date dateEvenement, String emailInvitee, String message) {
        this(0, nomEvenement, dateEvenement, emailInvitee, message);
    }

    // Getters et Setters
    public int getIdInvitation() {
        return idInvitation;
    }

    public void setIdInvitation(int idInvitation) {
        this.idInvitation = idInvitation;
    }

    public String getNomEvenement() {
        return nomEvenement;
    }

    public void setNomEvenement(String nomEvenement) {
        this.nomEvenement = nomEvenement;
    }

    public Date getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(Date dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public String getEmailInvitee() {
        return emailInvitee;
    }

    public void setEmailInvitee(String emailInvitee) {
        this.emailInvitee = emailInvitee;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public LocalDate getDateEvenementLocalDate() {
        return dateEvenement.toLocalDate();  // Conversion de java.sql.Date en LocalDate
    }
}