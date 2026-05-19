package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.entity.ReviewRecord;
import com.stonereading.entity.User;
import com.stonereading.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/submit")
    public Result<ReviewRecord> submitReview(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> request) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        String contentType = request.get("contentType").toString();
        Long contentId = Long.parseLong(request.get("contentId").toString());
        ReviewRecord record = reviewService.submitReview(contentType, contentId, user.getId());
        return Result.success(record);
    }

    @PostMapping("/ai")
    public Result<Map<String, Object>> aiReview(@RequestBody Map<String, Object> request) {
        String contentType = request.get("contentType").toString();
        Long contentId = Long.parseLong(request.get("contentId").toString());
        String content = request.get("content").toString();
        Map<String, Object> result = reviewService.aiReview(contentType, contentId, content);
        return Result.success(result);
    }

    @PostMapping("/manual/{recordId}")
    public Result<ReviewRecord> manualReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long recordId,
            @RequestBody Map<String, String> request) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        ReviewRecord record = reviewService.manualReview(
                recordId,
                user.getId(),
                request.get("result"),
                request.get("reason")
        );
        return Result.success(record);
    }

    @PostMapping("/batch")
    public Result<Void> batchReview(@RequestBody Map<String, Object> request) {
        Long[] recordIds = ((java.util.List<Long>) request.get("recordIds")).stream()
                .map(Long::longValue)
                .toArray(Long[]::new);
        String result = request.get("result").toString();
        reviewService.batchReview(recordIds, result);
        return Result.success();
    }
}
