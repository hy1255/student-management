package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Exam;
import com.example.backend.entity.Score;
import com.example.backend.entity.Student;
import com.example.backend.entity.StudentCourse;
import com.example.backend.mapper.ExamMapper;
import com.example.backend.mapper.ScoreMapper;
import com.example.backend.mapper.StudentCourseMapper;
import com.example.backend.mapper.StudentMapper;
import com.example.backend.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ExamMapper examMapper;   // 必须注入

    @Override
    public List<Map<String, Object>> getScoresByExamId(Long examId) {
        // 1. 查询考试信息，获取课程ID
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return new ArrayList<>();
        }
        Long courseId = exam.getCourseId();

        // 2. 查询该课程下的选课学生
        LambdaQueryWrapper<StudentCourse> scWrapper = new LambdaQueryWrapper<>();
        scWrapper.eq(StudentCourse::getCourseId, courseId);
        List<StudentCourse> scList = studentCourseMapper.selectList(scWrapper);
        if (scList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> studentIds = scList.stream()
                .map(StudentCourse::getStudentId)
                .collect(Collectors.toList());

        // 3. 获取学生信息
        LambdaQueryWrapper<Student> sWrapper = new LambdaQueryWrapper<>();
        sWrapper.in(Student::getId, studentIds);
        List<Student> students = studentMapper.selectList(sWrapper);

        // 4. 获取该考试已有的成绩
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getExamId, examId);
        List<Score> scores = scoreMapper.selectList(scoreWrapper);
        Map<Long, Score> scoreMap = scores.stream()
                .collect(Collectors.toMap(Score::getStudentId, s -> s));

        // 5. 组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student student : students) {
            Map<String, Object> item = new HashMap<>();
            item.put("studentId", student.getId());
            item.put("studentNo", student.getStudentNo());
            item.put("studentName", student.getName());
            item.put("className", student.getClassName());

            Score score = scoreMap.get(student.getId());
            if (score != null) {
                item.put("scoreId", score.getId());
                item.put("score", score.getScore());
                item.put("grade", score.getGrade());
                item.put("comment", score.getComment());
            } else {
                item.put("scoreId", null);
                item.put("score", null);
                item.put("grade", null);
                item.put("comment", null);
            }
            result.add(item);
        }
        return result;
    }

    private String calcGrade(BigDecimal score) {
        if (score == null) return null;
        double val = score.doubleValue();
        if (val >= 90) return "A";
        else if (val >= 80) return "B";
        else if (val >= 70) return "C";
        else if (val >= 60) return "D";
        else return "F";
    }

    @Override
    @Transactional
    public void batchSaveOrUpdate(List<Score> scoreList) {
        for (Score score : scoreList) {
            if (score.getScore() != null) {
                score.setGrade(calcGrade(score.getScore()));
            }
            if (score.getId() == null) {
                scoreMapper.insert(score);
            } else {
                scoreMapper.updateById(score);
            }
        }
    }

    @Override
    public Page<Map<String, Object>> queryPage(Integer pageNum, Integer pageSize, Long studentId, Long courseId, Long examId) {
        Page<Score> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(Score::getStudentId, studentId);
        }
        if (examId != null) {
            wrapper.eq(Score::getExamId, examId);
        }
        wrapper.orderByDesc(Score::getCreateTime);
        Page<Score> scorePage = scoreMapper.selectPage(page, wrapper);

        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setTotal(scorePage.getTotal());

        List<Map<String, Object>> list = new ArrayList<>();
        for (Score score : scorePage.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", score.getId());
            item.put("studentId", score.getStudentId());
            item.put("examId", score.getExamId());
            item.put("score", score.getScore());
            item.put("grade", score.getGrade());
            item.put("comment", score.getComment());
            item.put("createTime", score.getCreateTime());
            list.add(item);
        }
        resultPage.setRecords(list);
        return resultPage;
    }

    @Override
    public Map<String, Object> getExamStats(Long examId) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getExamId, examId);
        List<Score> scores = scoreMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        if (scores == null || scores.isEmpty()) {
            result.put("total", 0);
            result.put("avgScore", 0);
            result.put("maxScore", 0);
            result.put("minScore", 0);
            result.put("passRate", 0);
            result.put("gradeDistribution", new HashMap<>());
            return result;
        }

        double sum = 0;
        double max = -1;
        double min = 101;
        int passCount = 0;
        Map<String, Integer> gradeDist = new HashMap<>();

        for (Score s : scores) {
            if (s.getScore() != null) {
                double val = s.getScore().doubleValue();
                sum += val;
                max = Math.max(max, val);
                min = Math.min(min, val);
                if (val >= 60) passCount++;
                String grade = s.getGrade() != null ? s.getGrade() : calcGrade(s.getScore());
                gradeDist.put(grade, gradeDist.getOrDefault(grade, 0) + 1);
            }
        }

        int size = scores.size();
        result.put("total", size);
        result.put("avgScore", size > 0 ? Math.round(sum / size * 10) / 10.0 : 0);
        result.put("maxScore", max == -1 ? 0 : max);
        result.put("minScore", min == 101 ? 0 : min);
        result.put("passRate", size > 0 ? Math.round((double) passCount / size * 1000) / 10.0 : 0);
        result.put("gradeDistribution", gradeDist);
        return result;
    }

    // ⬇️ 新增：实现 deleteById 方法 ⬇️
    @Override
    public void deleteById(Long id) {
        scoreMapper.deleteById(id);
    }
}