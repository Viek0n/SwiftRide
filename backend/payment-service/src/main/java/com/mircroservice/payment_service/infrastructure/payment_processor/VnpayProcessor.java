package com.mircroservice.payment_service.infrastructure.payment_processor;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;
import com.mircroservice.payment_service.domain.interfaces.PaymentProcessor;
import com.mircroservice.payment_service.infrastructure.gateways.VnpayGateway;

@Component
public class VnpayProcessor implements PaymentProcessor {
    private final VnpayGateway gateway;

    public VnpayProcessor(VnpayGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public boolean isAvailable() {
        return gateway.isConfigured();
    }

    @Override
    public boolean isMerchantCode(String code) {
        return gateway.isMerchantCode(code);
    }

    @Override
    public String createPaymentUrl(Bill bill, PaymentAttempt attempt, String clientIp) {
        return gateway.buildPaymentUrl(bill, attempt, clientIp);
    }

    @Override
    public boolean verifyCallback(Map<String, String> parameters) {
        return gateway.verifyCallback(parameters);
    }
}
