package org.example.event_project.entities;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ObservableValue;

import java.util.List;

public class Post {
    private int id;
    private int userId;
    private String description;
    private String dateCreation;
    private int nbReactions;
    private String imagePath;

    // Liste des commentaires associée au post
    private List<Commentaire> commentaires;

    // Constructor with all fields
    public Post(int id, int userId, String description, String dateCreation, int nbReactions, String imagePath) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.dateCreation = dateCreation;
        this.nbReactions = nbReactions;
        this.imagePath = imagePath;
    }

    // Constructor without id (for new posts)
    public Post(int userId, String description, String dateCreation, int nbReactions, String imagePath) {
        this.userId = userId;
        this.description = description;
        this.dateCreation = dateCreation;
        this.nbReactions = nbReactions;
        this.imagePath = imagePath;
    }

    // Constructor without imagePath (for default value of imagePath)
    public Post(int userId, String description, String dateCreation) {
        this.userId = userId;
        this.description = description;
        this.dateCreation = dateCreation;
        this.nbReactions = 0;
        this.imagePath = imagePath; // Default to null if no image is provided
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getNbReactions() {
        return nbReactions;
    }

    public void setNbReactions(int nbReactions) {
        this.nbReactions = nbReactions;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getter and Setter for commentaires
    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", dateCreation='" + dateCreation + '\'' +
                ", nbReactions=" + nbReactions +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }


}
