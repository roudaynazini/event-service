package org.example.event_project.entities;

import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdatePersonne {
    public static void update() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("ID de la personne à modifier: ");
            int id = sc.nextInt();
            sc.nextLine(); // nettoyage du buffer

            System.out.print("Nouveau Nom: ");
            String nom = sc.nextLine();

            System.out.print("Nouveau Prénom: ");
            String prenom = sc.nextLine();

            System.out.print("Nouveau Numéro de téléphone: ");
            String numtelephone = sc.nextLine();

            System.out.print("Nouvel Email: ");
            String email = sc.nextLine();

            System.out.print("Nouveau Mot de passe: ");
            String motdepasse = sc.nextLine();

            System.out.print("Nouvel Âge: ");
            int age = sc.nextInt();

            Statement st = ConnexionDB.getStatement();
            String req = "UPDATE per SET nom='" + nom + "', prenom='" + prenom + "', numtelephone='" + numtelephone +
                    "', email='" + email + "', motdepasse='" + motdepasse + "', age=" + age + " WHERE id=" + id;

            int a = st.executeUpdate(req);
            if (a > 0) {
                System.out.println("Personne mise à jour !");
            } else {
                System.out.println("Personne non trouvée !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
