package com.mircroservice.payment_service.domain.entities;

import java.time.Instant;

import com.mircroservice.payment_service.domain.enums.PaymentAttemptStatus;

public class PaymentAttempt {
    private String id;
    private String idempotencyKey;
    private PaymentAttemptStatus status;
    private String paymentUrl;
    private String gatewayTransactionId;
    private String responseCode;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public PaymentAttemptStatus getStatus() { return status; }
    public void setStatus(PaymentAttemptStatus status) { this.status = status; }
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
