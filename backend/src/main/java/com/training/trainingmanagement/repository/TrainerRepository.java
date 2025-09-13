package com.training.trainingmanagement.repository;

import com.training.trainingmanagement.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {

}
