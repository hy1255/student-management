package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String operationType;

    private String description;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private String ipAddress;

    private LocalDateTime operateTime;
}