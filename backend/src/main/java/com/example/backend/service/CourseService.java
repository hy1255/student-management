package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Course;

public interface CourseService {

    Page<Course> queryPage(Integer pageNum, Integer pageSize, String keyword);

    Course getById(Long id);

    void saveOrUpdate(Course course);

    void deleteById(Long id);
}