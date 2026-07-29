package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysRole;
import com.example.backend.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/page")
    public Page<SysRole> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return sysRoleService.queryPage(pageNum, pageSize, keyword);
    }

    @GetMapping("/{id}")
    public SysRole getById(@PathVariable Long id) {
        return sysRoleService.getById(id);
    }

    @PostMapping
    public Map<String, String> saveOrUpdate(@RequestBody SysRole role) {
        sysRoleService.saveOrUpdate(role);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "操作成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        try {
            sysRoleService.deleteById(id);
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

    // 分配菜单权限
    @PostMapping("/{id}/menus")
    public Map<String, String> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "权限分配成功");
        return result;
    }

    // 获取角色已分配的菜单ID
    @GetMapping("/{id}/menus")
    public List<Long> getMenuIds(@PathVariable Long id) {
        return sysRoleService.getMenuIdsByRoleId(id);
    }
}