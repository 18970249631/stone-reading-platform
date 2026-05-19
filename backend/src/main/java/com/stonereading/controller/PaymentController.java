package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.entity.User;
import com.stonereading.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public Result<Map<String, Object>> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        String memberType = request.get("memberType");
        Map<String, Object> result = paymentService.createOrder(user.getId(), memberType);
        return Result.success(result);
    }

    @PostMapping("/pay")
    public Result<Map<String, Object>> payOrder(@RequestBody Map<String, String> request) {
        String orderNo = request.get("orderNo");
        String payType = request.get("payType");
        Map<String, Object> result = paymentService.payOrder(orderNo, payType);
        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error(result.get("message").toString());
        }
    }

    @PostMapping("/callback/{payType}")
    public Result<Void> callback(@PathVariable String payType, @RequestParam Map<String, String> params) {
        paymentService.handlePaymentCallback(payType, params);
        return Result.success();
    }

    @PostMapping("/refund")
    public Result<Void> refundOrder(@RequestBody Map<String, String> request) {
        paymentService.refundOrder(request.get("orderNo"));
        return Result.success();
    }
}
