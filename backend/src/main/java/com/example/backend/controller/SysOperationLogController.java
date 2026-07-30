package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysOperationLog;
import com.example.backend.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService logService;

    @GetMapping("/page")
    public Page<SysOperationLog> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return logService.queryPage(pageNum, pageSize, username, module, operationType, startDate, endDate);
    }

    @GetMapping("/{id}")
    public SysOperationLog getById(@PathVariable Long id) {
        return logService.getById(id);
    }
}