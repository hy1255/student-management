package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysOperationLog;
import com.example.backend.mapper.SysOperationLogMapper;
import com.example.backend.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysOperationLogServiceImpl implements SysOperationLogService {

    @Autowired
    private SysOperationLogMapper logMapper;

    @Override
    public Page<SysOperationLog> queryPage(Integer pageNum, Integer pageSize,
                                           String username, String module,
                                           String operationType, String startDate, String endDate) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(SysOperationLog::getUsername, username);
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(SysOperationLog::getModule, module);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysOperationLog::getOperationType, operationType);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(SysOperationLog::getOperateTime, startDate + " 00:00:00");
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(SysOperationLog::getOperateTime, endDate + " 23:59:59");
        }

        wrapper.orderByDesc(SysOperationLog::getOperateTime);
        return logMapper.selectPage(page, wrapper);
    }

    @Override
    public SysOperationLog getById(Long id) {
        return logMapper.selectById(id);
    }

    @Override
    public void save(SysOperationLog log) {
        logMapper.insert(log);
    }
}