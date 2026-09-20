package com.edu.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.analysis.entity.User;
import com.edu.analysis.mapper.UserMapper;
import com.edu.analysis.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!user.getPassword().equals(md5)) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(username);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        return data;
    }
}
