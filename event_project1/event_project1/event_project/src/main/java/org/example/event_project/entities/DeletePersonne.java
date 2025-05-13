package org.example.event_project.entities;

import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

public class DeletePersonne {
    public static void delete() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("ID de la personne à supprimer : ");
            int id = sc.nextInt();

            Statement st = ConnexionDB.getStatement();
            String req = "DELETE FROM per WHERE id = " + id;
            int result = st.executeUpdate(req);

            if (result > 0) {
                System.out.println("✅ Personne supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune personne trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}


