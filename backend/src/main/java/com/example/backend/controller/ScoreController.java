package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Score;
import com.example.backend.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    // 获取某考试所有学生的成绩（录入页面使用）
    @GetMapping("/exam/{examId}")
    public List<Map<String, Object>> getScoresByExam(@PathVariable Long examId) {
        return scoreService.getScoresByExamId(examId);
    }

    // 批量保存成绩
    @PostMapping("/batch")
    public Map<String, String> batchSave(@RequestBody List<Score> scoreList) {
        scoreService.batchSaveOrUpdate(scoreList);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "保存成功");
        return result;
    }

    // 分页查询成绩列表
    @GetMapping("/page")
    public Page<Map<String, Object>> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long examId) {
        return scoreService.queryPage(pageNum, pageSize, studentId, courseId, examId);
    }

    // 获取考试统计
    @GetMapping("/stats/{examId}")
    public Map<String, Object> getExamStats(@PathVariable Long examId) {
        return scoreService.getExamStats(examId);
    }

    // 删除成绩
    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        scoreService.deleteById(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "删除成功");
        return result;
    }
}