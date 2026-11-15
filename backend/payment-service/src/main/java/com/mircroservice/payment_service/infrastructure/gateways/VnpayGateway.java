package com.mircroservice.payment_service.infrastructure.gateways;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentAttempt;
import com.mircroservice.payment_service.infrastructure.config.VnpayProperties;

@Component
public class VnpayGateway {
    private static final DateTimeFormatter VNPAY_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final VnpayProperties properties;

    public VnpayGateway(VnpayProperties properties) {
        this.properties = properties;
    }

    public boolean isConfigured() {
        return hasText(properties.getTmnCode()) && hasText(properties.getHashSecret())
                && !"YOUR_TMN_CODE".equals(properties.getTmnCode())
                && !"YOUR_HASH_SECRET".equals(properties.getHashSecret())
                && hasText(properties.getPaymentUrl()) && hasText(properties.getReturnUrl());
    }

    public boolean isMerchantCode(String code) {
        return hasText(code) && code.equals(properties.getTmnCode());
    }

    public String buildPaymentUrl(Bill bill, PaymentAttempt attempt, String clientIp) {
        if (!isConfigured()) {
            throw new IllegalStateException("VNPay credentials and callback URL are not configured");
        }
        if (bill.getAmount() < 1 || bill.getAmount() > Long.MAX_VALUE / 100) {
            throw new IllegalArgumentException("Trip amount is outside VNPay's supported range");
        }

        ZonedDateTime now = ZonedDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        Map<String, String> parameters = new TreeMap<>();
        parameters.put("vnp_Version", "2.1.0");
        parameters.put("vnp_Command", "pay");
        parameters.put("vnp_TmnCode", properties.getTmnCode());
        parameters.put("vnp_Amount", Long.toString(Math.multiplyExact(bill.getAmount(), 100)));
        parameters.put("vnp_CurrCode", bill.getCurrency());
        parameters.put("vnp_TxnRef", attempt.getId());
        parameters.put("vnp_OrderInfo", "SwiftRide trip " + bill.getTripId());
        parameters.put("vnp_OrderType", "other");
        parameters.put("vnp_Locale", "vn");
        parameters.put("vnp_ReturnUrl", properties.getReturnUrl());
        parameters.put("vnp_IpAddr", normalizeIp(clientIp));
        parameters.put("vnp_CreateDate", VNPAY_DATE.format(now));

        String signature = sign(parameters);
        String query = encodeParameters(parameters) + "&vnp_SecureHash=" + signature;
        return properties.getPaymentUrl() + (properties.getPaymentUrl().contains("?") ? "&" : "?") + query;
    }

    public boolean verifyCallback(Map<String, String> parameters) {
        if (parameters == null || !hasText(properties.getHashSecret())) {
            return false;
        }
        String receivedHash = parameters.get("vnp_SecureHash");
        if (!hasText(receivedHash) || !receivedHash.matches("(?i)[0-9a-f]{128}")) {
            return false;
        }
        Map<String, String> signedParameters = new TreeMap<>(parameters);
        signedParameters.remove("vnp_SecureHash");
        signedParameters.remove("vnp_SecureHashType");
        byte[] expected;
        byte[] received;
        try {
            expected = HexFormat.of().parseHex(sign(signedParameters));
            received = HexFormat.of().parseHex(receivedHash);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return MessageDigest.isEqual(expected, received);
    }

    private String sign(Map<String, String> parameters) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(properties.getHashSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return HexFormat.of().formatHex(hmac.doFinal(encodeParameters(parameters).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not sign VNPay request", ex);
        }
    }

    private String encodeParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String normalizeIp(String ip) {
        if (!hasText(ip)) {
            return "127.0.0.1";
        }
        String normalized = ip.split(",", 2)[0].trim();
        if (normalized.startsWith("::ffff:")) {
            normalized = normalized.substring(7);
        } else if ("::1".equals(normalized)) {
            normalized = "127.0.0.1";
        }
        return normalized.length() > 45 ? normalized.substring(0, 45) : normalized;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
