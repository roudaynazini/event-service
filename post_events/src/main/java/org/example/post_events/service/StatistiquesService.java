package org.example.post_events.service;

import org.example.post_events.entities.Post;
import org.example.post_events.entities.Statistiques;  // Assure-toi que c'est bien Statistiques et non Staistiques

import java.util.List;

public class StatistiquesService {

    // Méthode pour calculer les statistiques à partir de la liste des posts
    public Statistiques calculerStatistiques(List<Post> posts) {
        if (posts == null) {
            throw new IllegalArgumentException("La liste des posts ne peut pas être null");
        }

        int totalPosts = posts.size(); // Nombre total de posts
        int totalCommentaires = 0;

        // Parcourir chaque post pour compter les commentaires
        for (Post post : posts) {
            if (post.getCommentaires() != null) {
                totalCommentaires += post.getCommentaires().size(); // Ajouter le nombre de commentaires du post
            } else {
                // Si la liste des commentaires est null, on la considère comme vide
                totalCommentaires += 0;
            }
        }

        // Retourner les statistiques calculées
        return new Statistiques(totalPosts, totalCommentaires);  // Utilisation correcte de la classe Statistiques
    }

    // Méthode pour ajouter des posts et commentaires aux statistiques
    public Statistiques ajouterStatistiques(Statistiques statistiques, List<Post> posts) {
        if (posts == null) {
            throw new IllegalArgumentException("La liste des posts ne peut pas être null");
        }

        int totalPosts = posts.size(); // Nombre total de posts
        int totalCommentaires = 0;

        // Parcourir chaque post pour compter les commentaires
        for (Post post : posts) {
            if (post.getCommentaires() != null) {
                totalCommentaires += post.getCommentaires().size(); // Ajouter le nombre de commentaires du post
            } else {
                // Si la liste des commentaires est null, on la considère comme vide
                totalCommentaires += 0;
            }
        }

        // Ajouter les valeurs aux statistiques existantes
        statistiques.ajouterPosts(totalPosts);
        statistiques.ajouterCommentaires(totalCommentaires);

        return statistiques;
    }
}
