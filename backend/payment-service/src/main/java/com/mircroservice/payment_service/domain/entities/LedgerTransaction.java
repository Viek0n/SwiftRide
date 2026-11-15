package com.mircroservice.payment_service.domain.entities;

import java.time.Instant;
import java.util.List;

public class LedgerTransaction {
    private String id;
    private String type;
    private String reference;
    private List<LedgerEntry> entries;
    private Instant createdAt;

    public LedgerTransaction() { }
    public LedgerTransaction(String id, String type, String reference, List<LedgerEntry> entries, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.reference = reference;
        this.entries = entries;
        this.createdAt = createdAt;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public List<LedgerEntry> getEntries() { return entries; }
    public void setEntries(List<LedgerEntry> entries) { this.entries = entries; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
