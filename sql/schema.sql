-- =============================================================
-- 教育数据分析可视化系统 数据库结构脚本 (DDL)
-- 数据库: edu_analysis
-- 说明: 先执行本脚本创建数据库与表结构，再执行 data.sql 导入数据
-- =============================================================

DROP DATABASE IF EXISTS edu_analysis;
CREATE DATABASE edu_analysis DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE edu_analysis;

-- 1. 系统用户表（管理员登录）
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(MD5)',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    role        VARCHAR(20)  DEFAULT 'ADMIN' COMMENT '角色',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 班级表
CREATE TABLE class_info (
    id             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    class_name     VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade          VARCHAR(20) DEFAULT NULL COMMENT '年级',
    major          VARCHAR(50) DEFAULT NULL COMMENT '专业',
    head_teacher   VARCHAR(50) DEFAULT NULL COMMENT '班主任',
    student_count  INT         DEFAULT 0 COMMENT '学生人数',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 3. 教师表
CREATE TABLE teacher (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    teacher_no VARCHAR(20) NOT NULL COMMENT '工号',
    name       VARCHAR(50) NOT NULL COMMENT '姓名',
    gender     VARCHAR(10) DEFAULT NULL COMMENT '性别',
    title      VARCHAR(30) DEFAULT NULL COMMENT '职称',
    department VARCHAR(50) DEFAULT NULL COMMENT '所属学院',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_no (teacher_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 4. 学生表
CREATE TABLE student (
    id              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_no      VARCHAR(20) NOT NULL COMMENT '学号',
    name            VARCHAR(50) NOT NULL COMMENT '姓名',
    gender          VARCHAR(10) DEFAULT NULL COMMENT '性别',
    age             INT         DEFAULT NULL COMMENT '年龄',
    class_id        BIGINT      DEFAULT NULL COMMENT '班级ID',
    phone           VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email           VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    enrollment_year VARCHAR(20) DEFAULT NULL COMMENT '入学年份',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (student_no),
    KEY idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 5. 课程表
CREATE TABLE course (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    course_no    VARCHAR(20)   NOT NULL COMMENT '课程编号',
    course_name  VARCHAR(100)  NOT NULL COMMENT '课程名称',
    credit       DECIMAL(3,1)  DEFAULT 0 COMMENT '学分',
    teacher_name VARCHAR(50)   DEFAULT NULL COMMENT '任课教师',
    semester     VARCHAR(30)   DEFAULT NULL COMMENT '开课学期',
    course_type  VARCHAR(20)   DEFAULT NULL COMMENT '课程类型(必修/选修)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_course_no (course_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 6. 成绩表
CREATE TABLE score (
    id             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_id     BIGINT        NOT NULL COMMENT '学生ID',
    course_id      BIGINT        NOT NULL COMMENT '课程ID',
    regular_score  DECIMAL(5,2)  DEFAULT NULL COMMENT '平时成绩',
    homework_score DECIMAL(5,2)  DEFAULT NULL COMMENT '作业成绩',
    attendance     INT           DEFAULT 0 COMMENT '出勤次数',
    final_score    DECIMAL(5,2)  DEFAULT NULL COMMENT '期末成绩',
    total_score    DECIMAL(5,2)  DEFAULT NULL COMMENT '总评成绩',
    semester       VARCHAR(30)   DEFAULT NULL COMMENT '学期',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 7. 分析记录表（机器学习分析结果）
CREATE TABLE analysis_record (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    record_type   VARCHAR(50)  NOT NULL COMMENT '分析类型(PREDICT/CLUSTER/WARNING)',
    record_name   VARCHAR(100) DEFAULT NULL COMMENT '分析名称',
    result_json   TEXT         COMMENT '分析结果(JSON)',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分析记录表';
