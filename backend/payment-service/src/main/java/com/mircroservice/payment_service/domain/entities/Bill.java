package com.mircroservice.payment_service.domain.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.payment_service.domain.enums.BillStatus;
import com.mircroservice.payment_service.domain.enums.PaymentMethod;

@Document(collection = "bills")
public class Bill {
    @Id
    private String id;

    @Indexed(unique = true)
    private String tripId;

    private String riderId;
    private String pricingQuoteId;
    private long amount;
    private String currency;
    private PaymentMethod method = PaymentMethod.VNPAY;
    private BillStatus status = BillStatus.PENDING;
    private List<PaymentAttempt> attempts = new ArrayList<>();
    private List<LedgerTransaction> ledgerTransactions = new ArrayList<>();
    private List<PaymentOutboxEvent> outboxEvents = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    @Version
    private Long version;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    public String getPricingQuoteId() { return pricingQuoteId; }
    public void setPricingQuoteId(String pricingQuoteId) { this.pricingQuoteId = pricingQuoteId; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
    public List<PaymentAttempt> getAttempts() { return attempts; }
    public void setAttempts(List<PaymentAttempt> attempts) { this.attempts = attempts; }
    public List<LedgerTransaction> getLedgerTransactions() { return ledgerTransactions; }
    public void setLedgerTransactions(List<LedgerTransaction> ledgerTransactions) { this.ledgerTransactions = ledgerTransactions; }
    public List<PaymentOutboxEvent> getOutboxEvents() { return outboxEvents; }
    public void setOutboxEvents(List<PaymentOutboxEvent> outboxEvents) { this.outboxEvents = outboxEvents; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
