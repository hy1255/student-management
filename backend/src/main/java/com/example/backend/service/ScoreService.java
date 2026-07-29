package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Score;

import java.util.List;
import java.util.Map;

public interface ScoreService {

    // 根据考试ID获取该考试所有学生的成绩（用于录入页面）
    List<Map<String, Object>> getScoresByExamId(Long examId);

    // 批量保存或更新成绩
    void batchSaveOrUpdate(List<Score> scoreList);

    // 分页查询成绩（带筛选）
    Page<Map<String, Object>> queryPage(Integer pageNum, Integer pageSize, Long studentId, Long courseId, Long examId);

    // 获取考试统计信息
    Map<String, Object> getExamStats(Long examId);

    // 删除成绩
    void deleteById(Long id);
}