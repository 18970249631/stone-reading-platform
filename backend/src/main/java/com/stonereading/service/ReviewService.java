package com.stonereading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.ReviewRecord;

import java.util.Map;

public interface ReviewService extends IService<ReviewRecord> {

    ReviewRecord submitReview(String contentType, Long contentId, Long userId);

    Map<String, Object> aiReview(String contentType, Long contentId, String content);

    ReviewRecord manualReview(Long recordId, Long reviewerId, String result, String reason);

    void batchReview(Long[] recordIds, String result);
}
