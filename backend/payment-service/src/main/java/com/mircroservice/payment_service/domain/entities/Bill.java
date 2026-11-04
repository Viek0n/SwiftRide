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
    private String trip_id;
    private Double amount;
    private String currency;
    private PaymentMethod method;
    private BillStatus status;
    private String gateway_transation_id;
    private String gateway_order_id;
    private Instant created_at;
    private Instant updated_at;
}
