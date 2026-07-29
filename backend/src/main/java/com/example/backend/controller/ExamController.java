package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Exam;
import com.example.backend.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/page")
    public Map<String, Object> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId) {

        Page<Exam> page = examService.queryPage(pageNum, pageSize, keyword, courseId);

        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("records", page.getRecords());
        return result;
    }

    @GetMapping("/{id}")
    public Exam getById(@PathVariable Long id) {
        return examService.getById(id);
    }

    @PostMapping
    public Map<String, String> saveOrUpdate(@RequestBody Exam exam) {
        examService.saveOrUpdate(exam);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "操作成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        examService.deleteById(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "删除成功");
        return result;
    }
}