package com.edu.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.analysis.common.Result;
import com.edu.analysis.entity.Course;
import com.edu.analysis.entity.Score;
import com.edu.analysis.entity.Student;
import com.edu.analysis.mapper.CourseMapper;
import com.edu.analysis.mapper.ScoreMapper;
import com.edu.analysis.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理接口
 */
@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) Long studentId,
                                            @RequestParam(required = false) Long courseId) {
        QueryWrapper<Score> qw = new QueryWrapper<>();
        if (studentId != null) {
            qw.eq("student_id", studentId);
        }
        if (courseId != null) {
            qw.eq("course_id", courseId);
        }
        qw.orderByAsc("student_id").orderByAsc("course_id");
        Page<Score> p = scoreMapper.selectPage(new Page<>(page, size), qw);

        Map<Long, String> studentNames = new HashMap<>();
        for (Student s : studentMapper.selectList(null)) {
            studentNames.put(s.getId(), s.getName());
        }
        Map<Long, String> courseNames = new HashMap<>();
        for (Course c : courseMapper.selectList(null)) {
            courseNames.put(c.getId(), c.getCourseName());
        }

        List<Map<String, Object>> records = new ArrayList<>();
        for (Score s : p.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", s.getId());
            item.put("studentId", s.getStudentId());
            item.put("studentName", studentNames.getOrDefault(s.getStudentId(), ""));
            item.put("courseId", s.getCourseId());
            item.put("courseName", courseNames.getOrDefault(s.getCourseId(), ""));
            item.put("regularScore", s.getRegularScore());
            item.put("homeworkScore", s.getHomeworkScore());
            item.put("attendance", s.getAttendance());
            item.put("finalScore", s.getFinalScore());
            item.put("totalScore", s.getTotalScore());
            item.put("semester", s.getSemester());
            records.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", p.getTotal());
        return Result.success(data);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Score score) {
        scoreMapper.insert(score);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Score score) {
        scoreMapper.updateById(score);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scoreMapper.deleteById(id);
        return Result.success();
    }
}
