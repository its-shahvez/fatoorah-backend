package com.fatoorah.pro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceId;
    private String invoiceNumber;
    private String action; // CREATED, CLEARED_BY_ZATCA, REPORTED_B2C, TAMPER_DETECTED, CSID_RENEWED
    private String actor;  // e.g. Mohd Shahvez (SUPER_ADMIN) or ZATCA-FATOORA-GW
    private String timestamp;

    @Column(length = 1000)
    private String details;

    public AuditLogEntity() {}

    public AuditLogEntity(String invoiceId, String invoiceNumber, String action, String actor, String timestamp, String details) {
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.action = action;
        this.actor = actor;
        this.timestamp = timestamp;
        this.details = details;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}