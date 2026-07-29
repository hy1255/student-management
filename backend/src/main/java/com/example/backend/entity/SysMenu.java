package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String menuName;

    private String menuType;

    private String routePath;

    private String icon;

    private String permission;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // ⬇️ 关键修改：标记为非数据库字段
    @TableField(exist = false)
    private List<SysMenu> children;
}