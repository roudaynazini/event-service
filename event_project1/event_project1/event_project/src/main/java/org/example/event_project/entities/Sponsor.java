package org.example.event_project.entities;

public class Sponsor {
    private int sponsorId;
    private String name;
    private String logoUrl;
    private String website;
    private String description;

    // Constructor
    public Sponsor(int sponsorId, String name, String logoUrl, String website, String description) {
        this.sponsorId = sponsorId;
        this.name = name;
        this.logoUrl = logoUrl;
        this.website = website;
        this.description = description;
    }
    public Sponsor( String name, String logoUrl, String website, String description) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.website = website;
        this.description = description;
    }

    // Getters and Setters
    public int getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(int sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
