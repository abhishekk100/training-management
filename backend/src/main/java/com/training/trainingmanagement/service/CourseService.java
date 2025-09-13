package com.training.trainingmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.training.trainingmanagement.entity.Course;
import com.training.trainingmanagement.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Create or Update Course
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get course by id
    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(id);
    }

    // Delete course by id
    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }
}
