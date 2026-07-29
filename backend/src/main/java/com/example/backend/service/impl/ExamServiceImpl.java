package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Exam;
import com.example.backend.mapper.ExamMapper;
import com.example.backend.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service   // ⬅️ 必须有！
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public Page<Exam> queryPage(Integer pageNum, Integer pageSize, String keyword, Long courseId) {
        Page<Exam> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Exam::getExamName, keyword);
        }
        if (courseId != null) {
            wrapper.eq(Exam::getCourseId, courseId);
        }
        wrapper.orderByDesc(Exam::getExamDate);
        return examMapper.selectPage(page, wrapper);
    }

    @Override
    public Exam getById(Long id) {
        return examMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(Exam exam) {
        LocalDate today = LocalDate.now();
        if (exam.getExamDate().isAfter(today)) {
            exam.setStatus("PENDING");
        } else if (exam.getExamDate().isEqual(today)) {
            exam.setStatus("ONGOING");
        } else {
            exam.setStatus("FINISHED");
        }
        if (exam.getId() == null) {
            examMapper.insert(exam);
        } else {
            examMapper.updateById(exam);
        }
    }

    @Override
    public void deleteById(Long id) {
        examMapper.deleteById(id);
    }
}