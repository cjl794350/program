package com.edu.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.analysis.common.Result;
import com.edu.analysis.entity.Student;
import com.edu.analysis.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生管理接口
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String keyword) {
        QueryWrapper<Student> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword).or().like("student_no", keyword);
        }
        qw.orderByAsc("student_no");
        Page<Student> p = studentMapper.selectPage(new Page<>(page, size), qw);
        Map<String, Object> data = new HashMap<>();
        data.put("records", p.getRecords());
        data.put("total", p.getTotal());
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<Student>> list() {
        return Result.success(studentMapper.selectList(new QueryWrapper<Student>().orderByAsc("student_no")));
    }

    @GetMapping("/{id}")
    public Result<Student> get(@PathVariable Long id) {
        return Result.success(studentMapper.selectById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Student student) {
        studentMapper.insert(student);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Student student) {
        studentMapper.updateById(student);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentMapper.deleteById(id);
        return Result.success();
    }
}
