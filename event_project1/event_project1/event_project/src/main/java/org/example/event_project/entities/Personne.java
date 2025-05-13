package org.example.event_project.entities;

import javafx.beans.property.*;

public class Personne {

    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty email;
    private final StringProperty numTelephone;
    private final IntegerProperty age;
    private final StringProperty motDePasse;

    public Personne(int id, String nom, String prenom, String email, String numTelephone, int age, String motDePasse) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.email = new SimpleStringProperty(email);
        this.numTelephone = new SimpleStringProperty(numTelephone);
        this.age = new SimpleIntegerProperty(age);
        this.motDePasse = new SimpleStringProperty(motDePasse);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty emailProperty() { return email; }
    public StringProperty numTelephoneProperty() { return numTelephone; }
    public IntegerProperty ageProperty() { return age; }
    public StringProperty motDePasseProperty() { return motDePasse; }

    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getEmail() { return email.get(); }
    public String getNumTelephone() { return numTelephone.get(); }
    public int getAge() { return age.get(); }
    public String getMotDePasse() { return motDePasse.get(); }
}
