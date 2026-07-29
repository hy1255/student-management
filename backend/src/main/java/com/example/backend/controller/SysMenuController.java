package com.example.backend.controller;

import com.example.backend.entity.SysMenu;
import com.example.backend.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/tree")
    public List<SysMenu> getMenuTree() {
        return sysMenuService.getMenuTree();
    }

    @GetMapping("/{id}")
    public SysMenu getById(@PathVariable Long id) {
        return sysMenuService.getById(id);
    }

    @PostMapping
    public Map<String, String> saveOrUpdate(@RequestBody SysMenu menu) {
        sysMenuService.saveOrUpdate(menu);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "操作成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        if (sysMenuService.hasChildren(id)) {
            Map<String, String> result = new HashMap<>();
            result.put("code", "400");
            result.put("msg", "该菜单下有子菜单，无法删除");
            return result;
        }
        sysMenuService.deleteById(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "删除成功");
        return result;
    }
}