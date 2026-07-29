package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Exam;

public interface ExamService {

    Page<Exam> queryPage(Integer pageNum, Integer pageSize, String keyword, Long courseId);

    Exam getById(Long id);

    void saveOrUpdate(Exam exam);

    void deleteById(Long id);
}