package com.stonereading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.AuthorProfile;
import java.util.Map;

public interface AuthorService extends IService<AuthorProfile> {

    AuthorProfile getByUserId(Long userId);

    Map<String, Object> applyAuthor(AuthorProfile profile);

    Map<String, Object> verifyAuthor(Long profileId, Integer status, String reason);

    Map<String, Object> getDashboard(Long userId);
}
