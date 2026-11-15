package com.mircroservice.payment_service.api.controllers;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.mircroservice.payment_service.api.PaymentRoute;
import com.mircroservice.payment_service.infrastructure.config.VnpayProperties;
import com.mircroservice.payment_service.application.use_cases.ProcessPayment;
import com.mircroservice.payment_service.application.use_cases.ProcessPayment.VnpayIpnResult;
import com.mircroservice.payment_service.application.use_cases.ProcessPayment.PaymentReturnStatus;

@RestController
@RequestMapping(PaymentRoute.PAYMENTS)
public class VnpayController {
    private final ProcessPayment paymentService;
    private final VnpayProperties properties;

    public VnpayController(ProcessPayment paymentService, VnpayProperties properties) {
        this.paymentService = paymentService;
        this.properties = properties;
    }

    @GetMapping(PaymentRoute.VNPAY_IPN)
    public Map<String, String> ipn(@RequestParam MultiValueMap<String, String> query) {
        VnpayIpnResult result = paymentService.processIpn(query.toSingleValueMap());
        return Map.of("RspCode", result.responseCode(), "Message", result.message());
    }

    @GetMapping(PaymentRoute.VNPAY_RETURN)
    public ResponseEntity<Void> paymentReturn(@RequestParam MultiValueMap<String, String> query) {
        Map<String, String> parameters = query.toSingleValueMap();
        PaymentReturnStatus result = paymentService.getReturnStatus(parameters);
        if (properties.getWebReturnUrl() == null || properties.getWebReturnUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Web return URL is not configured");
        }
        String redirectUrl = UriComponentsBuilder.fromUriString(properties.getWebReturnUrl())
                .queryParam("status", result.status())
                .queryParamIfPresent("tripId", java.util.Optional.ofNullable(result.tripId()))
                .queryParam("attemptId", parameters.getOrDefault("vnp_TxnRef", ""))
                .build()
                .encode()
                .toUriString();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }
}
