package com.roudaynazini.model;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private String clientName;
    private LocalDate eventDate;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Reservation() {
    }

    public Reservation(int id, String clientName, LocalDate eventDate, String status) {
        this.id = id;
        this.clientName = clientName;
        this.eventDate = eventDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
                '}';
    }
} 