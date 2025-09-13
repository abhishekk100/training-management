package com.training.trainingmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.trainingmanagement.entity.TrainerAvailability;

public interface TrainerAvailabilityRepository extends JpaRepository<TrainerAvailability, Integer>{
	List<TrainerAvailability> findByTrainerId(Integer trainerId);

    // Matches: TrainerAvailability.trainer.location, TrainerAvailability.availableDate, TrainerAvailability.timeSlot
    Optional<TrainerAvailability> findFirstByTrainer_LocationAndAvailableDateAndTimeSlot(
            String location,
            LocalDate availableDate,
            String timeSlot);}
