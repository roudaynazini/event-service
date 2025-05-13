package org.example.event_project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.event_project.entities.Post;
import javafx.scene.input.MouseEvent;
import org.example.event_project.service.ServicePost;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PostController {

    @FXML
    private Button button_ajouter, button_modifier, button_supprimer, button_rechercher, button_gestionCommentaires;

    @FXML
    private TableView<Post> tableview_post;

    @FXML
    private TableColumn<Post, Integer> column_id, column_userId, column_nbReactions;

    @FXML
    private TableColumn<Post, String> column_description, column_dateCreation;
    @FXML
    private TableColumn<Post, String> column_imagePath;


    @FXML
    private TextField textfield_userId, textfield_description, textfield_dateCreation,
            textfield_nbReactions, textfield_imagePath, textfield_recherche;

    private ServicePost servicePost = new ServicePost();

    @FXML
    void initialize() {
        afficherPosts();

        // Logic for double-click to show comments
        tableview_post.setRowFactory(tv -> {
            TableRow<Post> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Post selectedPost = row.getItem();
                    afficherCommentairesDuPost(selectedPost);
                }
            });
            return row;
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void ajouter(ActionEvent event) {
        String userIdText = textfield_userId.getText().trim();
        String description = textfield_description.getText().trim();
        String dateCreation = textfield_dateCreation.getText().trim();
        String nbReactionsText = textfield_nbReactions.getText().trim();
        String imagePath = textfield_imagePath.getText().trim();

        if (userIdText.isEmpty() || description.isEmpty() || dateCreation.isEmpty() || nbReactionsText.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (!dateCreation.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
            showAlert("Format de date invalide", "La date doit être au format : yyyy-MM-dd HH:mm:ss (ex : 2025-04-27 00:00:00).");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            int nbReactions = Integer.parseInt(nbReactionsText);

            Post post = new Post(userId, description, dateCreation, nbReactions, imagePath);
            servicePost.ajouter(post);
            afficherPosts();
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "ID utilisateur et nombre de réactions doivent être des entiers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout.");
        }
    }

    @FXML
    void updateDescription(ActionEvent event) {
        Post selectedPost = tableview_post.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showAlert("Aucun post sélectionné", "Veuillez sélectionner un post à modifier.");
            return;
        }

        String description = textfield_description.getText().trim();
        String imagePath = textfield_imagePath.getText().trim();

        if (description.isEmpty()) {
            showAlert("Champ manquant", "Le champ description ne peut pas être vide.");
            return;
        }

        selectedPost.setDescription(description);
        selectedPost.setImagePath(imagePath);

        try {
            servicePost.updateDescription(selectedPost);
            afficherPosts();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le post.");
        }
    }

    @FXML
    void delete(ActionEvent event) {
        Post selectedPost = tableview_post.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showAlert("Aucun post sélectionné", "Veuillez sélectionner un post à supprimer.");
            return;
        }

        try {
            servicePost.delete(selectedPost.getId());
            afficherPosts();
            showAlert("BIEN", "le post est bien supprimé.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le post.");
        }
    }

    @FXML
    void rechercherPost(ActionEvent event) {
        String recherche = textfield_recherche.getText().trim();
        if (recherche.isEmpty()) {
            afficherPosts();
            return;
        }

        try {
            List<Post> posts = servicePost.afficherAll();
            List<Post> filtered = posts.stream()
                    .filter(p -> p.getDescription() != null && p.getDescription().toLowerCase().contains(recherche.toLowerCase()))
                    .toList();

            tableview_post.setItems(FXCollections.observableList(filtered));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la recherche.");
        }
    }

    @FXML
    void ouvrirGestionCommentaires(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/manage_commentaire.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) button_gestionCommentaires.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void afficherPosts() {
        try {
            List<Post> posts = servicePost.afficherAll();
            ObservableList<Post> observablePosts = FXCollections.observableList(posts);
            tableview_post.setItems(observablePosts);

            // Lier les colonnes avec les propriétés de l'objet Post
            column_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            column_userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
            column_description.setCellValueFactory(new PropertyValueFactory<>("description"));
            column_dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
            column_nbReactions.setCellValueFactory(new PropertyValueFactory<>("nbReactions"));
            column_imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

            // Configurer la colonne d'image
            column_imagePath.setCellFactory(col -> new TableCell<>() {
                private final ImageView imageView = new ImageView();

                {
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    imageView.setPreserveRatio(true);  // Pour garder les proportions de l'image
                }

                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);
                    if (empty || imagePath == null || imagePath.isEmpty()) {
                        setGraphic(null);  // Si l'image est vide, ne rien afficher
                    } else {
                        try {
                            // Créer un fichier à partir du chemin d'image
                            File file = new File(imagePath);
                            if (file.exists()) {
                                // Créer l'URI du fichier et charger l'image
                                Image image = new Image(file.toURI().toString());
                                imageView.setImage(image);
                                setGraphic(imageView);
                            } else {
                                setGraphic(null);  // Si le fichier n'existe pas, ne rien afficher
                            }
                        } catch (Exception e) {
                            // En cas d'exception, ne rien afficher
                            e.printStackTrace();
                            setGraphic(null);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void chooseImageForPost(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            textfield_imagePath.setText(selectedFile.getAbsolutePath());
        }
    }
    @FXML
    private Button btnPartager;


    @FXML
    void onPartagerClicked(ActionEvent event) {


    }

    private void afficherCommentairesDuPost(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/event_project/PostDetails.fxml"));
            Parent root = loader.load();

            PostDetailController controller = loader.getController();
            controller.setPost(post); // envoie l'objet Post au contrôleur

            Stage stage = new Stage();
            stage.setTitle("Commentaires du Post");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les commentaires.");
        }
    }


    @FXML

    public void handleLike(ActionEvent actionEvent) {
        Post selectedPost = tableview_post.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showAlert("Aucun post sélectionné", "Veuillez sélectionner un post à liker.");
            return;
        }

        selectedPost.setNbReactions(selectedPost.getNbReactions() + 1); // +1 même si c'est 0

        try {
            servicePost.updateNbReactions(selectedPost);
            afficherPosts();
            showAlert("Succès", "Vous avez liké le post.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de liker le post.");
        }
    }


    public void handleDislike(ActionEvent actionEvent) {
        Post selectedPost = tableview_post.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showAlert("Aucun post sélectionné", "Veuillez sélectionner un post à disliker.");
            return;
        }

        selectedPost.setNbReactions(selectedPost.getNbReactions() + 1); // même traitement que like

        try {
            servicePost.updateNbReactions(selectedPost);
            afficherPosts();
            showAlert("Succès", "Vous avez disliké le post.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de disliker le post.");
        }
    }

}
