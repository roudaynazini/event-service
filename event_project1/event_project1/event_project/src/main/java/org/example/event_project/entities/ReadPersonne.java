package org.example.event_project.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReadPersonne {
    public static void read() {
        try {
            Statement st = ConnexionDB.getStatement();
            String req = "SELECT * FROM per";
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nom: " + rs.getString("nom") +
                        ", Prénom: " + rs.getString("prenom") +
                        ", Numéro de téléphone: " + rs.getString("numtelephone") +
                        ", Email: " + rs.getString("email") +
                        ", Âge: " + rs.getInt("age"));
                // Le mot de passe n'est pas affiché volontairement
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
