package com.training.trainingmanagement.service;

import com.training.trainingmanagement.dto.TrainerAvailabilityDTO;
import com.training.trainingmanagement.entity.Trainer;
import com.training.trainingmanagement.entity.TrainerAvailability;
import com.training.trainingmanagement.repository.TrainerAvailabilityRepository;
import com.training.trainingmanagement.repository.TrainerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerAvailabilityService {

    private final TrainerAvailabilityRepository availabilityRepository;
    private final TrainerRepository trainerRepository;

    public TrainerAvailabilityService(TrainerAvailabilityRepository availabilityRepository,
                                      TrainerRepository trainerRepository) {
        this.availabilityRepository = availabilityRepository;
        this.trainerRepository = trainerRepository;
    }

    // Get all availability
    public List<TrainerAvailabilityDTO> getAllAvailability() {
        return availabilityRepository.findAll()
        		.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get availability for a specific trainer
    public List<TrainerAvailabilityDTO> getAvailabilityByTrainer(Integer trainerId) {
        return availabilityRepository.findByTrainerId(trainerId)
        		.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Add availability for a trainer
    public TrainerAvailabilityDTO addAvailability(Integer trainerId, TrainerAvailabilityDTO availabilityDto) {
        Optional<Trainer> trainerOpt = trainerRepository.findById(trainerId);
        if (trainerOpt.isEmpty()) {
            throw new IllegalArgumentException("Trainer not found with id: " + trainerId);
        }
        TrainerAvailability avail = new TrainerAvailability();
        avail.setId(availabilityDto.getId());
        avail.setAvailableDate(availabilityDto.getAvailableDate());
        avail.setTimeSlot(availabilityDto.getTimeSlot());
        avail.setTrainer(trainerOpt.get());
        avail.setStatus(availabilityDto.getStatus());
        return mapToDTO(availabilityRepository.save(avail));
    }

    // Delete availability
    public void deleteAvailability(Integer availabilityId) {
        availabilityRepository.deleteById(availabilityId);
    }
    
    public TrainerAvailabilityDTO mapToDTO(TrainerAvailability availability) {
    	TrainerAvailabilityDTO dto = new TrainerAvailabilityDTO();
    	dto.setId(availability.getId());
    	dto.setAvailableDate(availability.getAvailableDate());
    	dto.setTimeSlot(availability.getTimeSlot());
    	dto.setStatus(availability.getStatus());
    	if(availability.getTrainer() != null) {
    		dto.setTrainerId(availability.getTrainer().getId());
    		dto.setTrainerName(availability.getTrainer().getName());
    	}
    	return dto;
    }
}
