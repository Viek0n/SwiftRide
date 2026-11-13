package com.mircroservice.payment_service.application.use_cases;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mircroservice.payment_service.application.dto.CheckoutResponse;
import com.mircroservice.payment_service.application.dto.PaymentStatusResponse;
import com.mircroservice.payment_service.application.dto.RideCompletedEvent;
import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.LedgerEntry;
import com.mircroservice.payment_service.domain.entities.LedgerTransaction;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;
import com.mircroservice.payment_service.domain.entities.PaymentOutboxEvent;
import com.mircroservice.payment_service.domain.enums.BillStatus;
import com.mircroservice.payment_service.domain.enums.PaymentAttemptStatus;
import com.mircroservice.payment_service.domain.repositories.BillRepository;
import com.mircroservice.payment_service.domain.value_objects.Money;
import com.mircroservice.payment_service.domain.interfaces.PaymentProcessor;

@Service
public class ProcessPayment {
    private static final String VND = "VND";
    private final BillRepository bills;
    private final PaymentProcessor paymentProcessor;

    public ProcessPayment(BillRepository bills, PaymentProcessor paymentProcessor) {
        this.bills = bills;
        this.paymentProcessor = paymentProcessor;
    }

    public Bill registerCompletedRide(RideCompletedEvent event) {
        if (event == null || !hasText(event.tripId()) || !hasText(event.riderId())) {
            throw badRequest("tripId and riderId are required");
        }
        if (!VND.equalsIgnoreCase(event.currency())) {
            throw badRequest("Only VND payments are supported");
        }
        try {
            new Money(event.amount(), VND);
        } catch (IllegalArgumentException ex) {
            throw badRequest(ex.getMessage());
        }
        if (event.amount() == 0 || event.amount() > Long.MAX_VALUE / 100) {
            throw badRequest("Trip amount is outside VNPay's supported range");
        }

        var existing = bills.findByTripId(event.tripId());
        if (existing.isPresent()) {
            return verifyDuplicateRideEvent(existing.get(), event);
        }

        Instant now = Instant.now();
        Bill bill = new Bill();
        bill.setId(UUID.randomUUID().toString());
        bill.setTripId(event.tripId());
        bill.setRiderId(event.riderId());
        bill.setPricingQuoteId(event.quoteId());
        bill.setAmount(event.amount());
        bill.setCurrency(VND);
        bill.setStatus(BillStatus.PENDING);
        bill.setAttempts(new ArrayList<>());
        bill.setLedgerTransactions(new ArrayList<>());
        bill.setOutboxEvents(new ArrayList<>());
        bill.setCreatedAt(now);
        bill.setUpdatedAt(now);

        try {
            return bills.save(bill);
        } catch (DuplicateKeyException ex) {
            return bills.findByTripId(event.tripId())
                    .map(saved -> verifyDuplicateRideEvent(saved, event))
                    .orElseThrow(() -> ex);
        }
    }

    public CheckoutResponse createCheckout(String tripId, String riderId, String idempotencyKey, String clientIp) {
        if (!hasText(idempotencyKey) || idempotencyKey.length() < 8 || idempotencyKey.length() > 120) {
            throw badRequest("Idempotency-Key must contain 8 to 120 characters");
        }
        Bill bill = findBill(tripId);
        verifyRider(bill, riderId);

        if (bill.getStatus() == BillStatus.SUCCEEDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Trip has already been paid");
        }
        if (bill.getStatus() == BillStatus.REQUIRES_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment requires support review");
        }

        PaymentAttempt sameRequest = bill.getAttempts().stream()
                .filter(attempt -> idempotencyKey.equals(attempt.getIdempotencyKey()))
                .findFirst()
                .orElse(null);
        if (sameRequest != null) {
            return checkoutResponse(bill, sameRequest);
        }
        if (bill.getAttempts().stream().anyMatch(attempt -> attempt.getStatus() == PaymentAttemptStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A payment attempt is still pending");
        }
        if (!paymentProcessor.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "VNPay is not configured");
        }

        Instant now = Instant.now();
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setId(UUID.randomUUID().toString());
        attempt.setIdempotencyKey(idempotencyKey);
        attempt.setStatus(PaymentAttemptStatus.PENDING);
        attempt.setCreatedAt(now);
        attempt.setUpdatedAt(now);
        attempt.setPaymentUrl(paymentProcessor.createPaymentUrl(bill, attempt, clientIp));

        bill.getAttempts().add(attempt);
        bill.setStatus(BillStatus.PENDING);
        bill.setUpdatedAt(now);
        try {
            bills.save(bill);
        } catch (OptimisticLockingFailureException ex) {
            Bill latest = findBill(tripId);
            PaymentAttempt concurrentRequest = latest.getAttempts().stream()
                    .filter(saved -> idempotencyKey.equals(saved.getIdempotencyKey()))
                    .findFirst()
                    .orElse(null);
            if (concurrentRequest != null) {
                return checkoutResponse(latest, concurrentRequest);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment changed concurrently; retry with the same Idempotency-Key", ex);
        }
        return checkoutResponse(bill, attempt);
    }

    public PaymentStatusResponse getStatus(String tripId, String riderId) {
        Bill bill = findBill(tripId);
        verifyRider(bill, riderId);
        PaymentAttempt latest = latestAttempt(bill);
        return new PaymentStatusResponse(
                bill.getTripId(),
                bill.getAmount(),
                bill.getCurrency(),
                bill.getStatus().name(),
                latest == null ? null : latest.getId(),
                latest == null ? null : latest.getStatus().name(),
                bill.getUpdatedAt());
    }

    public VnpayIpnResult processIpn(java.util.Map<String, String> parameters) {
        if (!paymentProcessor.verifyCallback(parameters)) {
            return new VnpayIpnResult("97", "Checksum failed");
        }
        if (!paymentProcessor.isMerchantCode(parameters.get("vnp_TmnCode"))) {
            return new VnpayIpnResult("99", "Merchant invalid");
        }

        String attemptId = parameters.get("vnp_TxnRef");
        String responseCode = parameters.get("vnp_ResponseCode");
        String transactionId = parameters.get("vnp_TransactionNo");
        if (!hasText(attemptId) || !hasText(responseCode)) {
            return new VnpayIpnResult("99", "Required fields missing");
        }

        Bill bill = bills.findByAttempts_Id(attemptId)
                .orElse(null);
        if (bill == null) {
            return new VnpayIpnResult("01", "Order not found");
        }
        PaymentAttempt attempt = bill.getAttempts().stream()
                .filter(candidate -> attemptId.equals(candidate.getId()))
                .findFirst()
                .orElse(null);
        if (attempt == null) {
            return new VnpayIpnResult("01", "Order not found");
        }

        long amount;
        try {
            amount = Long.parseLong(parameters.getOrDefault("vnp_Amount", ""));
            if (amount != Math.multiplyExact(bill.getAmount(), 100)) {
                return new VnpayIpnResult("04", "Amount invalid");
            }
        } catch (ArithmeticException | NumberFormatException ex) {
            return new VnpayIpnResult("04", "Amount invalid");
        }
        if (!bill.getCurrency().equals(parameters.get("vnp_CurrCode"))) {
            return new VnpayIpnResult("04", "Currency invalid");
        }
        if ("00".equals(responseCode) && !hasText(transactionId)) {
            return new VnpayIpnResult("99", "Transaction reference missing");
        }

        if ("00".equals(responseCode)) {
            return settleSuccess(bill, attempt, transactionId, responseCode);
        }
        return settleFailure(bill, attempt, transactionId, responseCode);
    }

    public PaymentReturnStatus getReturnStatus(java.util.Map<String, String> parameters) {
        if (!paymentProcessor.verifyCallback(parameters)) {
            return new PaymentReturnStatus(null, "invalid_signature");
        }
        if (!paymentProcessor.isMerchantCode(parameters.get("vnp_TmnCode"))) {
            return new PaymentReturnStatus(null, "invalid_merchant");
        }
        String attemptId = parameters.get("vnp_TxnRef");
        if (!hasText(attemptId)) {
            return new PaymentReturnStatus(null, "invalid_response");
        }
        Bill bill = bills.findByAttempts_Id(attemptId).orElse(null);
        if (bill == null) {
            return new PaymentReturnStatus(null, "not_found");
        }
        try {
            long amount = Long.parseLong(parameters.getOrDefault("vnp_Amount", ""));
            if (amount != Math.multiplyExact(bill.getAmount(), 100)
                    || !bill.getCurrency().equals(parameters.get("vnp_CurrCode"))) {
                return new PaymentReturnStatus(bill.getTripId(), "invalid_amount");
            }
        } catch (ArithmeticException | NumberFormatException ex) {
            return new PaymentReturnStatus(bill.getTripId(), "invalid_amount");
        }
        if (bill.getStatus() == BillStatus.SUCCEEDED) {
            return new PaymentReturnStatus(bill.getTripId(), "success");
        }
        if (bill.getStatus() == BillStatus.REQUIRES_REVIEW) {
            return new PaymentReturnStatus(bill.getTripId(), "review");
        }
        String status = "00".equals(parameters.get("vnp_ResponseCode")) ? "processing" : "failed";
        return new PaymentReturnStatus(bill.getTripId(), status);
    }

    private VnpayIpnResult settleSuccess(Bill bill, PaymentAttempt attempt, String transactionId, String responseCode) {
        if (alreadyRecorded(bill, transactionId)) {
            return new VnpayIpnResult("02", "Order already confirmed");
        }

        Instant now = Instant.now();
        boolean excessCapture = bill.getStatus() == BillStatus.SUCCEEDED
                || bill.getStatus() == BillStatus.REQUIRES_REVIEW;
        attempt.setStatus(PaymentAttemptStatus.SUCCEEDED);
        attempt.setGatewayTransactionId(transactionId);
        attempt.setResponseCode(responseCode);
        attempt.setUpdatedAt(now);
        appendLedger(bill, transactionId, excessCapture ? "EXCESS_CAPTURE" : "PAYMENT_CAPTURED",
                excessCapture ? "CUSTOMER_REFUND_PAYABLE" : "TRIP_FUNDS_PAYABLE", now);
        if (!excessCapture) {
            appendPaymentSettledEvent(bill, transactionId, now);
        }
        bill.setStatus(excessCapture ? BillStatus.REQUIRES_REVIEW : BillStatus.SUCCEEDED);
        bill.setUpdatedAt(now);

        try {
            bills.save(bill);
        } catch (OptimisticLockingFailureException ex) {
            Bill latest = bills.findByTripId(bill.getTripId()).orElse(null);
            if (latest != null && alreadyRecorded(latest, transactionId)) {
                return new VnpayIpnResult("02", "Order already confirmed");
            }
            throw ex;
        }
        return new VnpayIpnResult("00", excessCapture
                ? "Duplicate capture recorded for support review"
                : "Confirm Success");
    }

    private VnpayIpnResult settleFailure(Bill bill, PaymentAttempt attempt, String transactionId, String responseCode) {
        if (attempt.getStatus() == PaymentAttemptStatus.SUCCEEDED) {
            return new VnpayIpnResult("02", "Order already confirmed");
        }
        if (attempt.getStatus() == PaymentAttemptStatus.FAILED
                && responseCode.equals(attempt.getResponseCode())) {
            return new VnpayIpnResult("02", "Order already confirmed");
        }

        Instant now = Instant.now();
        attempt.setStatus(PaymentAttemptStatus.FAILED);
        attempt.setGatewayTransactionId(transactionId);
        attempt.setResponseCode(responseCode);
        attempt.setUpdatedAt(now);
        boolean anotherPending = bill.getAttempts().stream()
                .anyMatch(candidate -> candidate != attempt && candidate.getStatus() == PaymentAttemptStatus.PENDING);
        if (bill.getStatus() != BillStatus.SUCCEEDED && bill.getStatus() != BillStatus.REQUIRES_REVIEW) {
            bill.setStatus(anotherPending ? BillStatus.PENDING : BillStatus.FAILED);
        }
        bill.setUpdatedAt(now);
        bills.save(bill);
        return new VnpayIpnResult("00", "Confirm Success");
    }

    private void appendLedger(Bill bill, String reference, String type, String proceedsAccount, Instant now) {
        if (bill.getLedgerTransactions() == null) {
            bill.setLedgerTransactions(new ArrayList<>());
        }
        List<LedgerEntry> entries = List.of(
                new LedgerEntry("PAYMENT_GATEWAY_CLEARING", LedgerEntry.Direction.DEBIT, bill.getAmount()),
                new LedgerEntry(proceedsAccount, LedgerEntry.Direction.CREDIT, bill.getAmount()));
        LedgerTransaction journal = new LedgerTransaction(UUID.randomUUID().toString(), type, reference, entries, now);
        bill.getLedgerTransactions().add(journal);
    }

    private void appendPaymentSettledEvent(Bill bill, String transactionId, Instant now) {
        if (bill.getOutboxEvents() == null) {
            bill.setOutboxEvents(new ArrayList<>());
        }
        bill.getOutboxEvents().add(new PaymentOutboxEvent(
                UUID.randomUUID().toString(),
                "PaymentSettled",
                bill.getTripId(),
                bill.getRiderId(),
                bill.getAmount(),
                bill.getCurrency(),
                transactionId,
                now,
                null));
    }

    private boolean alreadyRecorded(Bill bill, String reference) {
        return bill.getLedgerTransactions() != null
                && bill.getLedgerTransactions().stream().anyMatch(journal -> reference.equals(journal.getReference()));
    }

    private Bill verifyDuplicateRideEvent(Bill bill, RideCompletedEvent event) {
        if (!bill.getRiderId().equals(event.riderId())
                || bill.getAmount() != event.amount()
                || !bill.getCurrency().equalsIgnoreCase(event.currency())
                || (bill.getPricingQuoteId() != null && event.quoteId() != null
                    && !bill.getPricingQuoteId().equals(event.quoteId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ride completion event conflicts with the existing bill");
        }
        return bill;
    }

    private Bill findBill(String tripId) {
        if (!hasText(tripId)) {
            throw badRequest("tripId is required");
        }
        return bills.findByTripId(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment for trip not found"));
    }

    private void verifyRider(Bill bill, String riderId) {
        if (!hasText(riderId) || !bill.getRiderId().equals(riderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rider cannot access this payment");
        }
    }

    private CheckoutResponse checkoutResponse(Bill bill, PaymentAttempt attempt) {
        return new CheckoutResponse(bill.getTripId(), attempt.getId(), bill.getAmount(),
                bill.getCurrency(), attempt.getStatus().name(), attempt.getPaymentUrl());
    }

    private PaymentAttempt latestAttempt(Bill bill) {
        List<PaymentAttempt> attempts = bill.getAttempts();
        return attempts == null || attempts.isEmpty() ? null : attempts.get(attempts.size() - 1);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public record VnpayIpnResult(String responseCode, String message) {
    }

    public record PaymentReturnStatus(String tripId, String status) {
    }
}
