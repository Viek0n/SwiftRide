package com.mircroservice.payment_service.api;

public final class PaymentRoute {
    public static final String PAYMENTS = "/payments";
    public static final String CHECKOUT = "/{tripId}/checkout";
    public static final String PAYMENT_BY_TRIP = "/{tripId}";
    public static final String VNPAY_IPN = "/vnpay/ipn";
    public static final String VNPAY_RETURN = "/vnpay/return";

    private PaymentRoute() {
    }
}
