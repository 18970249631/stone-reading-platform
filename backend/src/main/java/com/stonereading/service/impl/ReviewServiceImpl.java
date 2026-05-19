package com.stonereading.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonereading.entity.ReviewRecord;
import com.stonereading.mapper.ReviewRecordMapper;
import com.stonereading.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewRecordMapper, ReviewRecord> implements ReviewService {

    @Override
    public ReviewRecord submitReview(String contentType, Long contentId, Long userId) {
        ReviewRecord record = new ReviewRecord();
        record.setContentType(contentType);
        record.setContentId(contentId);
        record.setUserId(userId);
        record.setReviewLevel(1);
        record.setCreatedAt(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    public Map<String, Object> aiReview(String contentType, Long contentId, String content) {
        Map<String, Object> result = new HashMap<>();

        if (content.contains("敏感词") || content.contains("违法") || content.contains("色情")) {
            result.put("result", "REJECT");
            result.put("reason", "检测到敏感内容");
            result.put("level", 1);
        } else if (content.length() > 10000) {
            result.put("result", "SUSPECT");
            result.put("reason", "内容过长，需要人工审核");
            result.put("level", 2);
        } else {
            result.put("result", "PASS");
            result.put("reason", "内容正常");
            result.put("level", 1);
        }

        return result;
    }

    @Override
    public ReviewRecord manualReview(Long recordId, Long reviewerId, String result, String reason) {
        ReviewRecord record = getById(recordId);
        if (record != null) {
            record.setReviewResult(result);
            record.setReviewReason(reason);
            record.setReviewerId(reviewerId);
            record.setReviewedAt(LocalDateTime.now());
            updateById(record);
        }
        return record;
    }

    @Override
    public void batchReview(Long[] recordIds, String result) {
        for (Long id : recordIds) {
            ReviewRecord record = getById(id);
            if (record != null) {
                record.setReviewResult(result);
                record.setReviewedAt(LocalDateTime.now());
                updateById(record);
            }
        }
    }
}
