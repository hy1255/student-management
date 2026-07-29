package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.entity.SysUser;
import com.example.backend.mapper.SysUserMapper;
import com.example.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> result = new HashMap<>();

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            result.put("code", 400);
            result.put("message", "用户名和密码不能为空");
            return result;
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return result;
        }

        if (user.getStatus() == 0) {
            result.put("code", 403);
            result.put("message", "账号已被禁用，请联系管理员");
            return result;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return result;
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());

        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("data", data);
        return result;
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            result.put("code", 401);
            result.put("message", "Token 无效或已过期");
            return result;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", username);
        data.put("role", role);

        result.put("code", 200);
        result.put("data", data);
        return result;
    }
}