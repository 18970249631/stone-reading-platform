package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(statsService.getDashboardStats());
    }

    @GetMapping("/users")
    public Result<Map<String, Object>> getUserStats() {
        return Result.success(statsService.getUserStats());
    }

    @GetMapping("/books")
    public Result<Map<String, Object>> getBookStats() {
        return Result.success(statsService.getBookStats());
    }

    @GetMapping("/revenue")
    public Result<Map<String, Object>> getRevenueStats(
            @RequestParam(defaultValue = "month") String period) {
        return Result.success(statsService.getRevenueStats(period));
    }

    @GetMapping("/review")
    public Result<Map<String, Object>> getReviewStats() {
        return Result.success(statsService.getReviewStats());
    }
}
