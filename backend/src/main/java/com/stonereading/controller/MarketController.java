package com.stonereading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stonereading.common.Result;
import com.stonereading.entity.MarketOrder;
import com.stonereading.entity.MarketProduct;
import com.stonereading.entity.User;
import com.stonereading.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @GetMapping("/products")
    public Result<IPage<MarketProduct>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type
    ) {
        return Result.success(marketService.getProductList(page, size, type));
    }

    @GetMapping("/products/{id}")
    public Result<MarketProduct> getProduct(@PathVariable Long id) {
        MarketProduct product = marketService.getById(id);
        if (product != null) {
            product.setViewCount(product.getViewCount() + 1);
            marketService.updateById(product);
        }
        return Result.success(product);
    }

    @PostMapping("/products")
    public Result<MarketProduct> createProduct(
            @AuthenticationPrincipal User user,
            @RequestBody MarketProduct product
    ) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        product.setSellerId(user.getId());
        product.setSellerName(user.getNickname());
        return Result.success(marketService.createProduct(product));
    }

    @PostMapping("/products/{id}/buy")
    public Result<Map<String, Object>> buyProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(marketService.buyProduct(id, user.getId()));
    }

    @GetMapping("/orders")
    public Result<IPage<MarketOrder>> getOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(marketService.getOrderList(page, size, user.getId()));
    }
}
