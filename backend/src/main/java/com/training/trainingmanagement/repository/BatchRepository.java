package com.training.trainingmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.trainingmanagement.entity.Batch;

public interface BatchRepository extends JpaRepository<Batch, Integer> {

}
