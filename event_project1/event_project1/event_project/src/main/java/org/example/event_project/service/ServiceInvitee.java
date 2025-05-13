package org.example.event_project.service;


import org.example.event_project.Utils.DataSource;
import org.example.event_project.entities.Invitee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceInvitee {

    private Connection con = DataSource.getInstance().getCon();

    public boolean ajouterInvitee(Invitee invitee) throws SQLException {
        String sql = "INSERT INTO invitee(nomInvitee, emailInvitee, tlf, adresse) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, invitee.getNomInvitee());
            pst.setString(2, invitee.getEmail());
            pst.setString(3, invitee.getTlf());
            pst.setString(4, invitee.getAdresse());
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Invité ajouté !");
                return true;
            } else {
                System.out.println("Échec de l'ajout de l'invité.");
                return false;
            }
        }
    }



    public boolean modifierInvitee(Invitee invitee) throws SQLException {
        String sql = "UPDATE invitee SET nomInvitee=?, emailInvitee=?, tlf=?, adresse=? WHERE idInvitee=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, invitee.getNomInvitee());
            pst.setString(2, invitee.getEmail());
            pst.setString(3, invitee.getTlf());
            pst.setString(4, invitee.getAdresse());
            pst.setInt(5, invitee.getIdInvitee());
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Invité modifié !");
                return true;
            } else {
                System.out.println("Échec de la modification de l'invité.");
                return false;
            }
        }
    }





    public Invitee getInviteeById(int id) throws SQLException {
        String req = "SELECT * FROM invitee WHERE idInvitee = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idInvitee = rs.getInt("idInvitee");
                    String nomInvitee = rs.getString("nomInvitee");
                    String emailInvitee = rs.getString("emailInvitee");
                    String tlf = rs.getString("tlf");
                    String adresse = rs.getString("adresse");

                    return new Invitee(idInvitee, nomInvitee, emailInvitee, tlf, adresse);
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'invité : " + e.getMessage());
            throw e;
        }
    }





    public boolean supprimerInvitee(int id) throws SQLException {
        String sql = "DELETE FROM invitee WHERE idInvitee=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Invité supprimé !");
                return true;
            } else {
                System.out.println("Échec de la suppression de l'invité.");
                return false;
            }
        }
    }




    public List<Invitee> getAllInvitees() throws SQLException {
        List<Invitee> list = new ArrayList<>();
        String sql = "SELECT * FROM invitee";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invitee i = new Invitee(
                        rs.getInt("idInvitee"),
                        rs.getString("nomInvitee"),
                        rs.getString("emailInvitee"),
                        rs.getString("tlf"),
                        rs.getString("adresse")
                );
                list.add(i);
            }

        } catch (SQLException e) {
            System.err.println("Erreur récupération invités: " + e.getMessage());
            throw e;
        }

        return list;
    }

    public List<String> getAllEmails() {
        List<String> emails = new ArrayList<>();
        String req = "SELECT emailInvitee FROM invitee";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                emails.add(rs.getString("emailInvitee"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des emails: " + e.getMessage());
        }

        return emails;
    }

}
