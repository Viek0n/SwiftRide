package com.mircroservice.payment_service.api;

public final class PaymentRoute {
    public static final String PAYMENTS = "api/payments"; // Lấy danh sách thanh toán
    public static final String CHECKOUT = "/{tripId}/checkout"; // Tạo yêu cầu thanh toán cho hóa đơn đã tồn tại, tìm kiếm hóa đơn theo tripId
    public static final String PAYMENT_BY_TRIP = "/{tripId}"; // Lấy thông tin hóa đơn theo tripId
    public static final String VNPAY_IPN = "/vnpay/ipn"; // Xử lý thông báo thanh toán từ VNPAY
    public static final String VNPAY_RETURN = "/vnpay/return"; // Xử lý trả về từ VNPAY sau khi thanh toán thành công hoặc thất bại

    private PaymentRoute() {
    }
}
