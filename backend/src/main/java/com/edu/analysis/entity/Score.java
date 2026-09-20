package com.edu.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 成绩
 */
@Data
@TableName("score")
public class Score implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Double regularScore;

    private Double homeworkScore;

    private Integer attendance;

    private Double finalScore;

    private Double totalScore;

    private String semester;
}
