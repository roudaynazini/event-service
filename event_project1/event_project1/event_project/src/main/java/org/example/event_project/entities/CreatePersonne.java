package org.example.event_project.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CreatePersonne {
    public static void create() {
        Scanner sc = new Scanner(System.in);
        try {
            // Demande des informations à l'utilisateur
            System.out.print("Nom: ");
            String nom = sc.nextLine();

            System.out.print("Prénom: ");
            String prenom = sc.nextLine();

            System.out.print("Numéro de téléphone: ");
            String numtelephone = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Mot de passe: ");
            String motdepasse = sc.nextLine();

            System.out.print("Âge: ");
            int age = sc.nextInt();

            // Créer la connexion et la requête préparée
            Connection con = ConnexionDB.getConnection();
            String req = "INSERT INTO per (nom, prenom, numtelephone, email, motdepasse, age) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(req);
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, numtelephone);
            pstmt.setString(4, email);
            pstmt.setString(5, motdepasse);
            pstmt.setInt(6, age);

            // Exécuter la requête
            int a = pstmt.executeUpdate();
            if (a > 0) {
                System.out.println("Personne ajoutée !");
            }

        } catch (SQLException e) {
            // Afficher l'erreur SQL
            System.out.println("Erreur SQL : " + e.getMessage());
        } finally {
            // Fermer la connexion à la base de données
            ConnexionDB.close();
        }
    }
}
