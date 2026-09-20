package com.edu.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 班级
 */
@Data
@TableName("class_info")
public class ClassInfo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String className;

    private String grade;

    private String major;

    private String headTeacher;

    private Integer studentCount;
}
