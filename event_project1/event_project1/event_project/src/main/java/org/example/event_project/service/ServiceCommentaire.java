package org.example.event_project.service;



import org.example.event_project.Utils.DataSource;
import org.example.event_project.entities.Commentaire;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ServiceCommentaire {

    private Connection cnx;
    private Statement stmt;

    public ServiceCommentaire() {
        cnx = DataSource.getInstance().getCon();
        try {
            stmt = cnx.createStatement();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du statement : " + e.getMessage());
        }
    }

    // Ajouter un commentaire
    public void ajouter(Commentaire c) {
        String req = "INSERT INTO commentaire (user_id, post_id, contenu, date_commentaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, c.getUserId());
            ps.setInt(2, c.getPostId());
            ps.setString(3, c.getContenu());
            ps.setString(4, c.getDateCommentaire());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
        }
    }

    // Mettre à jour le contenu
    public void updateContenu(Commentaire c) {
        String req = "UPDATE commentaire SET contenu=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, c.getContenu());
            ps.setInt(2, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du commentaire : " + e.getMessage());
        }
    }

    // Supprimer un commentaire
    public void delete(int id) {
        String req = "DELETE FROM commentaire WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du commentaire : " + e.getMessage());
        }
    }

    // Afficher tous les commentaires
    public List<Commentaire> afficherAll() {
        List<Commentaire> list = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        try (ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                Commentaire c = new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("post_id"),
                        rs.getInt("user_id"),
                        rs.getString("contenu"),
                        rs.getString("date_commentaire")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des commentaires : " + e.getMessage());
        }
        return list;
    }

    // Afficher les commentaires d'un post spécifique
    public List<Commentaire> afficherParPost(int postId) {
        List<Commentaire> list = new ArrayList<>();
        String req = "SELECT * FROM commentaire WHERE post_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Commentaire c = new Commentaire(
                            rs.getInt("id"),
                            rs.getInt("post_id"),
                            rs.getInt("user_id"),
                            rs.getString("contenu"),
                            rs.getString("date_commentaire")
                    );
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des commentaires par post : " + e.getMessage());
        }
        return list;
    }

    public List<Commentaire> getCommentairesByPostId(int postId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire WHERE post_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Commentaire c = new Commentaire(
                            rs.getInt("id"),
                            rs.getInt("post_id"),
                            rs.getInt("user_id"),
                            rs.getString("contenu"),
                            rs.getString("date_commentaire")
                    );
                    commentaires.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires par post ID : " + e.getMessage());
        }
        return commentaires;
    }

    public List<Commentaire> getCommentairesAvecPost(int postId) {
        List<Commentaire> commentaires = new ArrayList<>();
        try {
            String query = "SELECT * FROM commentaire WHERE post_id = ?";
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setContenu(rs.getString("contenu"));
                c.setDateCommentaire(String.valueOf(rs.getTimestamp("date_commentaire").toLocalDateTime()));
                commentaires.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaires;
    }



        public Map<String, Integer> getNombreCommentairesParMois() {
            Map<String, Integer> result = new TreeMap<>(); // TreeMap pour trier par ordre chronologique

            // Requête SQL pour récupérer le nombre de commentaires par mois avec MySQL
            String query = "SELECT DATE_FORMAT(date_commentaire, '%Y-%m') AS mois, COUNT(*) AS total " +
                    "FROM commentaire " +
                    "GROUP BY mois " +
                    "ORDER BY mois";

            try (PreparedStatement statement = cnx.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                // Parcourir les résultats et les ajouter à la Map
                while (resultSet.next()) {
                    String mois = resultSet.getString("mois"); // Format: "2025-05"
                    int total = resultSet.getInt("total");
                    result.put(mois, total);
                }

            } catch (SQLException e) {
                // Vous pouvez utiliser un logger ici pour enregistrer l'erreur dans un fichier
                System.err.println("Erreur lors de la récupération du nombre de commentaires par mois : " + e.getMessage());
                // Optionnel : vous pouvez loguer l'erreur dans un fichier pour un meilleur débogage
            }

            return result;
        }
    }









