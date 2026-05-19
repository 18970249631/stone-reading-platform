package com.stonereading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.MarketOrder;
import com.stonereading.entity.MarketProduct;

import java.util.Map;

public interface MarketService extends IService<MarketProduct> {

    IPage<MarketProduct> getProductList(int page, int size, String type);

    MarketProduct createProduct(MarketProduct product);

    Map<String, Object> buyProduct(Long productId, Long userId);

    IPage<MarketOrder> getOrderList(int page, int size, Long userId);
}
