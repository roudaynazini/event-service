package org.example.event_project.entities;
public class Commentaire {

        private int id;
        private int postId;
        private int userId;
        private String contenu;
        private String dateCommentaire;

        public Commentaire() {}

        public Commentaire(int id, int postId, int userId, String contenu, String dateCommentaire) {
            this.id = id;
            this.postId = postId;
            this.userId = userId;
            this.contenu = contenu;
            this.dateCommentaire = dateCommentaire;
        }

        public Commentaire(int postId, int userId, String contenu, String dateCommentaire) {
            this.postId = postId;
            this.userId = userId;
            this.contenu = contenu;
            this.dateCommentaire = dateCommentaire;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getPostId() { return postId; }
        public void setPostId(int postId) { this.postId = postId; }

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getContenu() { return contenu; }
        public void setContenu(String contenu) { this.contenu = contenu; }

        public String getDateCommentaire() { return dateCommentaire; }
        public void setDateCommentaire(String dateCommentaire) { this.dateCommentaire = dateCommentaire; }

        @Override
        public String toString() {
            return "Commentaire{" +
                    "id=" + id +
                    ", postId=" + postId +
                    ", userId=" + userId +
                    ", contenu='" + contenu + '\'' +
                    ", dateCommentaire='" + dateCommentaire + '\'' +
                    '}';
        }



}

