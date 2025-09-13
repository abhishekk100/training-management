package com.training.trainingmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.training.trainingmanagement.entity.Course;
import com.training.trainingmanagement.response.ApiResponse;
import com.training.trainingmanagement.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(new ApiResponse<>(true, "All courses fetched successfully", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable Integer id) {
        return courseService.getCourseById(id)
                .map(course -> ResponseEntity.ok(new ApiResponse<>(true, "Course fetched successfully", course)))
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Course not found with id " + id, null)));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        Course saved = courseService.saveCourse(course);
        return ResponseEntity.status(200).body(new ApiResponse<>(true, "Course created successfully", saved));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Integer id, @RequestBody Course updatedCourse) {
        return courseService.getCourseById(id)
                .map(existing -> {
                    existing.setName(updatedCourse.getName());
                    existing.setDescription(updatedCourse.getDescription());
                    Course saved = courseService.saveCourse(existing);
                    return ResponseEntity.ok(new ApiResponse<>(true, "Course updated successfully", saved));
                })
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Course not found with id " + id, null)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Course deleted successfully", null));
    }
}
