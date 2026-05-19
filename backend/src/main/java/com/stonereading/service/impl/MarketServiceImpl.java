package com.stonereading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonereading.entity.MarketOrder;
import com.stonereading.entity.MarketProduct;
import com.stonereading.mapper.MarketOrderMapper;
import com.stonereading.mapper.MarketProductMapper;
import com.stonereading.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class MarketServiceImpl extends ServiceImpl<MarketProductMapper, MarketProduct> implements MarketService {

    @Autowired
    private MarketOrderMapper orderMapper;

    @Override
    public IPage<MarketProduct> getProductList(int page, int size, String type) {
        Page<MarketProduct> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<MarketProduct> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(MarketProduct::getProductType, type);
        }
        wrapper.eq(MarketProduct::getStatus, 1);
        wrapper.orderByDesc(MarketProduct::getCreatedAt);
        return page(pageObj, wrapper);
    }

    @Override
    public MarketProduct createProduct(MarketProduct product) {
        product.setStatus(0);
        product.setViewCount(0);
        product.setSalesCount(0);
        save(product);
        return product;
    }

    @Override
    @Transactional
    public Map<String, Object> buyProduct(Long productId, Long userId) {
        MarketProduct product = getById(productId);
        if (product == null || product.getStatus() != 1) {
            return Map.of("success", false, "message", "商品不存在或已下架");
        }

        BigDecimal fee = product.getPrice().multiply(new BigDecimal("0.1"));
        BigDecimal sellerRevenue = product.getPrice().subtract(fee);

        MarketOrder order = new MarketOrder();
        order.setOrderNo("MK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setProductId(productId);
        order.setBuyerId(userId);
        order.setSellerId(product.getSellerId());
        order.setAmount(product.getPrice());
        order.setSellerRevenue(sellerRevenue);
        order.setPlatformFee(fee);
        order.setTransactionType(product.getTransactionType());
        order.setPayStatus(0);
        order.setStatus(0);
        orderMapper.insert(order);

        return Map.of(
            "success", true,
            "orderNo", order.getOrderNo(),
            "amount", product.getPrice(),
            "product", product
        );
    }

    @Override
    public IPage<MarketOrder> getOrderList(int page, int size, Long userId) {
        Page<MarketOrder> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<MarketOrder> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.and(w -> w.eq(MarketOrder::getBuyerId, userId).or().eq(MarketOrder::getSellerId, userId));
        }
        wrapper.orderByDesc(MarketOrder::getCreatedAt);
        return orderMapper.selectPage(pageObj, wrapper);
    }
}
