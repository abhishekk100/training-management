package com.training.trainingmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.trainingmanagement.entity.Trainee;

public interface TraineeRepository extends JpaRepository<Trainee, Integer>{

	List<Trainee> findByBatchId(Integer id);

}
