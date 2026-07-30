package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.ClassEntity;

public interface ClassService {

    Page<ClassEntity> queryPage(Integer pageNum, Integer pageSize, String keyword);

    ClassEntity getById(Long id);

    void saveOrUpdate(ClassEntity classEntity);

    void deleteById(Long id);
}