package com.edu.analysis.service;

import com.edu.analysis.entity.ClassInfo;
import com.edu.analysis.entity.Course;
import com.edu.analysis.entity.Score;
import com.edu.analysis.entity.Student;
import com.edu.analysis.mapper.ClassInfoMapper;
import com.edu.analysis.mapper.CourseMapper;
import com.edu.analysis.mapper.ScoreMapper;
import com.edu.analysis.mapper.StudentMapper;
import com.edu.analysis.mapper.TeacherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计分析服务：在内存中完成各类教育数据统计
 */
@Service
public class StatisticsService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ClassInfoMapper classInfoMapper;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    private double round2(double v) {
        return Math.round(v * 100) / 100.0;
    }

    /**
     * 数据总览
     */
    public Map<String, Object> overview() {
        List<Score> scores = scoreMapper.selectList(null);
        long studentCount = studentMapper.selectCount(null);
        long teacherCount = teacherMapper.selectCount(null);
        long courseCount = courseMapper.selectCount(null);
        long classCount = classInfoMapper.selectCount(null);

        double sum = 0;
        int pass = 0, excellent = 0;
        for (Score s : scores) {
            double t = s.getTotalScore() == null ? 0 : s.getTotalScore();
            sum += t;
            if (t >= 60) pass++;
            if (t >= 90) excellent++;
        }
        int n = scores.size();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("studentCount", studentCount);
        map.put("teacherCount", teacherCount);
        map.put("courseCount", courseCount);
        map.put("classCount", classCount);
        map.put("scoreCount", n);
        map.put("avgScore", n == 0 ? 0 : round2(sum / n));
        map.put("passRate", n == 0 ? 0 : round2(pass * 100.0 / n));
        map.put("excellentRate", n == 0 ? 0 : round2(excellent * 100.0 / n));
        return map;
    }

    /**
     * 学生平均成绩分布（按分数段统计人数）
     */
    public List<Map<String, Object>> scoreDistribution() {
        Map<Long, List<Double>> byStudent = groupTotalScoreByStudent();
        String[] names = {"不及格(<60)", "及格(60-69)", "中等(70-79)", "良好(80-89)", "优秀(≥90)"};
        int[] counts = new int[5];
        for (List<Double> ts : byStudent.values()) {
            double avg = avg(ts);
            int idx;
            if (avg < 60) idx = 0;
            else if (avg < 70) idx = 1;
            else if (avg < 80) idx = 2;
            else if (avg < 90) idx = 3;
            else idx = 4;
            counts[idx]++;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", names[i]);
            item.put("value", counts[i]);
            list.add(item);
        }
        return list;
    }

    /**
     * 各课程平均成绩与及格率
     */
    public List<Map<String, Object>> courseAverage() {
        Map<Long, String> courseNames = new HashMap<>();
        for (Course c : courseMapper.selectList(null)) {
            courseNames.put(c.getId(), c.getCourseName());
        }
        Map<Long, List<Double>> byCourse = new HashMap<>();
        for (Score s : scoreMapper.selectList(null)) {
            byCourse.computeIfAbsent(s.getCourseId(), k -> new ArrayList<>()).add(s.getTotalScore());
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<Long, List<Double>> e : byCourse.entrySet()) {
            List<Double> ts = e.getValue();
            int pass = 0;
            double max = -1, min = 101, sum = 0;
            for (double t : ts) {
                sum += t;
                if (t >= 60) pass++;
                max = Math.max(max, t);
                min = Math.min(min, t);
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("courseName", courseNames.get(e.getKey()));
            item.put("avg", round2(sum / ts.size()));
            item.put("passRate", round2(pass * 100.0 / ts.size()));
            item.put("max", round2(max));
            item.put("min", round2(min));
            item.put("count", ts.size());
            list.add(item);
        }
        return list;
    }

    /**
     * 班级平均成绩对比
     */
    public List<Map<String, Object>> classCompare() {
        Map<Long, String> className = new HashMap<>();
        for (ClassInfo c : classInfoMapper.selectList(null)) {
            className.put(c.getId(), c.getClassName());
        }
        Map<Long, Long> studentClass = new HashMap<>();
        for (Student s : studentMapper.selectList(null)) {
            studentClass.put(s.getId(), s.getClassId());
        }
        Map<Long, List<Double>> byClass = new HashMap<>();
        for (Score s : scoreMapper.selectList(null)) {
            Long cid = studentClass.get(s.getStudentId());
            if (cid != null) {
                byClass.computeIfAbsent(cid, k -> new ArrayList<>()).add(s.getTotalScore());
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<Long, List<Double>> e : byClass.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("className", className.get(e.getKey()));
            item.put("avg", round2(avg(e.getValue())));
            list.add(item);
        }
        return list;
    }

    /**
     * 各课程平均成绩（雷达图）
     */
    public List<Map<String, Object>> radar() {
        Map<Long, String> courseNames = new HashMap<>();
        for (Course c : courseMapper.selectList(null)) {
            courseNames.put(c.getId(), c.getCourseName());
        }
        Map<Long, List<Double>> byCourse = new HashMap<>();
        for (Score s : scoreMapper.selectList(null)) {
            byCourse.computeIfAbsent(s.getCourseId(), k -> new ArrayList<>()).add(s.getTotalScore());
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<Long, List<Double>> e : byCourse.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("courseName", courseNames.get(e.getKey()));
            item.put("avg", round2(avg(e.getValue())));
            list.add(item);
        }
        return list;
    }

    /**
     * 特征相关性热力图（Pearson 相关系数）
     */
    public Map<String, Object> correlation() {
        List<Score> scores = scoreMapper.selectList(null);
        String[] names = {"平时成绩", "作业成绩", "出勤", "期末成绩"};
        int m = names.length;
        double[][] data = new double[scores.size()][m];
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            data[i][0] = s.getRegularScore() == null ? 0 : s.getRegularScore();
            data[i][1] = s.getHomeworkScore() == null ? 0 : s.getHomeworkScore();
            data[i][2] = s.getAttendance() == null ? 0 : s.getAttendance();
            data[i][3] = s.getFinalScore() == null ? 0 : s.getFinalScore();
        }
        double[][] matrix = new double[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = round2(pearson(col(data, i), col(data, j)));
            }
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("names", names);
        map.put("matrix", matrix);
        return map;
    }

    private double[] col(double[][] data, int c) {
        double[] res = new double[data.length];
        for (int i = 0; i < data.length; i++) res[i] = data[i][c];
        return res;
    }

    private double pearson(double[] a, double[] b) {
        int n = a.length;
        double ma = avg(a), mb = avg(b);
        double cov = 0, va = 0, vb = 0;
        for (int i = 0; i < n; i++) {
            cov += (a[i] - ma) * (b[i] - mb);
            va += (a[i] - ma) * (a[i] - ma);
            vb += (b[i] - mb) * (b[i] - mb);
        }
        if (va == 0 || vb == 0) return 0;
        return cov / Math.sqrt(va * vb);
    }

    private double avg(double[] a) {
        double s = 0;
        for (double v : a) s += v;
        return a.length == 0 ? 0 : s / a.length;
    }

    private double avg(List<Double> list) {
        double s = 0;
        for (double v : list) s += v;
        return list.isEmpty() ? 0 : s / list.size();
    }

    private Map<Long, List<Double>> groupTotalScoreByStudent() {
        Map<Long, List<Double>> byStudent = new HashMap<>();
        for (Score s : scoreMapper.selectList(null)) {
            byStudent.computeIfAbsent(s.getStudentId(), k -> new ArrayList<>()).add(s.getTotalScore());
        }
        return byStudent;
    }
}
