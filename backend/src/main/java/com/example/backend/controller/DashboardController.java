package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.entity.Course;
import com.example.backend.entity.Exam;
import com.example.backend.entity.Score;
import com.example.backend.entity.Student;
import com.example.backend.mapper.CourseMapper;
import com.example.backend.mapper.ExamMapper;
import com.example.backend.mapper.ScoreMapper;
import com.example.backend.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();

        try {
            // ============================================================
            // 1. 统计卡片（学生总数、课程总数、考试总数、平均分）
            // ============================================================
            long studentCount = studentMapper.selectCount(null);
            result.put("studentCount", studentCount);

            long courseCount = courseMapper.selectCount(null);
            result.put("courseCount", courseCount);

            long examCount = examMapper.selectCount(null);
            result.put("examCount", examCount);

            List<Score> allScores = scoreMapper.selectList(null);
            if (allScores == null || allScores.isEmpty()) {
                result.put("avgScore", 0);
            } else {
                double avg = allScores.stream()
                        .filter(s -> s.getScore() != null)
                        .mapToDouble(s -> s.getScore().doubleValue())
                        .average()
                        .orElse(0);
                result.put("avgScore", Math.round(avg * 10) / 10.0);
            }

            // ============================================================
            // 2. 各考试及格率分布（柱状图数据）
            // ============================================================
            List<Map<String, Object>> passRateList = new ArrayList<>();
            List<Exam> exams = examMapper.selectList(null);
            if (exams != null && !exams.isEmpty()) {
                for (Exam exam : exams) {
                    LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
                    scoreWrapper.eq(Score::getExamId, exam.getId());
                    List<Score> scores = scoreMapper.selectList(scoreWrapper);

                    if (scores == null || scores.isEmpty()) continue;

                    long total = scores.size();
                    long passCount = scores.stream()
                            .filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60)
                            .count();

                    double passRate = Math.round((double) passCount / total * 1000) / 10.0;

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("examName", exam.getExamName());
                    item.put("passRate", passRate);
                    passRateList.add(item);
                }
            }
            result.put("passRateData", passRateList);

            // ============================================================
            // 3. 各课程平均分对比（柱状图数据）
            // ============================================================
            List<Map<String, Object>> courseAvgList = new ArrayList<>();
            List<Course> courses = courseMapper.selectList(null);
            if (courses != null && !courses.isEmpty()) {
                for (Course course : courses) {
                    // 获取该课程的所有考试
                    LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
                    examWrapper.eq(Exam::getCourseId, course.getId());
                    List<Exam> courseExams = examMapper.selectList(examWrapper);

                    if (courseExams == null || courseExams.isEmpty()) continue;

                    List<Long> examIds = courseExams.stream()
                            .map(Exam::getId)
                            .collect(Collectors.toList());

                    LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
                    scoreWrapper.in(Score::getExamId, examIds);
                    List<Score> scores = scoreMapper.selectList(scoreWrapper);

                    if (scores == null || scores.isEmpty()) continue;

                    double avg = scores.stream()
                            .filter(s -> s.getScore() != null)
                            .mapToDouble(s -> s.getScore().doubleValue())
                            .average()
                            .orElse(0);

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("courseName", course.getCourseName());
                    item.put("avgScore", Math.round(avg * 10) / 10.0);
                    courseAvgList.add(item);
                }
            }
            result.put("courseAvgData", courseAvgList);

            // ============================================================
            // 4. 近期考试安排（只显示待考 PENDING，按日期升序）
            // ============================================================
            List<Map<String, Object>> recentExamList = new ArrayList<>();
            LambdaQueryWrapper<Exam> recentWrapper = new LambdaQueryWrapper<>();
            recentWrapper.eq(Exam::getStatus, "PENDING")
                    .orderByAsc(Exam::getExamDate);
            List<Exam> recentExams = examMapper.selectList(recentWrapper);

            if (recentExams != null && !recentExams.isEmpty()) {
                for (Exam exam : recentExams) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("examName", exam.getExamName());
                    Course course = courseMapper.selectById(exam.getCourseId());
                    item.put("courseName", course != null ? course.getCourseName() : "未知课程");
                    item.put("examDate", exam.getExamDate());
                    item.put("examTime", exam.getExamTime());
                    item.put("location", exam.getLocation());
                    item.put("status", exam.getStatus());
                    recentExamList.add(item);
                }
            }
            result.put("recentExams", recentExamList);

        } catch (Exception e) {
            e.printStackTrace();
            // 返回空数据，防止前端白屏
            result.put("studentCount", 0);
            result.put("courseCount", 0);
            result.put("examCount", 0);
            result.put("avgScore", 0);
            result.put("passRateData", new ArrayList<>());
            result.put("courseAvgData", new ArrayList<>());
            result.put("recentExams", new ArrayList<>());
        }

        return result;
    }
}