package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String examName;

    private Long courseId;

    private LocalDate examDate;

    private String examTime;

    private String location;

    private String examType;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}