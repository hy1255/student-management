package com.example.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // 密钥（生产环境应从配置文件读取）
    private static final String SECRET = "studentManagementSecretKey2026!@#$%^&*()";

    // 过期时间：2小时
    private static final long EXPIRATION = 1000 * 60 * 60 * 2;

    // 生成签名密钥
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 生成 Token
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 验证 Token
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从 Token 获取用户名
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    // 从 Token 获取用户ID
    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    // 从 Token 获取角色
    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }
}