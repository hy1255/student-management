package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.entity.Student;
import com.example.backend.entity.StudentCourse;
import com.example.backend.mapper.StudentCourseMapper;
import com.example.backend.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student-course")
public class StudentCourseController {

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private StudentMapper studentMapper;

    // 获取某课程已选学生列表
    @GetMapping("/course/{courseId}")
    public List<Student> getStudentsByCourse(@PathVariable Long courseId) {
        LambdaQueryWrapper<StudentCourse> scWrapper = new LambdaQueryWrapper<>();
        scWrapper.eq(StudentCourse::getCourseId, courseId);
        List<StudentCourse> scList = studentCourseMapper.selectList(scWrapper);
        if (scList.isEmpty()) return new ArrayList<>();

        List<Long> studentIds = scList.stream()
                .map(StudentCourse::getStudentId)
                .toList();

        LambdaQueryWrapper<Student> sWrapper = new LambdaQueryWrapper<>();
        sWrapper.in(Student::getId, studentIds);
        return studentMapper.selectList(sWrapper);
    }

    // 获取未选某课程的学生列表（用于添加）
    @GetMapping("/course/{courseId}/available")
    public List<Student> getAvailableStudents(@PathVariable Long courseId) {
        // 已选学生ID
        LambdaQueryWrapper<StudentCourse> scWrapper = new LambdaQueryWrapper<>();
        scWrapper.eq(StudentCourse::getCourseId, courseId);
        List<StudentCourse> scList = studentCourseMapper.selectList(scWrapper);
        List<Long> selectedIds = scList.stream()
                .map(StudentCourse::getStudentId)
                .toList();

        // 所有学生
        LambdaQueryWrapper<Student> sWrapper = new LambdaQueryWrapper<>();
        if (!selectedIds.isEmpty()) {
            sWrapper.notIn(Student::getId, selectedIds);
        }
        return studentMapper.selectList(sWrapper);
    }

    // 批量添加学生到课程
    @PostMapping("/batch")
    public Map<String, Object> addStudents(@RequestBody Map<String, Object> payload) {
        Long courseId = Long.valueOf(payload.get("courseId").toString());
        List<Integer> studentIds = (List<Integer>) payload.get("studentIds");

        int successCount = 0;
        for (Integer sid : studentIds) {
            StudentCourse sc = new StudentCourse();
            sc.setStudentId(Long.valueOf(sid));
            sc.setCourseId(courseId);
            try {
                studentCourseMapper.insert(sc);
                successCount++;
            } catch (Exception e) {
                // 已存在则跳过
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "成功添加 " + successCount + " 名学生");
        return result;
    }

    // 从课程移除学生
    @DeleteMapping("/course/{courseId}/student/{studentId}")
    public Map<String, String> removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentCourse::getCourseId, courseId)
                .eq(StudentCourse::getStudentId, studentId);
        studentCourseMapper.delete(wrapper);

        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "移除成功");
        return result;
    }
}