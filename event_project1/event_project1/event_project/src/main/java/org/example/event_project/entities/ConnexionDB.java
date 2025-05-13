package org.example.event_project.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionDB {

    private static Connection con;
    private static Statement st;

    // Établit la connexion à la base de données
    public static void connect() {
        try {
            if (con == null || con.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/project_event";
                String user = "root";
                String pass = "";

                con = DriverManager.getConnection(url, user, pass);
                st = con.createStatement();
                System.out.println("Connexion réussie !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }

    // Retourne une connexion valide, même si elle a été fermée
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                connect();  // ⚠️ reconnecte si la connexion est fermée
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
        return con;
    }

    // Retourne le statement
    public static Statement getStatement() {
        try {
            if (st == null || st.isClosed()) {
                st = getConnection().createStatement();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du statement : " + e.getMessage());
        }
        return st;
    }

    // Ferme la connexion et le statement
    public static void close() {
        try {
            if (st != null && !st.isClosed()) {
                st.close();
                System.out.println("Statement fermé !");
            }
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Connexion fermée !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture : " + e.getMessage());
        }
    }
}
