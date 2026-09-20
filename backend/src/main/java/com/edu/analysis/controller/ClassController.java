package com.edu.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.analysis.common.Result;
import com.edu.analysis.entity.ClassInfo;
import com.edu.analysis.mapper.ClassInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级管理接口
 */
@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String keyword) {
        QueryWrapper<ClassInfo> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("class_name", keyword).or().like("major", keyword);
        }
        qw.orderByAsc("id");
        Page<ClassInfo> p = classInfoMapper.selectPage(new Page<>(page, size), qw);
        Map<String, Object> data = new HashMap<>();
        data.put("records", p.getRecords());
        data.put("total", p.getTotal());
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<ClassInfo>> list() {
        return Result.success(classInfoMapper.selectList(new QueryWrapper<ClassInfo>().orderByAsc("id")));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ClassInfo classInfo) {
        classInfoMapper.insert(classInfo);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody ClassInfo classInfo) {
        classInfoMapper.updateById(classInfo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classInfoMapper.deleteById(id);
        return Result.success();
    }
}
