package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysOperationLog;

public interface SysOperationLogService {

    Page<SysOperationLog> queryPage(Integer pageNum, Integer pageSize,
                                    String username, String module,
                                    String operationType, String startDate, String endDate);

    SysOperationLog getById(Long id);

    void save(SysOperationLog log);
}