package com.mircroservice.payment_service.application.use_cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mircroservice.payment_service.application.dto.RideCompletedEvent;
import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;
import com.mircroservice.payment_service.domain.interfaces.PaymentProcessor;
import com.mircroservice.payment_service.domain.repositories.BillRepository;

class ProcessPaymentTests {
    @Test
    void duplicateRideEventAndCheckoutRequestDoNotCreateDuplicateRecords() {
        InMemoryBillRepository repository = new InMemoryBillRepository();
        FakePaymentProcessor processor = new FakePaymentProcessor();
        ProcessPayment service = new ProcessPayment(repository, processor);
        RideCompletedEvent event = new RideCompletedEvent("trip-1", "rider-1", 25_000, "VND", "quote-1", null);

        service.registerCompletedRide(event);
        service.registerCompletedRide(event);
        var first = service.createCheckout("trip-1", "rider-1", "idempotency-001", "127.0.0.1");
        var repeated = service.createCheckout("trip-1", "rider-1", "idempotency-001", "127.0.0.1");

        assertEquals(first.attemptId(), repeated.attemptId());
        assertEquals(1, repository.findByTripId("trip-1").orElseThrow().getAttempts().size());
        assertEquals(1, processor.createUrlCalls);
    }

    @Test
    void successfulIpnCreatesOneBalancedJournalEvenWhenDeliveredTwice() {
        InMemoryBillRepository repository = new InMemoryBillRepository();
        ProcessPayment service = new ProcessPayment(repository, new FakePaymentProcessor());
        service.registerCompletedRide(new RideCompletedEvent("trip-2", "rider-2", 25_000, "VND", "quote-2", null));
        var checkout = service.createCheckout("trip-2", "rider-2", "idempotency-002", "127.0.0.1");
        Map<String, String> ipn = validIpn(checkout.attemptId(), "2500000");
        ipn.put("vnp_ResponseCode", "00");
        ipn.put("vnp_TransactionNo", "gateway-transaction-1");

        var firstResult = service.processIpn(ipn);
        var duplicateResult = service.processIpn(ipn);
        Bill bill = repository.findByTripId("trip-2").orElseThrow();

        assertEquals("00", firstResult.responseCode());
        assertEquals("02", duplicateResult.responseCode());
        assertEquals(1, bill.getLedgerTransactions().size());
        assertEquals(1, bill.getOutboxEvents().size());
        assertEquals(bill.getLedgerTransactions().get(0).getEntries().get(0).getAmount(),
                bill.getLedgerTransactions().get(0).getEntries().get(1).getAmount());
        assertEquals("SUCCEEDED", bill.getStatus().name());
    }

    @Test
    void riderCanCreateANewAttemptAfterAConfirmedFailure() {
        InMemoryBillRepository repository = new InMemoryBillRepository();
        ProcessPayment service = new ProcessPayment(repository, new FakePaymentProcessor());
        service.registerCompletedRide(new RideCompletedEvent("trip-3", "rider-3", 25_000, "VND", "quote-3", null));
        var firstCheckout = service.createCheckout("trip-3", "rider-3", "idempotency-003", "127.0.0.1");
        Map<String, String> ipn = validIpn(firstCheckout.attemptId(), "2500000");
        ipn.put("vnp_ResponseCode", "24");

        assertEquals("00", service.processIpn(ipn).responseCode());
        var secondCheckout = service.createCheckout("trip-3", "rider-3", "idempotency-004", "127.0.0.1");

        assertNotEquals(firstCheckout.attemptId(), secondCheckout.attemptId());
        assertEquals(2, repository.findByTripId("trip-3").orElseThrow().getAttempts().size());
    }

    private Map<String, String> validIpn(String attemptId, String amount) {
        Map<String, String> ipn = new HashMap<>();
        ipn.put("signature", "valid");
        ipn.put("vnp_TmnCode", "merchant");
        ipn.put("vnp_TxnRef", attemptId);
        ipn.put("vnp_Amount", amount);
        ipn.put("vnp_CurrCode", "VND");
        return ipn;
    }

    private static final class InMemoryBillRepository implements BillRepository {
        private final Map<String, Bill> byTrip = new HashMap<>();

        @Override
        public Optional<Bill> findByTripId(String tripId) {
            return Optional.ofNullable(byTrip.get(tripId));
        }

        @Override
        public Optional<Bill> findByAttempts_Id(String attemptId) {
            return byTrip.values().stream()
                    .filter(bill -> bill.getAttempts().stream().anyMatch(attempt -> attemptId.equals(attempt.getId())))
                    .findFirst();
        }

        @Override
        public List<Bill> findTop50ByOutboxEvents_PublishedAtIsNull() {
            return byTrip.values().stream()
                    .filter(bill -> bill.getOutboxEvents().stream().anyMatch(event -> event.getPublishedAt() == null))
                    .limit(50)
                    .toList();
        }

        @Override
        public Bill save(Bill bill) {
            byTrip.put(bill.getTripId(), bill);
            return bill;
        }
    }

    private static final class FakePaymentProcessor implements PaymentProcessor {
        private int createUrlCalls;

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean isMerchantCode(String code) {
            return "merchant".equals(code);
        }

        @Override
        public String createPaymentUrl(Bill bill, PaymentAttempt attempt, String clientIp) {
            createUrlCalls++;
            return "https://gateway.test/" + attempt.getId();
        }

        @Override
        public boolean verifyCallback(Map<String, String> parameters) {
            return "valid".equals(parameters.get("signature"));
        }
    }
}
