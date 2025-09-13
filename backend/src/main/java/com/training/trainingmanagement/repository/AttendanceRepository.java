package com.training.trainingmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.trainingmanagement.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

	List<Attendance> findByBatchId(Integer id);

}
