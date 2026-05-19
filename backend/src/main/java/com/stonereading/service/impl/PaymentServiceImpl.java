package com.stonereading.service.impl;

import com.stonereading.entity.MembershipOrder;
import com.stonereading.entity.User;
import com.stonereading.mapper.MembershipOrderMapper;
import com.stonereading.mapper.UserMapper;
import com.stonereading.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final MembershipOrderMapper orderMapper;
    private final UserMapper userMapper;

    public PaymentServiceImpl(MembershipOrderMapper orderMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Map<String, Object> createOrder(Long userId, String memberType) {
        Map<String, Object> result = new HashMap<>();
        String orderNo = generateOrderNo();

        BigDecimal amount = "BASIC".equals(memberType) ? new BigDecimal("19.00") : new BigDecimal("30.00");

        MembershipOrder order = new MembershipOrder();
        order.setUserId(userId);
        order.setMemberType(memberType);
        order.setAmount(amount);
        order.setPayStatus(0);
        order.setOrderNo(orderNo);
        orderMapper.insert(order);

        result.put("orderNo", orderNo);
        result.put("amount", amount);
        result.put("memberType", memberType);
        result.put("createTime", LocalDateTime.now());

        return result;
    }

    @Override
    public Map<String, Object> payOrder(String orderNo, String payType) {
        Map<String, Object> result = new HashMap<>();

        MembershipOrder order = orderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MembershipOrder>()
                .eq(MembershipOrder::getOrderNo, orderNo)
        );

        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (order.getPayStatus() == 1) {
            result.put("success", false);
            result.put("message", "订单已支付");
            return result;
        }

        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());

        int vipLevel = "BASIC".equals(order.getMemberType()) ? 1 : 2;
        LocalDateTime expireDate = LocalDateTime.now().plusMonths(1);

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            user.setVipLevel(vipLevel);
            user.setVipExpireDate(expireDate);
            userMapper.updateById(user);
        }

        order.setExpireDate(expireDate);
        orderMapper.updateById(order);

        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderNo", orderNo);
        result.put("vipLevel", vipLevel);
        result.put("expireDate", expireDate);

        return result;
    }

    @Override
    public void handlePaymentCallback(String payType, Map<String, String> params) {
        String orderNo = params.get("orderNo");
        String status = params.get("status");

        if ("SUCCESS".equals(status)) {
            payOrder(orderNo, payType);
        }
    }

    @Override
    public void refundOrder(String orderNo) {
        MembershipOrder order = orderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MembershipOrder>()
                .eq(MembershipOrder::getOrderNo, orderNo)
        );

        if (order != null && order.getPayStatus() == 1) {
            order.setPayStatus(2);
            orderMapper.updateById(order);

            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                user.setVipLevel(0);
                user.setVipExpireDate(null);
                userMapper.updateById(user);
            }
        }
    }

    private String generateOrderNo() {
        return "MO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
