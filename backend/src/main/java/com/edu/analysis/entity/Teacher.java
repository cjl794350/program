package com.edu.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 教师
 */
@Data
@TableName("teacher")
public class Teacher implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String teacherNo;

    private String name;

    private String gender;

    private String title;

    private String department;
}
