package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.entity.AuthorProfile;
import com.stonereading.entity.User;
import com.stonereading.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/profile")
    public Result<AuthorProfile> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        AuthorProfile profile = authorService.getByUserId(user.getId());
        return Result.success(profile);
    }

    @PostMapping("/apply")
    public Result<Map<String, Object>> applyAuthor(
            @AuthenticationPrincipal User user,
            @RequestBody AuthorProfile profile
    ) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        profile.setUserId(user.getId());
        return Result.success(authorService.applyAuthor(profile));
    }

    @PostMapping("/verify/{profileId}")
    public Result<Map<String, Object>> verifyAuthor(
            @PathVariable Long profileId,
            @RequestBody Map<String, Object> params
    ) {
        Integer status = (Integer) params.get("status");
        String reason = (String) params.get("reason");
        return Result.success(authorService.verifyAuthor(profileId, status, reason));
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(authorService.getDashboard(user.getId()));
    }
}
