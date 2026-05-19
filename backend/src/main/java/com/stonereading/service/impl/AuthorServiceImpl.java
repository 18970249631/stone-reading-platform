package com.stonereading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonereading.entity.AuthorProfile;
import com.stonereading.mapper.AuthorProfileMapper;
import com.stonereading.service.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorProfileMapper, AuthorProfile> implements AuthorService {

    @Override
    public AuthorProfile getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<AuthorProfile>().eq(AuthorProfile::getUserId, userId));
    }

    @Override
    @Transactional
    public Map<String, Object> applyAuthor(AuthorProfile profile) {
        AuthorProfile existing = getByUserId(profile.getUserId());
        if (existing != null) {
            existing.setPenName(profile.getPenName());
            existing.setRealName(profile.getRealName());
            existing.setIdCard(profile.getIdCard());
            existing.setBio(profile.getBio());
            existing.setSkills(profile.getSkills());
            existing.setVerifyStatus(0);
            updateById(existing);
            return Map.of("success", true, "message", "已重新提交申请，等待审核", "profile", existing);
        }

        profile.setVerifyStatus(0);
        profile.setBookCount(0);
        profile.setTotalWordCount(0);
        profile.setTotalEarnings(new java.math.BigDecimal("0.00"));
        save(profile);

        return Map.of("success", true, "message", "申请已提交，等待审核", "profile", profile);
    }

    @Override
    @Transactional
    public Map<String, Object> verifyAuthor(Long profileId, Integer status, String reason) {
        AuthorProfile profile = getById(profileId);
        if (profile == null) {
            return Map.of("success", false, "message", "记录不存在");
        }

        profile.setVerifyStatus(status);
        profile.setVerifyReason(reason);
        profile.setVerifiedAt(LocalDateTime.now());
        updateById(profile);

        return Map.of("success", true, "message", "审核完成");
    }

    @Override
    public Map<String, Object> getDashboard(Long userId) {
        AuthorProfile profile = getByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("profile", profile);
        result.put("stats", Map.of(
            "bookCount", profile != null ? profile.getBookCount() : 0,
            "totalWordCount", profile != null ? profile.getTotalWordCount() : 0,
            "totalEarnings", profile != null ? profile.getTotalEarnings() : "0.00",
            "todayReads", 1234
        ));
        return result;
    }
}
