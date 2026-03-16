package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    /**
     * 微信登录（模拟版）
     * TODO: 接入真实微信登录需要：
     * 1. 调用微信 API 用 code 换 openid
     * 2. 配置 sky.wechat.appid 和 sky.wechat.secret
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 模拟登录：直接把 code 当 openid 用
        // 真实登录需要调用微信 API: https://api.weixin.qq.com/sns/jscode2session
        String openid = userLoginDTO.getCode();

        if (openid == null || openid.isEmpty()) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 根据 openid 查询用户
        User user = userMapper.getByOpenid(openid);

        // 如果用户不存在，自动注册
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 返回用户信息（Controller 会生成 token）
        return user;
    }

}
