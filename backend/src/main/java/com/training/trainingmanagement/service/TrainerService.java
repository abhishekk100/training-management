package com.training.trainingmanagement.service;

import com.training.trainingmanagement.entity.Trainer;
import com.training.trainingmanagement.entity.TrainerAvailability;
import com.training.trainingmanagement.repository.TrainerAvailabilityRepository;
import com.training.trainingmanagement.repository.TrainerRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

	private final TrainerRepository trainerRepository;
	private final TrainerAvailabilityRepository availabilityRepository;

	public TrainerService(TrainerRepository trainerRepository, TrainerAvailabilityRepository availabilityRepository) {
		this.trainerRepository = trainerRepository;
		this.availabilityRepository = availabilityRepository;
	}

	public List<Trainer> getAllTrainers() {
		return trainerRepository.findAll();
	}

	public Optional<Trainer> getTrainerById(Integer id) {
		return trainerRepository.findById(id);
	}

	@Transactional
	public Trainer saveTrainer(Trainer trainer) {
		return trainerRepository.save(trainer);
	}

	@Transactional
	public void deleteTrainer(Integer id) {
		trainerRepository.deleteById(id);
	}

	@Transactional
	public TrainerAvailability findAndReserveAvailability(Integer trainerId, String location, LocalDate date, String timeSlot) {

	    TrainerAvailability availability = availabilityRepository
	            .findFirstByTrainer_IdAndTrainer_LocationAndAvailableDateAndTimeSlot(
	                    trainerId, location, date, timeSlot)
	            .orElseThrow(() -> new RuntimeException("No available slot found for trainer " + trainerId));
	    availability.setStatus("No"); // Mark reserved
	    return availabilityRepository.saveAndFlush(availability);
	}


}
