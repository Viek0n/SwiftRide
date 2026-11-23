package com.mircroservice.payment_service.infrastructure.payment_processor;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;
import com.mircroservice.payment_service.domain.interfaces.PaymentProcessor;
import com.mircroservice.payment_service.infrastructure.gateways.VnpayGateway;

@Component
// Lớp này triển khai giao diện PaymentProcessor để xử lý thanh toán thông qua VNPAY
public class VnpayProcessor implements PaymentProcessor {
    private final VnpayGateway gateway;
    // Constructor nhận một đối tượng VnpayGateway để giao tiếp với cổng thanh toán VNPAY
    public VnpayProcessor(VnpayGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    // Kiểm tra xem cổng thanh toán VNPAY có được cấu hình và sẵn sàng sử dụng hay không
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
