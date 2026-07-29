package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysUser;
import com.example.backend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    public Page<SysUser> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return sysUserService.queryPage(pageNum, pageSize, keyword);
    }

    @GetMapping("/{id}")
    public SysUser getById(@PathVariable Long id) {
        return sysUserService.getById(id);
    }

    @PostMapping
    public Map<String, String> saveOrUpdate(@RequestBody SysUser user) {
        try {
            sysUserService.saveOrUpdate(user);
            Map<String, String> result = new HashMap<>();
            result.put("code", "200");
            result.put("msg", "操作成功");
            return result;
        } catch (Exception e) {
            Map<String, String> result = new HashMap<>();
            result.put("code", "400");
            result.put("msg", e.getMessage());
            return result;
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        try {
            sysUserService.deleteById(id);
            Map<String, String> result = new HashMap<>();
            result.put("code", "200");
            result.put("msg", "删除成功");
            return result;
        } catch (RuntimeException e) {
            Map<String, String> result = new HashMap<>();
            result.put("code", "400");
            result.put("msg", e.getMessage());
            return result;
        }
    }

    @PutMapping("/{id}/reset-password")
    public Map<String, String> resetPassword(@PathVariable Long id) {
        try {
            sysUserService.resetPassword(id);
            Map<String, String> result = new HashMap<>();
            result.put("code", "200");
            result.put("msg", "密码已重置为 123456");
            return result;
        } catch (RuntimeException e) {
            Map<String, String> result = new HashMap<>();
            result.put("code", "400");
            result.put("msg", e.getMessage());
            return result;
        }
    }
}