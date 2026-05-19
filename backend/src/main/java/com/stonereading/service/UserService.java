package com.stonereading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stonereading.entity.User;

public interface UserService extends IService<User> {

    User register(String username, String password, String email, String phone);

    String login(String username, String password);

    User getUserInfo(Long userId);

    void updateUserInfo(User user);
}