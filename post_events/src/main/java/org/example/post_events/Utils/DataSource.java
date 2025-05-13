package org.example.post_events.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

        private Connection con;
        private String url = "jdbc:mysql://localhost:3306/communitydb";  // URL de la base de données 'communitydb'
        private String user = "root";  // Nom d'utilisateur
        private String pass = "";  // Mot de passe de la base de données
        private static DataSource data;  // Instance unique du DataSource

        // Constructeur privé pour empêcher l'instanciation directe
        private DataSource() {
            try {
                // Tentative de connexion à la base de données
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("Connexion à la base de données 'communitydb' réussie");
            } catch (SQLException e) {
                // Gestion de l'exception et affichage de l'erreur
                System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            }
        }

        // Méthode pour obtenir l'instance unique de DataSource
        public static DataSource getInstance() {
            if (data == null) {
                synchronized (DataSource.class) {
                    if (data == null) {
                        data = new DataSource();  // Création de l'instance si elle n'existe pas
                    }
                }
            }
            return data;  // Retour de l'instance unique
        }

        // Méthode pour obtenir la connexion
        public Connection getCon() {
            return con;
        }

        // Méthode pour fermer la connexion à la base de données proprement
        public void closeConnection() {
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Connexion fermée avec succès");
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
                }
            }
        }
    }
