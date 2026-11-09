package com.mircroservice.payment_service.domain.entities;

import java.time.Instant;

import com.mircroservice.payment_service.domain.enums.BillStatus;
import com.mircroservice.payment_service.domain.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Bill {
    private String id;
    private String tripId;
    private Double amount;
    private String currency;
    private PaymentMethod method;
    private BillStatus status;
    private String gatewayTransationId;
    private String gatewayOrderId;
    private Instant createdAt;
}
