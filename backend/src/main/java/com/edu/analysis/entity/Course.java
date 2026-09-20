package com.edu.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 课程
 */
@Data
@TableName("course")
public class Course implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String courseNo;

    private String courseName;

    private Double credit;

    private String teacherName;

    private String semester;

    private String courseType;
}
