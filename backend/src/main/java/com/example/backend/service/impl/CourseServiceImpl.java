package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Course;
import com.example.backend.mapper.CourseMapper;
import com.example.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service   // ⬅️ 这个注解必须有！
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Page<Course> queryPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getCourseName, keyword)
                    .or()
                    .like(Course::getCourseCode, keyword);
        }
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectPage(page, wrapper);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(Course course) {
        if (course.getId() == null) {
            courseMapper.insert(course);
        } else {
            courseMapper.updateById(course);
        }
    }

    @Override
    public void deleteById(Long id) {
        courseMapper.deleteById(id);
    }
}