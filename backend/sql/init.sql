-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_db DEFAULT CHARACTER SET utf8mb4;

-- 使用数据库
USE student_db;

-- 创建学生表
CREATE TABLE IF NOT EXISTS `student` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT NULL COMMENT '性别 (0-女, 1-男)',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 插入测试数据
INSERT INTO `student` (`name`, `gender`, `age`, `class_name`) VALUES
                                                                  ('张三', 1, 20, '计算机科学与技术1班'),
                                                                  ('李四', 0, 21, '软件工程2班'),
                                                                  ('王五', 1, 19, '网络工程3班');