package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Student;
import com.example.backend.mapper.StudentMapper;
import com.example.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Page<Student> queryPage(Integer pageNum, Integer pageSize, String name) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Student::getName, name);
        }
        wrapper.orderByDesc(Student::getCreateTime);
        return studentMapper.selectPage(page, wrapper);
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(Student student) {
        if (student.getId() == null) {
            studentMapper.insert(student);
        } else {
            studentMapper.updateById(student);
        }
    }

    @Override
    public void deleteById(Long id) {
        studentMapper.deleteById(id);
    }
}