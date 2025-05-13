package org.example.event_project.entities;

public class EventSponsor {
    private int eventId;
    private int sponsorId;
    private String sponsorshipLevel;

    // Constructor
    public EventSponsor(int eventId, int sponsorId, String sponsorshipLevel) {
        this.eventId = eventId;
        this.sponsorId = sponsorId;
        this.sponsorshipLevel = sponsorshipLevel;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(int sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getSponsorshipLevel() {
        return sponsorshipLevel;
    }

    public void setSponsorshipLevel(String sponsorshipLevel) {
        this.sponsorshipLevel = sponsorshipLevel;
    }
}
