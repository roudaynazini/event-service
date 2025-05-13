package org.example.event_project.entities;

public class Invitee {
    private int idInvitee;
    private String nomInvitee;
    private String emailInvitee;
    private String tlf;
    private String adresse;

    public Invitee(int idInvitee, String nomInvitee, String emailInvitee, String tlf, String adresse) {
        this.idInvitee = idInvitee;
        this.nomInvitee = nomInvitee;
        this.emailInvitee = emailInvitee;
        this.tlf = tlf;
        this.adresse = adresse;
    }

    public Invitee(String nomInvitee, String emailInvitee, String tlf, String adresse) {
        this.nomInvitee = nomInvitee;
        this.emailInvitee = emailInvitee;
        this.tlf = tlf;
        this.adresse = adresse;
    }


    // Getters et Setters
    public int getIdInvitee() { return idInvitee; }
    public void setIdInvitee(int idInvitee) { this.idInvitee = idInvitee; }

    public String getNomInvitee() { return nomInvitee; }
    public void setNomInvitee(String nomInvitee) { this.nomInvitee = nomInvitee; }

    public String getEmail() { return emailInvitee; }
    public void setEmail(String emailInvitee) { this.emailInvitee = emailInvitee; }

    public String getTlf() { return tlf; }
    public void setTlf(String tlf) { this.tlf = tlf; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public String toString() {
        return "Invitee{" +
                "idInvitee=" + idInvitee +
                ", nomInvitee='" + nomInvitee + '\'' +
                ", emailInvitee='" + emailInvitee + '\'' +
                ", tel='" + tlf + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
