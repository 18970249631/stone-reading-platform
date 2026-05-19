package com.stonereading.controller;

import com.stonereading.common.Result;
import com.stonereading.dto.LoginRequest;
import com.stonereading.dto.RegisterRequest;
import com.stonereading.entity.User;
import com.stonereading.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getPhone()
        );
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "未登录");
        }
        User fullUser = userService.getUserInfo(user.getId());
        return Result.success(fullUser);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@AuthenticationPrincipal User user, @RequestBody User request) {
        if (user == null) {
            return Result.error(401, "未登录");
        }
        request.setId(user.getId());
        userService.updateUserInfo(request);
        return Result.success();
    }
}