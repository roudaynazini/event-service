package org.example.post_events.service;

import org.example.post_events.Utils.DataSource;
import org.example.post_events.entities.Post;
import org.example.post_events.entities.Commentaire;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicePost {

    private Connection cnx;
    private Statement stmt;

    public ServicePost() {
        cnx = DataSource.getInstance().getCon();
        try {
            stmt = cnx.createStatement();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du statement : " + e.getMessage());
        }
    }

    // Ajouter un Post
    public void ajouter(Post p) {
        String req = "INSERT INTO post (user_id, description, date_creation, nb_reactions, imagePath) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getDateCreation());
            ps.setInt(4, p.getNbReactions());
            ps.setString(5, p.getImagePath());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du post : " + e.getMessage());
        }
    }

    // Mettre à jour la description
    public void updateDescription(Post p) {
        String req = "UPDATE post SET description=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, p.getDescription());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la description : " + e.getMessage());
        }
    }

    // Supprimer un post
    public void delete(int id) {
        String req = "DELETE FROM post WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du post : " + e.getMessage());
        }
    }

    // Afficher tous les posts
    public List<Post> afficherAll() {
        List<Post> list = new ArrayList<>();
        String req = "SELECT * FROM post";
        try (ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                Post p = new Post(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("description"),
                        rs.getString("date_creation"),
                        rs.getInt("nb_reactions"),
                        rs.getString("imagePath")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des posts : " + e.getMessage());
        }
        return list;
    }


    // Récupérer les commentaires d'un post par ID
    public List<Commentaire> getCommentairesByPostId(int postId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire WHERE post_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Commentaire commentaire = new Commentaire(
                            rs.getInt("id"),
                            rs.getInt("post_id"),
                            rs.getInt("user_id"),
                            rs.getString("contenu"),
                            rs.getString("date_commentaire")
                    );
                    commentaires.add(commentaire);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires : " + e.getMessage());
        }
        return commentaires;
    }



    // Mettre à jour le nombre de réactions
    public void updateNbReactions(Post post) throws SQLException {
        String sql = "UPDATE post SET nb_reactions = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, post.getNbReactions());
            ps.setInt(2, post.getId());
            ps.executeUpdate();
        }
    }

    // Recherche de posts par utilisateur
    public List<Post> getPostsByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String req = "SELECT * FROM post WHERE user_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("description"),
                            rs.getString("date_creation"),
                            rs.getInt("nb_reactions"),
                            rs.getString("imagePath")
                    );
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des posts par utilisateur : " + e.getMessage());
        }
        return posts;
    }
    public List<Post> getAllPostsWithCommentaires() {
        List<Post> posts = new ArrayList<>();

        String req = "SELECT p.id AS post_id, p.user_id, p.description, p.date_creation, p.nb_reactions, p.imagePath, " +
                "c.id AS comment_id, c.user_id AS comment_user_id, c.contenu, c.date_commentaire " +
                "FROM post p LEFT JOIN commentaire c ON p.id = c.post_id " +
                "ORDER BY p.id";

        try (PreparedStatement ps = cnx.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            int currentPostId = -1;
            Post currentPost = null;

            while (rs.next()) {
                int postId = rs.getInt("post_id");

                if (postId != currentPostId) {
                    currentPostId = postId;
                    currentPost = new Post(
                            postId,
                            rs.getInt("user_id"),
                            rs.getString("description"),
                            rs.getString("date_creation"),
                            rs.getInt("nb_reactions"),
                            rs.getString("imagePath")
                    );
                    currentPost.setCommentaires(new ArrayList<>());
                    posts.add(currentPost);
                }

                int commentId = rs.getInt("comment_id");
                if (commentId != 0) {
                    Commentaire commentaire = new Commentaire(
                            commentId,
                            postId,
                            rs.getInt("comment_user_id"),
                            rs.getString("contenu"),
                            rs.getString("date_commentaire")
                    );
                    currentPost.getCommentaires().add(commentaire);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }

        return posts;
    }
    public List<Post> getAllPosts() {
        // Remplace cette méthode par la logique réelle pour récupérer les posts
        // Exemple : Récupération depuis une base de données
        List<Post> posts = new ArrayList<>();



        return posts;
    }




        public Map<String, Integer> getNombrePostsParMois() {
            Map<String, Integer> postsParMois = new HashMap<>();

            // Requête SQL pour récupérer le nombre de posts par mois avec MySQL
            String req = "SELECT DATE_FORMAT(date_creation, '%Y-%m') AS mois, COUNT(*) AS nombre " +
                    "FROM post " +
                    "GROUP BY mois " +
                    "ORDER BY mois";

            try (PreparedStatement ps = cnx.prepareStatement(req);
                 ResultSet rs = ps.executeQuery()) {

                // Parcourir les résultats et les ajouter au Map
                while (rs.next()) {
                    String mois = rs.getString("mois"); // Format: "2025-05"
                    int nombre = rs.getInt("nombre");
                    postsParMois.put(mois, nombre);
                }

            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération du nombre de posts par mois : " + e.getMessage());
                // Optionnel : vous pouvez loguer l'erreur dans un fichier pour un meilleur débogage
            }

            return postsParMois;
        }
    }




