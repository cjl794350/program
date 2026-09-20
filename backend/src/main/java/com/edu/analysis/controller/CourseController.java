package com.edu.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.analysis.common.Result;
import com.edu.analysis.entity.Course;
import com.edu.analysis.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程管理接口
 */
@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String keyword) {
        QueryWrapper<Course> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("course_name", keyword).or().like("course_no", keyword);
        }
        qw.orderByAsc("course_no");
        Page<Course> p = courseMapper.selectPage(new Page<>(page, size), qw);
        Map<String, Object> data = new HashMap<>();
        data.put("records", p.getRecords());
        data.put("total", p.getTotal());
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.success(courseMapper.selectList(new QueryWrapper<Course>().orderByAsc("course_no")));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Course course) {
        courseMapper.insert(course);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Course course) {
        courseMapper.updateById(course);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseMapper.deleteById(id);
        return Result.success();
    }
}
