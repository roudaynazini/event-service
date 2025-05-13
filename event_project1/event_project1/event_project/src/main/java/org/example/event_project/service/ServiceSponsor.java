package org.example.event_project.service;

import org.example.event_project.Utils.DataSource;
import org.example.event_project.entities.Sponsor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceSponsor {

    private Connection con = DataSource.getInstance().getCon();
    private Statement stmt;

    public ServiceSponsor() {
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // CREATE - ajouter (simple statement)
    public boolean ajouter(Sponsor s) throws SQLException {
        boolean result = false;
        String req = "INSERT INTO `sponsors` (`name`, `logo_url`, `website`, `description`) " +
                "VALUES ('" + s.getName() + "', '" + s.getLogoUrl() + "', '" + s.getWebsite() + "', '" + s.getDescription() + "');";
        int x = stmt.executeUpdate(req);
        if (x > 0) {
            result = true;
        }
        return result;
    }

    // CREATE - ajouter (prepared statement)
    public void ajouterPstm(Sponsor s) throws SQLException {
        String req = "INSERT INTO `sponsors` (`name`, `logo_url`, `website`, `description`) VALUES (?, ?, ?, ?);";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, s.getName());
        pre.setString(2, s.getLogoUrl());
        pre.setString(3, s.getWebsite());
        pre.setString(4, s.getDescription());
        pre.executeUpdate();
    }

    // READ - getAll
    public List<Sponsor> getAll() throws SQLException {
        List<Sponsor> list = new ArrayList<>();
        String req = "SELECT * FROM sponsors";
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("sponsor_id");
            String name = rs.getString("name");
            String logoUrl = rs.getString("logo_url");
            String website = rs.getString("website");
            String description = rs.getString("description");

            Sponsor sponsor = new Sponsor(id, name, logoUrl, website, description);
            list.add(sponsor);
        }
        return list;
    }

    // READ - getById
    public Sponsor getById(int id) throws SQLException {
        String req = "SELECT * FROM sponsors WHERE sponsor_id = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);
        ResultSet rs = pre.executeQuery();

        if (rs.next()) {
            return new Sponsor(
                    rs.getInt("sponsor_id"),
                    rs.getString("name"),
                    rs.getString("logo_url"),
                    rs.getString("website"),
                    rs.getString("description")
            );
        }
        return null;
    }

    // UPDATE
    public boolean update(Sponsor s) throws SQLException {
        String req = "UPDATE sponsors SET name = ?, logo_url = ?, website = ?, description = ? WHERE sponsor_id = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, s.getName());
        pre.setString(2, s.getLogoUrl());
        pre.setString(3, s.getWebsite());
        pre.setString(4, s.getDescription());
        pre.setInt(5, s.getSponsorId());

        int x = pre.executeUpdate();
        return x > 0;
    }

    // DELETE
    public boolean delete(Sponsor s) throws SQLException {
        String req = "DELETE FROM sponsors WHERE sponsor_id = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, s.getSponsorId());

        int x = pre.executeUpdate();
        return x > 0;
    }

    // SEARCH - rechercher par nom
    public List<Sponsor> rechercherParNom(String nomRecherche) throws SQLException {
        List<Sponsor> list = new ArrayList<>();
        String req = "SELECT * FROM sponsors WHERE name LIKE ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setString(1, "%" + nomRecherche + "%");
        ResultSet rs = pre.executeQuery();

        while (rs.next()) {
            Sponsor sponsor = new Sponsor(
                    rs.getInt("sponsor_id"),
                    rs.getString("name"),
                    rs.getString("logo_url"),
                    rs.getString("website"),
                    rs.getString("description")
            );
            list.add(sponsor);
        }
        return list;
    }
}