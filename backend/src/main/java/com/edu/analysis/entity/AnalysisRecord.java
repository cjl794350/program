package com.edu.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 分析记录
 */
@Data
@TableName("analysis_record")
public class AnalysisRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String recordType;

    private String recordName;

    private String resultJson;

    private Date createTime;
}
