package com.stonereading.service;

import java.util.Map;

public interface PaymentService {

    Map<String, Object> createOrder(Long userId, String memberType);

    Map<String, Object> payOrder(String orderNo, String payType);

    void handlePaymentCallback(String payType, Map<String, String> params);

    void refundOrder(String orderNo);
}
