package com.mircroservice.payment_service.infrastructure.gateways;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;
import com.mircroservice.payment_service.infrastructure.config.VnpayProperties;

class VnpayGatewayTests {
    @Test
    void buildsSignedPaymentUrlAndRejectsTamperedCallback() {
        VnpayProperties properties = new VnpayProperties();
        properties.setTmnCode("TEST1234");
        properties.setHashSecret("test-secret");
        properties.setPaymentUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        properties.setReturnUrl("https://api.example.test/payments/vnpay/return");
        VnpayGateway gateway = new VnpayGateway(properties);

        Bill bill = new Bill();
        bill.setTripId("trip-123");
        bill.setAmount(42_000);
        bill.setCurrency("VND");
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setId("attempt-123");

        String url = gateway.buildPaymentUrl(bill, attempt, "::1");
        Map<String, String> parameters = Arrays.stream(URI.create(url).getRawQuery().split("&"))
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(
                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        pair -> URLDecoder.decode(pair.length > 1 ? pair[1] : "", StandardCharsets.UTF_8)));

        assertTrue(url.contains("vnp_Amount=4200000"));
        assertTrue(gateway.verifyCallback(parameters));

        parameters.put("vnp_Amount", "4200001");
        assertFalse(gateway.verifyCallback(parameters));
    }
}
