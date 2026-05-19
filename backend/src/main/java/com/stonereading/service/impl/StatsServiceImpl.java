package com.stonereading.service.impl;

import com.stonereading.entity.Book;
import com.stonereading.entity.MarketOrder;
import com.stonereading.entity.ReviewRecord;
import com.stonereading.entity.User;
import com.stonereading.mapper.*;
import com.stonereading.service.StatsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;
    private final MarketOrderMapper orderMapper;
    private final ReviewRecordMapper reviewRecordMapper;

    public StatsServiceImpl(UserMapper userMapper, BookMapper bookMapper, 
                           ChapterMapper chapterMapper, MarketOrderMapper orderMapper,
                           ReviewRecordMapper reviewRecordMapper) {
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
        this.chapterMapper = chapterMapper;
        this.orderMapper = orderMapper;
        this.reviewRecordMapper = reviewRecordMapper;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> result = new HashMap<>();
        
        long totalUsers = userMapper.selectCount(null);
        long totalBooks = bookMapper.selectCount(null);
        long totalOrders = orderMapper.selectCount(null);
        long pendingReviews = reviewRecordMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReviewRecord>()
                .isNull(ReviewRecord::getReviewResult)
        );

        BigDecimal todayRevenue = getTodayRevenue();

        result.put("totalUsers", totalUsers);
        result.put("totalBooks", totalBooks);
        result.put("totalOrders", totalOrders);
        result.put("pendingReviews", pendingReviews);
        result.put("todayRevenue", todayRevenue);
        result.put("todayUsers", getTodayNewUsers());
        result.put("todayBooks", getTodayNewBooks());

        return result;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> result = new HashMap<>();
        
        List<User> users = userMapper.selectList(null);
        
        long vipUsers = users.stream().filter(u -> u.getVipLevel() > 0).count();
        long authorUsers = users.stream().filter(u -> "AUTHOR".equals(u.getRole())).count();

        result.put("total", users.size());
        result.put("vip", vipUsers);
        result.put("authors", authorUsers);
        result.put("normal", users.size() - vipUsers - authorUsers);

        return result;
    }

    @Override
    public Map<String, Object> getBookStats() {
        Map<String, Object> result = new HashMap<>();
        
        List<Book> books = bookMapper.selectList(null);
        
        long freeBooks = books.stream().filter(b -> b.getIsPaid() == 0).count();
        long paidBooks = books.stream().filter(b -> b.getIsPaid() == 1).count();
        long publishedBooks = books.stream().filter(b -> b.getStatus() == 1).count();

        int totalChapters = chapterMapper.selectCount(null);
        int totalWords = books.stream().mapToInt(Book::getWordCount).sum();

        result.put("total", books.size());
        result.put("free", freeBooks);
        result.put("paid", paidBooks);
        result.put("published", publishedBooks);
        result.put("totalChapters", totalChapters);
        result.put("totalWords", totalWords);

        return result;
    }

    @Override
    public Map<String, Object> getRevenueStats(String period) {
        Map<String, Object> result = new HashMap<>();
        
        List<MarketOrder> orders = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MarketOrder>()
                .eq(MarketOrder::getPayStatus, 1)
        );

        BigDecimal totalRevenue = orders.stream()
            .map(MarketOrder::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal platformFee = orders.stream()
            .map(MarketOrder::getPlatformFee)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        result.put("totalRevenue", totalRevenue);
        result.put("platformFee", platformFee);
        result.put("orderCount", orders.size());

        return result;
    }

    @Override
    public Map<String, Object> getReviewStats() {
        Map<String, Object> result = new HashMap<>();
        
        long total = reviewRecordMapper.selectCount(null);
        long pass = reviewRecordMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReviewRecord>()
                .eq(ReviewRecord::getReviewResult, "PASS")
        );
        long reject = reviewRecordMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReviewRecord>()
                .eq(ReviewRecord::getReviewResult, "REJECT")
        );
        long pending = reviewRecordMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReviewRecord>()
                .isNull(ReviewRecord::getReviewResult)
        );

        result.put("total", total);
        result.put("pass", pass);
        result.put("reject", reject);
        result.put("pending", pending);

        return result;
    }

    private BigDecimal getTodayRevenue() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<MarketOrder> orders = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MarketOrder>()
                .eq(MarketOrder::getPayStatus, 1)
                .between(MarketOrder::getPayTime, start, end)
        );

        return orders.stream()
            .map(MarketOrder::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long getTodayNewUsers() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return userMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .between(User::getCreatedAt, start, end)
        );
    }

    private long getTodayNewBooks() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return bookMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Book>()
                .between(Book::getCreatedAt, start, end)
        );
    }
}
