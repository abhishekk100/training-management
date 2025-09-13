package com.training.trainingmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.trainingmanagement.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer>{

}
