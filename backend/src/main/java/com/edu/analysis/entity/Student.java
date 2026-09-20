package com.edu.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生
 */
@Data
@TableName("student")
public class Student implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;

    private String name;

    private String gender;

    private Integer age;

    private Long classId;

    private String phone;

    private String email;

    private String enrollmentYear;
}
