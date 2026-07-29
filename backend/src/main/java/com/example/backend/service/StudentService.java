package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Student;

public interface StudentService {

    Page<Student> queryPage(Integer pageNum, Integer pageSize, String name);

    Student getById(Long id);

    void saveOrUpdate(Student student);

    void deleteById(Long id);
}