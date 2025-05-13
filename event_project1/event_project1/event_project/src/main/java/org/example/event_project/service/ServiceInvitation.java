package org.example.event_project.service;



import org.example.event_project.Utils.DataSource;
import org.example.event_project.entities.Invitation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

public class ServiceInvitation {

    private Connection con = DataSource.getInstance().getCon();
    private EmailService emailService = new EmailService();





    public boolean ajouterInvitation(Invitation invitation) throws IOException {
        String sql = "INSERT INTO invitation(nomEvenement, dateEvenement, emailInvitee, message) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, invitation.getNomEvenement());
            pst.setDate(2, new java.sql.Date(invitation.getDateEvenement().getTime()));
            pst.setString(3, invitation.getEmailInvitee());
            pst.setString(4, invitation.getMessage());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Invitation ajoutée !");

                // Envoyer un e-mail à l'invité
                String subject = "Invitation à " + invitation.getNomEvenement();
                String content = "Vous êtes invité à " + invitation.getNomEvenement() + " le " + invitation.getDateEvenement() + ". " + invitation.getMessage();


                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajout invitation: " + e.getMessage());
            return false;
        }
    }







    public boolean modifierInvitation(Invitation invitation) {
        String sql = "UPDATE invitation SET nomEvenement=?, dateEvenement=?, emailInvitee=?, message=? WHERE idInvitation=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, invitation.getNomEvenement());
            pst.setDate(2, new java.sql.Date(invitation.getDateEvenement().getTime()));
            pst.setString(3, invitation.getEmailInvitee());
            pst.setString(4, invitation.getMessage());
            pst.setInt(5, invitation.getIdInvitation());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Invitation modifiée !");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur modification invitation: " + e.getMessage());
            return false;
        }
    }



    public Invitation getInvitationById(int id) throws SQLException {
        String req = "SELECT * FROM invitation WHERE idInvitation = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idInvitation = rs.getInt("idInvitation");
                    String nomEvenement = rs.getString("nomEvenement");
                    Date dateEvenement = rs.getDate("dateEvenement");
                    String emailInvitee = rs.getString("emailInvitee");
                    String message = rs.getString("message");

                    return new Invitation(idInvitation, nomEvenement, dateEvenement, emailInvitee, message);
                }
                return null; // Aucune invitation trouvée avec cet ID
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'invitation : " + e.getMessage());
            throw e;
        }
    }



    public boolean supprimerInvitation(int id) {
        String sql = "DELETE FROM invitation WHERE idInvitation=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Invitation supprimée !");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur suppression invitation: " + e.getMessage());
            return false;
        }
    }





    public List<Invitation> getAllInvitations() throws SQLException {
        List<Invitation> liste = new ArrayList<>();
        String sql = "SELECT * FROM invitation";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invitation i = new Invitation(
                        rs.getInt("idInvitation"),
                        rs.getString("nomEvenement"),
                        rs.getDate("dateEvenement"),
                        rs.getString("emailInvitee"),
                        rs.getString("message")
                );
                liste.add(i);
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération invitations: " + e.getMessage());
            throw e;
        }

        return liste;
    }
}
