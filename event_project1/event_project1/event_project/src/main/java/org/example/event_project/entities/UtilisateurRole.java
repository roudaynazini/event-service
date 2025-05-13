package org.example.event_project.entities;


public class UtilisateurRole {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;

    public UtilisateurRole(int id, String nom, String prenom, String email, String motDePasse, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }

    // Setters
    public void setRole(String role) {
        this.role = role;
    }
}

