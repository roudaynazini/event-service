package org.example.event_project.service;


import org.example.event_project.entities.EventSponsor;
import org.example.event_project.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEventSponsor {

    private Connection con = DataSource.getInstance().getCon();
    private Statement stmt;

    public ServiceEventSponsor() {
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // CREATE - ajouter
    public boolean ajouter(EventSponsor es) throws SQLException {
        boolean result = false;
        String req = "INSERT INTO `event_sponsors` (`event_id`, `sponsor_id`, `sponsorship_level`) " +
                "VALUES (" + es.getEventId() + ", " + es.getSponsorId() + ", '" + es.getSponsorshipLevel() + "');";
        int x = stmt.executeUpdate(req);
        if (x > 0) {
            result = true;
        }
        return result;
    }

    // CREATE - ajouterPstm
    public void ajouterPstm(EventSponsor es) throws SQLException {
        String req = "INSERT INTO `event_sponsors` (`event_id`, `sponsor_id`, `sponsorship_level`) VALUES (?, ?, ?);";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, es.getEventId());
        pre.setInt(2, es.getSponsorId());
        pre.setString(3, es.getSponsorshipLevel());
        pre.executeUpdate();
    }

    // READ - getAll
    public List<EventSponsor> getAll() throws SQLException {
        List<EventSponsor> list = new ArrayList<>();
        String req = "SELECT * FROM event_sponsors";
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            int eventId = rs.getInt("event_id");
            int sponsorId = rs.getInt("sponsor_id");
            String level = rs.getString("sponsorship_level");

            EventSponsor es = new EventSponsor(eventId, sponsorId, level);
            list.add(es);
        }
        return list;
    }

    // READ - getById (event_id, sponsor_id)
    public EventSponsor getById(int eventId, int sponsorId) throws SQLException {
        String req = "SELECT * FROM event_sponsors WHERE event_id = ? AND sponsor_id = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, eventId);
        pre.setInt(2, sponsorId);
        ResultSet rs = pre.executeQuery();

        if (rs.next()) {
            return new EventSponsor(
                    rs.getInt("event_id"),
                    rs.getInt("sponsor_id"),
                    rs.getString("sponsorship_level")
            );
        }
        return null;
    }

    // UPDATE
    public boolean update(EventSponsor es) throws SQLException {
        String req = "UPDATE event_sponsors SET sponsorship_level = ? WHERE event_id = ? AND sponsor_id = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, es.getSponsorshipLevel());
        pre.setInt(2, es.getEventId());
        pre.setInt(3, es.getSponsorId());

        int x = pre.executeUpdate();
        return x > 0;
    }

    // DELETE
    public boolean delete(EventSponsor es) throws SQLException {
        String req = "DELETE FROM event_sponsors WHERE event_id = ? AND sponsor_id = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, es.getEventId());
        pre.setInt(2, es.getSponsorId());

        int x = pre.executeUpdate();
        return x > 0;
    }

    // SEARCH - rechercher par sponsorship_level
    public List<EventSponsor> rechercherParLevel(String levelSearch) throws SQLException {
        List<EventSponsor> list = new ArrayList<>();
        String req = "SELECT * FROM event_sponsors WHERE sponsorship_level LIKE ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, "%" + levelSearch + "%");
        ResultSet rs = pre.executeQuery();

        while (rs.next()) {
            EventSponsor es = new EventSponsor(
                    rs.getInt("event_id"),
                    rs.getInt("sponsor_id"),
                    rs.getString("sponsorship_level")
            );
            list.add(es);
        }
        return list;
    }
}
