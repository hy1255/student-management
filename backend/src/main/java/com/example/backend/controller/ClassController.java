package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.ClassEntity;
import com.example.backend.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/page")
    public Page<ClassEntity> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return classService.queryPage(pageNum, pageSize, keyword);
    }

    @GetMapping("/{id}")
    public ClassEntity getById(@PathVariable Long id) {
        return classService.getById(id);
    }

    @PostMapping
    public Map<String, String> saveOrUpdate(@RequestBody ClassEntity classEntity) {
        classService.saveOrUpdate(classEntity);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "操作成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        classService.deleteById(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "删除成功");
        return result;
    }
}