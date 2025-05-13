package org.example.sevice;
import org.example.entities.avis;
import org.example.entities.reclamation;
import org.example.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class servicereclamatin {

    private Connection con = DataSource.getInstance().getCon();
    private Statement stmt;

    public servicereclamatin() {
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public boolean ajouter(reclamation r) throws SQLException {
        boolean result = false;
        String req = "INSERT INTO reclamation (idUtilisateur, idEvenement, description, etat, dateReclamation) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pre = con.prepareStatement(req)) {
            pre.setInt(1, r.getIdUtilisateur());
            pre.setInt(2, r.getIdEvenement());
            pre.setString(3, r.getDescription());
            pre.setString(4, r.getEtat());
            pre.setDate(5, new java.sql.Date(r.getDateReclamation().getTime()));

            int x = pre.executeUpdate();
            if (x > 0) {
                result = true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());  // Affiche l'erreur exacte
            throw e;  // Relancer l'exception pour la gestion plus haut dans la pile
        }
        return result;
    }


    // CREATE - ajouterPstm
    public void ajouterPstm(reclamation r) throws SQLException {
        String req = "INSERT INTO reclamation (idUtilisateur, idEvenement, description, etat, dateReclamation) " +
                "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, r.getIdUtilisateur());
        pre.setInt(2, r.getIdEvenement());
        pre.setString(3, r.getDescription());
        pre.setString(4, r.getEtat());
        pre.setDate(5, new java.sql.Date(r.getDateReclamation().getTime()));
        pre.executeUpdate();
    }

    // READ - getAll
    public List<reclamation> getAll() throws SQLException {
        List<reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            reclamation r = new reclamation(
                    rs.getInt("idUtilisateur"),
                    rs.getInt("idReclamation"),
                    rs.getInt("idEvenement"),
                    rs.getString("description"),
                    rs.getDate("dateReclamation"),
                    rs.getString("etat")
            );
            list.add(r);
        }
        return list;
    }

    // READ - getById
    public reclamation getById(int idReclamation) throws SQLException {
        String req = "SELECT * FROM reclamation WHERE idReclamation = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idReclamation);
        ResultSet rs = pre.executeQuery();

        if (rs.next()) {
            return new reclamation(
                    rs.getInt("idUtilisateur"),
                    rs.getInt("idReclamation"),
                    rs.getInt("idEvenement"),
                    rs.getString("description"),
                    rs.getDate("dateReclamation"),
                    rs.getString("etat")
            );
        }
        return null;
    }

    // UPDATE
    public boolean update(reclamation r) throws SQLException {
        String req = "UPDATE reclamation SET idUtilisateur = ?, idEvenement = ?, description = ?, etat = ?, dateReclamation = ? WHERE idReclamation = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, r.getIdUtilisateur());
        pre.setInt(2, r.getIdEvenement());
        pre.setString(3, r.getDescription());
        pre.setString(4, r.getEtat());
        pre.setDate(5, new java.sql.Date(r.getDateReclamation().getTime()));
        pre.setInt(6, r.getIdReclamation());

        int x = pre.executeUpdate();
        return x > 0;
    }
    public boolean existeDejaReclamation(int idUtilisateur, int idEvenement, String description) throws SQLException {
        String req = "SELECT * FROM reclamation WHERE idUtilisateur = ? AND idEvenement = ? AND description = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idUtilisateur);
        pre.setInt(2, idEvenement);
        pre.setString(3, description);

        ResultSet rs = pre.executeQuery();
        return rs.next(); // Si une réclamation correspondante est trouvée, retourne true
    }

    // DELETE
    public boolean delete(int idReclamation) throws SQLException {
        String req = "DELETE FROM reclamation WHERE idReclamation = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, idReclamation);

        int x = pre.executeUpdate();
        return x > 0;
    }

    // SEARCH - rechercher par description
    public List<reclamation> rechercherParEtat(String etat) throws SQLException {
        List<reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE etat = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, etat);  // On filtre sur l'état
        ResultSet rs = pre.executeQuery();

        while (rs.next()) {
            reclamation r = new reclamation(
                    rs.getInt("idUtilisateur"),
                    rs.getInt("idReclamation"),
                    rs.getInt("idEvenement"),
                    rs.getString("description"),
                    rs.getDate("dateReclamation"),
                    rs.getString("etat")
            );
            list.add(r);
        }
        return list;
    }
    private static final List<String> motsInappropries = List.of("insulte", "violence", "abus", "fuck");

    public boolean contientMotsInappropries(String commentaire) {
        for (String mot : motsInappropries) {
            if (commentaire.toLowerCase().contains(mot.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public Map<Date, Integer> getReclamationsParJour() throws SQLException {
        Map<Date, Integer> reclamationsParJour = new LinkedHashMap<>(); // LinkedHashMap pour garder l'ordre

        String req = "SELECT DATE(dateReclamation) AS jour, COUNT(*) AS nombre " +
                "FROM reclamation GROUP BY DATE(dateReclamation) ORDER BY jour ASC";

        try (Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Date jour = rs.getDate("jour");
                int nombre = rs.getInt("nombre");
                reclamationsParJour.put(jour, nombre);
            }
        }

        return reclamationsParJour;
    }

}

