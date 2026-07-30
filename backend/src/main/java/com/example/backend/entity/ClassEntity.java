package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class")
public class ClassEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String classCode;

    private String className;

    private String grade;

    private String headTeacher;

    private Integer studentCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}