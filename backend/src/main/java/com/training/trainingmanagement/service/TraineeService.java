package com.training.trainingmanagement.service;

import com.training.trainingmanagement.dto.TraineeDTO;
import com.training.trainingmanagement.entity.Trainee;
import com.training.trainingmanagement.repository.TraineeRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeService {

    private final TraineeRepository traineeRepository;

    public TraineeService(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public List<TraineeDTO> getAllTrainees() {
        return traineeRepository.findAll()
        		.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<Trainee> getTraineeById(Integer id) {
        return traineeRepository.findById(id);
    }

    @Transactional
    public Trainee saveTrainee(Trainee trainee) {
        return traineeRepository.save(trainee);
    }
    
    @Transactional
    public void deleteTrainee(Integer id) {
        traineeRepository.deleteById(id);
    }

	public List<TraineeDTO> getTraineeByBatchId(Integer id) {
		return traineeRepository.findByBatchId(id)
		.stream().map(this::mapToDTO).collect(Collectors.toList());
	}
	
    public TraineeDTO mapToDTO(Trainee trainee) {
    	TraineeDTO dto = new TraineeDTO();
    	dto.setId(trainee.getId());
    	dto.setName(trainee.getName());
    	dto.setEmail(trainee.getEmail());
    	if(trainee.getBatch() != null) {    		
    		dto.setBatchId(trainee.getBatch().getId());
    		dto.setBatchName(trainee.getBatch().getName());
    		if(trainee.getBatch().getCourse() != null) {
    			dto.setCourseId(trainee.getBatch().getCourse().getId());
    			dto.setCourseName(trainee.getBatch().getCourse().getName());
    		}
    	}
    	return dto;
    	
    }
}
