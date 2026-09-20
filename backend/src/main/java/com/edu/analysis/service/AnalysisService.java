package com.edu.analysis.service;

import com.edu.analysis.entity.AnalysisRecord;
import com.edu.analysis.entity.ClassInfo;
import com.edu.analysis.entity.Score;
import com.edu.analysis.entity.Student;
import com.edu.analysis.mapper.AnalysisRecordMapper;
import com.edu.analysis.mapper.ClassInfoMapper;
import com.edu.analysis.mapper.ScoreMapper;
import com.edu.analysis.mapper.StudentMapper;
import com.edu.analysis.ml.KMeans;
import com.edu.analysis.ml.LinearRegression;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 机器学习分析服务：成绩预测、学生聚类、学业预警
 */
@Service
public class AnalysisService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private ClassInfoMapper classInfoMapper;
    @Autowired
    private AnalysisRecordMapper analysisRecordMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private double round2(double v) {
        return Math.round(v * 100) / 100.0;
    }

    /**
     * 线性回归成绩预测
     */
    public Map<String, Object> predict() {
        List<Score> scores = scoreMapper.selectList(null);
        int n = scores.size();
        double[][] X = new double[n][3];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            Score s = scores.get(i);
            X[i][0] = s.getRegularScore() == null ? 0 : s.getRegularScore();
            X[i][1] = s.getHomeworkScore() == null ? 0 : s.getHomeworkScore();
            X[i][2] = s.getAttendance() == null ? 0 : s.getAttendance();
            y[i] = s.getFinalScore() == null ? 0 : s.getFinalScore();
        }

        LinearRegression lr = new LinearRegression();
        List<Double> losses = lr.fit(X, y, 0.01, 2000);
        double r2 = lr.r2(X, y);
        double[] theta = lr.getTheta();

        // 每个学生的平均特征与预测结果（用于可视化）
        Map<Long, String> studentNames = new HashMap<>();
        for (Student s : studentMapper.selectList(null)) {
            studentNames.put(s.getId(), s.getName());
        }
        Map<Long, List<Score>> byStudent = groupByStudent(scores);
        List<Map<String, Object>> predictions = new ArrayList<>();
        for (Map.Entry<Long, List<Score>> e : byStudent.entrySet()) {
            List<Score> list = e.getValue();
            double reg = 0, hw = 0, att = 0, fin = 0;
            for (Score s : list) {
                reg += s.getRegularScore() == null ? 0 : s.getRegularScore();
                hw += s.getHomeworkScore() == null ? 0 : s.getHomeworkScore();
                att += s.getAttendance() == null ? 0 : s.getAttendance();
                fin += s.getFinalScore() == null ? 0 : s.getFinalScore();
            }
            int c = list.size();
            double[] avgFeat = {reg / c, hw / c, att / c};
            double predicted = lr.predict(avgFeat);
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("name", studentNames.get(e.getKey()));
            p.put("actual", round2(fin / c));
            p.put("predicted", round2(predicted));
            predictions.add(p);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("theta", theta);
        result.put("r2", round2(r2));
        result.put("featureNames", new String[]{"平时成绩", "作业成绩", "出勤次数"});
        result.put("loss", losses);
        result.put("predictions", predictions);

        saveRecord("PREDICT", "期末成绩预测", result);
        return result;
    }

    /**
     * K-Means 学生分层聚类
     */
    public Map<String, Object> cluster() {
        List<Score> scores = scoreMapper.selectList(null);
        Map<Long, List<Score>> byStudent = groupByStudent(scores);

        // 每个学生的三维特征：平均总评、出勤率、平均作业成绩
        List<Long> ids = new ArrayList<>(byStudent.keySet());
        double[][] X = new double[ids.size()][3];
        double[] avgTotalArr = new double[ids.size()];
        double[] attRateArr = new double[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            List<Score> list = byStudent.get(ids.get(i));
            double total = 0, att = 0, hw = 0;
            for (Score s : list) {
                total += s.getTotalScore() == null ? 0 : s.getTotalScore();
                att += s.getAttendance() == null ? 0 : s.getAttendance();
                hw += s.getHomeworkScore() == null ? 0 : s.getHomeworkScore();
            }
            int c = list.size();
            X[i][0] = total / c;          // 平均总评
            X[i][1] = (att / c) / 20.0;   // 出勤率(0~1)
            X[i][2] = hw / c;             // 平均作业成绩
            avgTotalArr[i] = X[i][0];
            attRateArr[i] = X[i][1] * 100;
        }

        double[][] Xn = normalize(X);
        KMeans.ClusterResult cr = KMeans.fit(Xn, 3, 100);

        // 根据各簇平均总评排序并命名
        double[] clusterAvg = new double[3];
        int[] clusterCnt = new int[3];
        for (int i = 0; i < ids.size(); i++) {
            clusterAvg[cr.labels[i]] += avgTotalArr[i];
            clusterCnt[cr.labels[i]]++;
        }
        int[] order = {0, 1, 2};
        // 简单排序簇（按平均分降序）
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                if (clusterAvg[order[j]] / Math.max(1, clusterCnt[order[j]]) >
                        clusterAvg[order[i]] / Math.max(1, clusterCnt[order[i]])) {
                    int t = order[i];
                    order[i] = order[j];
                    order[j] = t;
                }
            }
        }
        String[] levelNames = {"优秀", "良好", "需关注"};
        int[] rankOfCluster = new int[3];
        for (int r = 0; r < 3; r++) {
            rankOfCluster[order[r]] = r;
        }

        Map<Long, String> studentNames = new HashMap<>();
        for (Student s : studentMapper.selectList(null)) {
            studentNames.put(s.getId(), s.getName());
        }

        List<Map<String, Object>> students = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("name", studentNames.get(ids.get(i)));
            p.put("avgScore", round2(avgTotalArr[i]));
            p.put("attendanceRate", round2(attRateArr[i]));
            p.put("cluster", rankOfCluster[cr.labels[i]]);
            students.add(p);
        }

        List<Map<String, Object>> clusterInfo = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            int orig = order[r];
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", r);
            info.put("label", levelNames[r]);
            info.put("count", clusterCnt[orig]);
            info.put("avgScore", round2(clusterAvg[orig] / Math.max(1, clusterCnt[orig])));
            clusterInfo.add(info);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("iterations", cr.iterations);
        result.put("clusters", clusterInfo);
        result.put("students", students);

        saveRecord("CLUSTER", "学生分层聚类", result);
        return result;
    }

    /**
     * 学业预警
     */
    public Map<String, Object> warning() {
        Map<String, Object> clusterResult = cluster();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) clusterResult.get("students");

        Map<Long, String> studentNo = new HashMap<>();
        Map<Long, Long> studentClass = new HashMap<>();
        for (Student s : studentMapper.selectList(null)) {
            studentNo.put(s.getId(), s.getStudentNo());
            studentClass.put(s.getId(), s.getClassId());
        }
        Map<Long, String> classNames = new HashMap<>();
        for (ClassInfo c : classInfoMapper.selectList(null)) {
            classNames.put(c.getId(), c.getClassName());
        }

        List<Map<String, Object>> warnings = new ArrayList<>();
        // 按学生姓名建立索引
        Map<String, Map<String, Object>> nameIndex = new HashMap<>();
        for (Map<String, Object> st : students) {
            nameIndex.put((String) st.get("name"), st);
        }
        // 从成绩表计算平均分，识别需关注学生
        Map<Long, List<Score>> byStudent = groupByStudent(scoreMapper.selectList(null));
        for (Map.Entry<Long, List<Score>> e : byStudent.entrySet()) {
            Long sid = e.getKey();
            double total = 0;
            for (Score s : e.getValue()) {
                total += s.getTotalScore() == null ? 0 : s.getTotalScore();
            }
            double avg = total / e.getValue().size();
            if (avg < 70) {
                String name = studentNames(sid);
                Map<String, Object> st = nameIndex.get(name);
                int cluster = st == null ? 2 : ((Number) st.get("cluster")).intValue();
                Map<String, Object> w = new LinkedHashMap<>();
                w.put("studentNo", studentNo.get(sid));
                w.put("name", name);
                w.put("className", classNames.get(studentClass.get(sid)));
                w.put("avgScore", round2(avg));
                w.put("cluster", cluster);
                w.put("risk", avg < 60 ? "高风险" : "中风险");
                w.put("advice", avg < 60 ? "建议重点关注，安排学业帮扶" : "成绩偏低，建议加强辅导");
                warnings.add(w);
            }
        }
        warnings.sort((a, b) -> Double.compare((Double) a.get("avgScore"), (Double) b.get("avgScore")));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", warnings.size());
        result.put("list", warnings);
        saveRecord("WARNING", "学业预警", result);
        return result;
    }

    private String studentNames(Long id) {
        Student s = studentMapper.selectById(id);
        return s == null ? "未知" : s.getName();
    }

    private Map<Long, List<Score>> groupByStudent(List<Score> scores) {
        Map<Long, List<Score>> map = new HashMap<>();
        for (Score s : scores) {
            map.computeIfAbsent(s.getStudentId(), k -> new ArrayList<>()).add(s);
        }
        return map;
    }

    /**
     * z-score 归一化
     */
    private double[][] normalize(double[][] X) {
        int n = X.length;
        int f = X[0].length;
        double[] mean = new double[f];
        double[] std = new double[f];
        for (int j = 0; j < f; j++) {
            double sum = 0;
            for (double[] row : X) sum += row[j];
            mean[j] = sum / n;
            double sq = 0;
            for (double[] row : X) sq += (row[j] - mean[j]) * (row[j] - mean[j]);
            std[j] = Math.sqrt(sq / n);
            if (std[j] < 1e-9) std[j] = 1;
        }
        double[][] Xn = new double[n][f];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < f; j++) {
                Xn[i][j] = (X[i][j] - mean[j]) / std[j];
            }
        }
        return Xn;
    }

    private void saveRecord(String type, String name, Object result) {
        try {
            AnalysisRecord record = new AnalysisRecord();
            record.setRecordType(type);
            record.setRecordName(name);
            record.setResultJson(objectMapper.writeValueAsString(result));
            analysisRecordMapper.insert(record);
        } catch (Exception ignored) {
        }
    }
}
