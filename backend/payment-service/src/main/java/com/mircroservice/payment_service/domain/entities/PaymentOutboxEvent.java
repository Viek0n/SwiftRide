package com.mircroservice.payment_service.domain.entities;

import java.time.Instant;

public class PaymentOutboxEvent {
    private String eventId;
    private String eventType;
    private String tripId;
    private String riderId;
    private long amount;
    private String currency;
    private String gatewayTransactionId;
    private Instant occurredAt;
    private Instant publishedAt;

    public PaymentOutboxEvent() { }

    public PaymentOutboxEvent(String eventId, String eventType, String tripId, String riderId, long amount,
            String currency, String gatewayTransactionId, Instant occurredAt, Instant publishedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.tripId = tripId;
        this.riderId = riderId;
        this.amount = amount;
        this.currency = currency;
        this.gatewayTransactionId = gatewayTransactionId;
        this.occurredAt = occurredAt;
        this.publishedAt = publishedAt;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
}
