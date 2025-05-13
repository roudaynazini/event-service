package org.example.post_events.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.post_events.entities.Commentaire;
import org.example.post_events.entities.Post;
import org.example.post_events.service.ServiceCommentaire;
import org.example.post_events.service.ServicePost;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.util.List;

public class PostDetailController {
    @FXML
    private TableView<Post> postTableView;

    @FXML
    private TableColumn<Post, String> descriptionColumn;

    @FXML
    private TableColumn<Post, String> commentaireColumn;

    @FXML
    private Label descriptionLabel;

    @FXML
    private TextArea contentTextArea;

    private final ServicePost servicePost = new ServicePost();
    private final ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    private Post currentPost;

    public void loadPosts() {
        List<Post> posts = servicePost.getAllPosts();
        postTableView.getItems().clear();

        // Configurer les colonnes (à faire une seule fois)
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        commentaireColumn.setCellValueFactory(cellData -> {
            Post post = cellData.getValue();
            List<Commentaire> commentaires = serviceCommentaire.getCommentairesAvecPost(post.getId());
            StringBuilder sb = new StringBuilder();
            for (Commentaire c : commentaires) {
                sb.append("🗨️ ").append(c.getContenu())
                        .append(" (le ").append(c.getDateCommentaire()).append(")\n");
            }
            return new SimpleStringProperty(sb.toString());
        });

        postTableView.getItems().addAll(posts);
    }

    @FXML
    private void handlePostDoubleClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            Post selectedPost = postTableView.getSelectionModel().getSelectedItem();
            if (selectedPost != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/post_events/views/PostDetailView.fxml"));
                Parent root = loader.load();

                PostDetailController controller = loader.getController();
                controller.setPost(selectedPost);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Détails du Post");
                stage.show();
            }
        }
    }

    public void setPost(Post post) {
        this.currentPost = post;
        descriptionLabel.setText(post.getDescription());
        contentTextArea.setText(post.getDescription());
        // Charger et afficher les commentaires du post sélectionné
        postTableView.getItems().clear();
        // Ajoute uniquement ce post dans la table (pour la vue détail)
        postTableView.getItems().add(post);
        // Configuration des colonnes (déjà faite dans loadPosts mais doublé ici si besoin)
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        commentaireColumn.setCellValueFactory(cellData -> {
            List<Commentaire> commentaires = serviceCommentaire.getCommentairesAvecPost(cellData.getValue().getId());
            StringBuilder sb = new StringBuilder();
            for (Commentaire c : commentaires) {
                sb.append("🗨️ ").append(c.getContenu())
                        .append(" (le ").append(c.getDateCommentaire()).append(")\n");
            }
            return new SimpleStringProperty(sb.toString());
        });
    }

    @FXML
    private void handleBack() {
        Stage stage =
                (Stage) descriptionLabel.getScene().getWindow();
        stage.close();
    }
}
