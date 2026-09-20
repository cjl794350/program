package com.edu.analysis.controller;

import com.edu.analysis.common.Result;
import com.edu.analysis.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计分析接口
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(statisticsService.overview());
    }

    @GetMapping("/scoreDistribution")
    public Result<List<Map<String, Object>>> scoreDistribution() {
        return Result.success(statisticsService.scoreDistribution());
    }

    @GetMapping("/courseAverage")
    public Result<List<Map<String, Object>>> courseAverage() {
        return Result.success(statisticsService.courseAverage());
    }

    @GetMapping("/classCompare")
    public Result<List<Map<String, Object>>> classCompare() {
        return Result.success(statisticsService.classCompare());
    }

    @GetMapping("/radar")
    public Result<List<Map<String, Object>>> radar() {
        return Result.success(statisticsService.radar());
    }

    @GetMapping("/correlation")
    public Result<Map<String, Object>> correlation() {
        return Result.success(statisticsService.correlation());
    }
}
