package com.mircroservice.payment_service.domain.interfaces;

import java.util.Map;

import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;

public interface PaymentProcessor {
    boolean isAvailable();

    boolean isMerchantCode(String code);

    String createPaymentUrl(Bill bill, PaymentAttempt attempt, String clientIp);

    boolean verifyCallback(Map<String, String> parameters);
}
