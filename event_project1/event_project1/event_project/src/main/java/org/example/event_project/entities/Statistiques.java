package org.example.event_project.entities;

import java.util.Objects;

public class Statistiques {
    private int totalPosts;        // Nombre total de posts
    private int totalCommentaires; // Nombre total de commentaires

    // Constructeur pour initialiser les valeurs
    public Statistiques(int totalPosts, int totalCommentaires) {
        if (totalPosts < 0 || totalCommentaires < 0) {
            throw new IllegalArgumentException("Les valeurs ne peuvent pas être négatives");
        }
        this.totalPosts = totalPosts;
        this.totalCommentaires = totalCommentaires;
    }

    // Getters et Setters pour accéder aux valeurs
    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        if (totalPosts < 0) {
            throw new IllegalArgumentException("Le nombre de posts ne peut pas être négatif");
        }
        this.totalPosts = totalPosts;
    }

    public int getTotalCommentaires() {
        return totalCommentaires;
    }

    public void setTotalCommentaires(int totalCommentaires) {
        if (totalCommentaires < 0) {
            throw new IllegalArgumentException("Le nombre de commentaires ne peut pas être négatif");
        }
        this.totalCommentaires = totalCommentaires;
    }

    // Méthodes pour ajouter des posts ou des commentaires
    public void ajouterPosts(int posts) {
        if (posts < 0) {
            throw new IllegalArgumentException("Le nombre de posts ne peut pas être négatif");
        }
        this.totalPosts += posts;
    }

    public void ajouterCommentaires(int commentaires) {
        if (commentaires < 0) {
            throw new IllegalArgumentException("Le nombre de commentaires ne peut pas être négatif");
        }
        this.totalCommentaires += commentaires;
    }

    // Méthode pour afficher les statistiques sous forme de chaîne
    @Override
    public String toString() {
        return "Statistiques{" +
                "totalPosts=" + totalPosts +
                ", totalCommentaires=" + totalCommentaires +
                '}';
    }

    // Méthodes equals et hashCode pour la comparaison d'objets
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Statistiques that = (Statistiques) obj;
        return totalPosts == that.totalPosts && totalCommentaires == that.totalCommentaires;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPosts, totalCommentaires);
    }
}
