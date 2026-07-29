package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.Course;
import com.example.backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/page")
    public Map<String, Object> queryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<Course> page = courseService.queryPage(pageNum, pageSize, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("records", page.getRecords());
        return result;
    }

    @GetMapping("/{id}")
    public Course getById(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    public Map<String, String> saveOrUpdate(@RequestBody Course course) {
        courseService.saveOrUpdate(course);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "操作成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteById(@PathVariable Long id) {
        courseService.deleteById(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "删除成功");
        return result;
    }
}
