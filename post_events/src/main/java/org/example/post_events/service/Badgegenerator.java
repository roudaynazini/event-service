package org.example.post_events.service;

import org.example.post_events.Utils.DataSource;
import org.example.post_events.entities.Badge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Badgegenerator {

    // Méthode statique pour générer tous les badges
    public static List<Badge> generateBadges() {
        return new Badgegenerator().generateBadgesForAllUsers();
    }

    // Méthode qui parcourt tous les utilisateurs et génère les badges adaptés
    public List<Badge> generateBadgesForAllUsers() {
        List<Badge> badges = new ArrayList<>();
        String sql = """
                SELECT 
                    user_id,
                    COUNT(id) AS totalPosts,
                    SUM(CASE WHEN LENGTH(description) > 100 THEN 1 ELSE 0 END) AS longPosts,
                    SUM(CASE WHEN imagePath IS NOT NULL AND imagePath != '' THEN 1 ELSE 0 END) AS imagePosts,
                    SUM(nb_reactions) AS totalReactions,
                    SUM(CASE WHEN nb_reactions > 10 THEN 1 ELSE 0 END) AS controversialPosts
                FROM post
                GROUP BY user_id
                """;

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            LocalDate now = LocalDate.now();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                int totalPosts = rs.getInt("totalPosts");
                int longPosts = rs.getInt("longPosts");
                int imagePosts = rs.getInt("imagePosts");
                int totalReactions = rs.getInt("totalReactions");
                int controversialPosts = rs.getInt("controversialPosts");

                // 🧠 Thinker
                if (longPosts > 5) {
                    badges.add(new Badge("Thinker", "Tu réfléchis profondément avant d’écrire !", "🧠", now.minusDays(0), "Post avec plus de 100 caractères.", userId));
                }

                // 🔥 Popular
                if (totalReactions > 10) {
                    badges.add(new Badge("Popular", "Tes posts ont fait réagir la communauté !", "🔥", now.minusDays(0), "Plus de 10 réactions au total.", userId));
                }

                // 📸 Photogenic
                if (imagePosts > 3) {
                    badges.add(new Badge("Photogenic", "Tu postes beaucoup d’images !", "📸", now.minusDays(0), "4 posts avec images.", userId));
                }
                // 🚀 Active User
                if (totalPosts > 5) {
                    badges.add(new Badge("Active User", "Tu es super actif !", "🚀", now.minusDays(0), "Plus de 5 posts.", userId));
                }
                // 💬 Controversial
                if (controversialPosts > 0) {
                    badges.add(new Badge("Controversial", "Tes opinions ne laissent personne indifférent !", "💬", now.minusDays(0), "Au moins un post avec plus de 10 réactions.", userId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return badges;
    }
}
