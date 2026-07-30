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

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // 学生总数
        long studentCount = studentMapper.selectCount(null);
        result.put("studentCount", studentCount);

        // 课程总数
        long courseCount = courseMapper.selectCount(null);
        result.put("courseCount", courseCount);

        // 考试总数
        long examCount = examMapper.selectCount(null);
        result.put("examCount", examCount);

        // 平均分
        List<Score> allScores = scoreMapper.selectList(null);
        if (allScores.isEmpty()) {
            result.put("avgScore", 0);
        } else {
            double avg = allScores.stream()
                    .mapToDouble(s -> s.getScore() != null ? s.getScore().doubleValue() : 0)
                    .average()
                    .orElse(0);
            result.put("avgScore", Math.round(avg * 10) / 10.0);
        }

        // 近期考试（最近3场）
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.orderByDesc(Exam::getExamDate);
        examWrapper.last("LIMIT 3");
        List<Exam> recentExams = examMapper.selectList(examWrapper);
        result.put("recentExams", recentExams);

        // 各课程平均分（用于图表）
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        List<Course> courses = courseMapper.selectList(courseWrapper);
        Map<String, Object> courseAvgMap = new LinkedHashMap<>();
        for (Course course : courses) {
            // 获取该课程所有考试的成绩
            LambdaQueryWrapper<Exam> examWrapper2 = new LambdaQueryWrapper<>();
            examWrapper2.eq(Exam::getCourseId, course.getId());
            List<Exam> exams = examMapper.selectList(examWrapper2);
            if (exams.isEmpty()) continue;

            List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());
            LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
            scoreWrapper.in(Score::getExamId, examIds);
            List<Score> scores = scoreMapper.selectList(scoreWrapper);
            if (scores.isEmpty()) continue;

            double avg = scores.stream()
                    .mapToDouble(s -> s.getScore() != null ? s.getScore().doubleValue() : 0)
                    .average()
                    .orElse(0);
            courseAvgMap.put(course.getCourseName(), Math.round(avg * 10) / 10.0);
        }
        result.put("courseAvgMap", courseAvgMap);

        return result;
    }
}