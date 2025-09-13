package com.training.trainingmanagement.controller;

import com.training.trainingmanagement.dto.TrainerDTO;
import com.training.trainingmanagement.entity.Trainer;
import com.training.trainingmanagement.response.ApiResponse;
import com.training.trainingmanagement.service.TrainerAvailabilityService;
import com.training.trainingmanagement.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerAvailabilityService availabilityService;

    public TrainerController(TrainerService trainerService, TrainerAvailabilityService availabilityService) {
        this.trainerService = trainerService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TrainerDTO>>> getAllTrainers() {
        List<TrainerDTO> trainers = trainerService.getAllTrainers().stream()
        		.map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "All trainers fetched successfully", trainers));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<TrainerDTO>> getTrainerById(@PathVariable Integer id) {
        return trainerService.getTrainerById(id)
                .map(trainer -> ResponseEntity.ok(new ApiResponse<>(true, "Trainer fetched successfully", mapToDTO(trainer))))
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Trainer not found with id " + id, null)));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TrainerDTO>> createTrainer(@RequestBody TrainerDTO trDto) {
    	Trainer trainer = new Trainer();
    	trainer.setName(trDto.getName());
    	trainer.setLocation(trDto.getLocation());
    	
    	TrainerDTO dto = mapToDTO(trainerService.saveTrainer(trainer));
        return ResponseEntity.status(200)
                .body(new ApiResponse<>(true, "Trainer created successfully", dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<TrainerDTO>> updateTrainer(@PathVariable Integer id, @RequestBody TrainerDTO trainer) {
        return trainerService.getTrainerById(id)
                .map(existing -> {
                    existing.setName(trainer.getName());
                    existing.setLocation(trainer.getLocation());
                    TrainerDTO updated = mapToDTO(trainerService.saveTrainer(existing));
                    return ResponseEntity.ok(new ApiResponse<>(true, "Trainer updated successfully", updated));
                })
                .orElse(ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Trainer not found with id " + id, null)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrainer(@PathVariable Integer id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Trainer deleted successfully", null));
    }
    
    private TrainerDTO mapToDTO(Trainer trainer) {
    	TrainerDTO dto = new TrainerDTO();
    	dto.setId(trainer.getId());
    	dto.setName(trainer.getName());
    	dto.setLocation(trainer.getLocation());
    	dto.setAvailability(trainer.getAvailability().stream().map(a -> availabilityService.mapToDTO(a))
    			.collect(Collectors.toList()));
    	return dto;
    }
}
