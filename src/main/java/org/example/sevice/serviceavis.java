package org.example.sevice;

import org.example.Utils.DataSource;
import org.example.entities.avis;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class serviceavis {

    private Connection con = DataSource.getInstance().getCon();
    private Statement stmt;

    public serviceavis() {
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // CREATE - ajouter un avis avec une note (étoiles)
    public boolean ajouter(avis a) throws SQLException {
        String req = "INSERT INTO avis (idUtilisateur, idEvenement, commentaire, note, dateAvis) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, a.getIdUtilisateur());
        pre.setInt(2, a.getIdEvenement());
        pre.setString(3, a.getCommentaire());
        pre.setInt(4, a.getNote());  // La note (étoiles) est un entier de 1 à 5
        pre.setDate(5, new Date(a.getDateAvis().getTime()));

        return pre.executeUpdate() > 0;
    }

    // READ - récupérer tous les avis
    public List<avis> getAll() throws SQLException {
        List<avis> list = new ArrayList<>();
        String req = "SELECT * FROM avis";
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            avis a = new avis(
                    rs.getInt("idUtilisateur"),
                    rs.getInt("idEvenement"),
                    rs.getString("commentaire"),
                    rs.getInt("note"),  // La note
                    rs.getDate("dateAvis")
            );
            a.setIdAvis(rs.getInt("idAvis"));
            list.add(a);
        }
        return list;
    }

    // READ - récupérer un avis par son ID
    public avis getById(int idAvis) throws SQLException {
        String req = "SELECT * FROM avis WHERE idAvis = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idAvis);
        ResultSet rs = pre.executeQuery();

        if (rs.next()) {
            avis a = new avis(
                    rs.getInt("idUtilisateur"),
                    rs.getInt("idEvenement"),
                    rs.getString("commentaire"),
                    rs.getInt("note"),  // La note
                    rs.getDate("dateAvis")
            );
            a.setIdAvis(rs.getInt("idAvis"));
            return a;
        }
        return null;
    }

    // UPDATE - mettre à jour un avis
    public boolean update(avis a) throws SQLException {
        String req = "UPDATE avis SET idUtilisateur = ?, idEvenement = ?, commentaire = ?, note = ?, dateAvis = ? WHERE idAvis = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, a.getIdUtilisateur());
        pre.setInt(2, a.getIdEvenement());
        pre.setString(3, a.getCommentaire());
        pre.setInt(4, a.getNote());  // Mise à jour de la note
        pre.setDate(5, new Date(a.getDateAvis().getTime()));
        pre.setInt(6, a.getIdAvis()); // WHERE clause

        return pre.executeUpdate() > 0;
    }

    // DELETE - supprimer un avis
    public boolean delete(int idAvis) throws SQLException {
        String req = "DELETE FROM avis WHERE idAvis = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idAvis);

        return pre.executeUpdate() > 0;
    }

    // SEARCH - rechercher les avis par note (étoiles)
    public List<avis> rechercherParNote(int note) throws SQLException {
        List<avis> list = new ArrayList<>();
        String req = "SELECT * FROM avis WHERE note = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, note);  // Rechercher les avis avec la même note
        ResultSet rs = pre.executeQuery();

        while (rs.next()) {
            avis a = new avis(
                    rs.getInt("idUtilisateur"),
                    rs.getInt("idEvenement"),
                    rs.getString("commentaire"),
                    rs.getInt("note"),  // La note
                    rs.getDate("dateAvis")
            );
            a.setIdAvis(rs.getInt("idAvis"));
            list.add(a);
        }
        return list;
    }

    // Vérifier si le commentaire contient des mots inappropriés
    private static final List<String> motsInappropries = List.of("insulte", "violence", "abus", "fuck");

    public boolean contientMotsInappropries(String commentaire) {
        for (String mot : motsInappropries) {
            if (commentaire.toLowerCase().contains(mot.toLowerCase())) {
                return true;  // Le commentaire contient un mot inapproprié
            }
        }
        return false;  // Aucun mot inapproprié trouvé
    }
    public List<Map<String, Object>> getAvisEtReclamations(int idUtilisateur, int idEvenement) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String req = "SELECT a.idAvis, a.idUtilisateur, a.idEvenement, a.commentaire, a.note, a.dateAvis, " +
                "r.idReclamation, r.description, r.etat, r.dateReclamation " +
                "FROM avis a " +
                "JOIN reclamation r ON a.idUtilisateur = r.idUtilisateur AND a.idEvenement = r.idEvenement " +
                "WHERE a.idUtilisateur = ? AND a.idEvenement = ?";

        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idUtilisateur);
        pre.setInt(2, idEvenement);

        ResultSet rs = pre.executeQuery();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("idAvis", rs.getInt("idAvis"));
            row.put("idUtilisateur", rs.getInt("idUtilisateur"));
            row.put("idEvenement", rs.getInt("idEvenement"));
            row.put("commentaire", rs.getString("commentaire"));
            row.put("note", rs.getInt("note"));
            row.put("dateAvis", rs.getDate("dateAvis"));
            row.put("idReclamation", rs.getInt("idReclamation"));
            row.put("description", rs.getString("description"));
            row.put("etat", rs.getString("etat"));
            row.put("dateReclamation", rs.getDate("dateReclamation"));

            list.add(row);
        }

        return list;
    }

    public boolean existeDejaAvis(int idUtilisateur, int idEvenement, String commentaire) throws SQLException {
        String req = "SELECT * FROM avis WHERE idUtilisateur = ? AND idEvenement = ? AND commentaire = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idUtilisateur);
        pre.setInt(2, idEvenement);
        pre.setString(3, commentaire);

        ResultSet rs = pre.executeQuery();
        return rs.next(); // Si un avis similaire existe, retourne true
    }



}
