package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.ClassEntity;
import com.example.backend.mapper.ClassMapper;
import com.example.backend.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public Page<ClassEntity> queryPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<ClassEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClassEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ClassEntity::getClassName, keyword)
                    .or()
                    .like(ClassEntity::getClassCode, keyword);
        }
        wrapper.orderByAsc(ClassEntity::getCreateTime);
        return classMapper.selectPage(page, wrapper);
    }

    @Override
    public ClassEntity getById(Long id) {
        return classMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(ClassEntity classEntity) {
        if (classEntity.getId() == null) {
            classMapper.insert(classEntity);
        } else {
            classMapper.updateById(classEntity);
        }
    }

    @Override
    public void deleteById(Long id) {
        classMapper.deleteById(id);
    }
}