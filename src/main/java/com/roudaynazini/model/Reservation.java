package com.roudaynazini.model;

import java.time.LocalDate;

public class Reservation {
    private Long id;
    private String clientName;
    private LocalDate eventDate;
    private String status;
    private String notes;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Reservation() {
    }

    public Reservation(Long id, String clientName, LocalDate eventDate, String status, String notes) {
        this.id = id;
        this.clientName = clientName;
        this.eventDate = eventDate;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", clientName='" + clientName + '\'' +
                ", eventDate=" + eventDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
} 