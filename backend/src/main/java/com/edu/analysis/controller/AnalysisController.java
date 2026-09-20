package com.edu.analysis.controller;

import com.edu.analysis.common.Result;
import com.edu.analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 机器学习分析接口
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/predict")
    public Result<Map<String, Object>> predict() {
        return Result.success(analysisService.predict());
    }

    @PostMapping("/cluster")
    public Result<Map<String, Object>> cluster() {
        return Result.success(analysisService.cluster());
    }

    @GetMapping("/warning")
    public Result<Map<String, Object>> warning() {
        return Result.success(analysisService.warning());
    }
}
