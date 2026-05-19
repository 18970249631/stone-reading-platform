package com.stonereading.service;

import java.util.Map;

public interface StatsService {

    Map<String, Object> getDashboardStats();

    Map<String, Object> getUserStats();

    Map<String, Object> getBookStats();

    Map<String, Object> getRevenueStats(String period);

    Map<String, Object> getReviewStats();
}
