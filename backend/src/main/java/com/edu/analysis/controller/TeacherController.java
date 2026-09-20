package com.edu.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.analysis.common.Result;
import com.edu.analysis.entity.Teacher;
import com.edu.analysis.mapper.TeacherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师管理接口
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String keyword) {
        QueryWrapper<Teacher> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword).or().like("teacher_no", keyword);
        }
        qw.orderByAsc("teacher_no");
        Page<Teacher> p = teacherMapper.selectPage(new Page<>(page, size), qw);
        Map<String, Object> data = new HashMap<>();
        data.put("records", p.getRecords());
        data.put("total", p.getTotal());
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<Teacher>> list() {
        return Result.success(teacherMapper.selectList(new QueryWrapper<Teacher>().orderByAsc("teacher_no")));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Teacher teacher) {
        teacherMapper.insert(teacher);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Teacher teacher) {
        teacherMapper.updateById(teacher);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teacherMapper.deleteById(id);
        return Result.success();
    }
}
