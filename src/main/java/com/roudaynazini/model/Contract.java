package com.roudaynazini.model;

import java.time.LocalDate;

public class Contract {
    private Long id;
    private String contractNumber;
    private String contractType;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long reservationId;
    private Double totalAmount;
    private String terms;
    private String notes;

    // Default constructor
    public Contract() {}

    // Constructor with all fields
    public Contract(Long id, String contractNumber, String contractType, String status, LocalDate startDate, 
                   LocalDate endDate, Long reservationId, Double totalAmount, String terms, String notes) {
        this.id = id;
        this.contractNumber = contractNumber;
        this.contractType = contractType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservationId = reservationId;
        this.totalAmount = totalAmount;
        this.terms = terms;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", contractNumber='" + contractNumber + '\'' +
                ", contractType='" + contractType + '\'' +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reservationId=" + reservationId +
                ", totalAmount=" + totalAmount +
                ", terms='" + terms + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
} 