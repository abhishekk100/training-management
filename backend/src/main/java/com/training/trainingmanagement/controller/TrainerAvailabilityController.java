package com.training.trainingmanagement.controller;

import com.training.trainingmanagement.dto.TrainerAvailabilityDTO;
import com.training.trainingmanagement.response.ApiResponse;
import com.training.trainingmanagement.service.TrainerAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class TrainerAvailabilityController {

    private final TrainerAvailabilityService availabilityService;

    public TrainerAvailabilityController(TrainerAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // 1. Get all availability records for all trainers
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TrainerAvailabilityDTO>>> getAllAvailability() {
        List<TrainerAvailabilityDTO> allAvailability = availabilityService.getAllAvailability();
        return ResponseEntity.ok(new ApiResponse<>(true, "All availability fetched successfully", allAvailability));
    }

    // 2. Get availability for a specific trainer
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<ApiResponse<List<TrainerAvailabilityDTO>>> getAvailabilityByTrainer(@PathVariable Integer trainerId) {
        List<TrainerAvailabilityDTO> availability = availabilityService.getAvailabilityByTrainer(trainerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability fetched successfully", availability));
    }

    // 3. Add availability for a trainer
    @PostMapping("/trainer/{trainerId}")
    public ResponseEntity<ApiResponse<TrainerAvailabilityDTO>> addAvailability(
            @PathVariable Integer trainerId,
            @RequestBody TrainerAvailabilityDTO availabilityDto) {

    	TrainerAvailabilityDTO availDto = availabilityService.addAvailability(trainerId, availabilityDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability added successfully", availDto));
    }

    // 4. Delete a trainer's availability
    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(@PathVariable Integer availabilityId) {
        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability deleted successfully", null));
    }
    
}
