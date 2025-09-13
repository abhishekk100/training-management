package com.training.trainingmanagement.controller;

import com.training.trainingmanagement.dto.TraineeDTO;
import com.training.trainingmanagement.entity.Batch;
import com.training.trainingmanagement.entity.Trainee;
import com.training.trainingmanagement.response.ApiResponse;
import com.training.trainingmanagement.service.BatchService;
import com.training.trainingmanagement.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {
	private final BatchService batchService;

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService, BatchService batchService) {
        this.traineeService = traineeService;
        this.batchService = batchService;
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TraineeDTO>>> getAllTrainees() {
        List<TraineeDTO> trainees = traineeService.getAllTrainees();
        return ResponseEntity.ok(new ApiResponse<>(true, "All trainees fetched successfully", trainees));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TraineeDTO>> createTrainee(@RequestBody TraineeDTO dto) {

    	if(dto.getBatchId() == null || dto.getBatchId() == 0) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "batch is required", null));
    	}
    	Batch batch = batchService.getBatchById(dto.getBatchId()).orElse(null);
		if (batch == null) {
			return ResponseEntity.status(404)
					.body(new ApiResponse<>(false, "Batch not found with id " + dto.getBatchId(), null));
		}
		
    	Trainee trainee = new Trainee();
    	if(dto.getId() != null && dto.getId() != 0) {    		
    		trainee.setId(dto.getId());
    	}
    	trainee.setName(dto.getName());
    	trainee.setEmail(dto.getEmail());
		trainee.setBatch(batch);
    	TraineeDTO saved = traineeService.mapToDTO(traineeService.saveTrainee(trainee));
        return ResponseEntity.status(200).body(new ApiResponse<>(true, "Trainee created successfully", saved));
    }
    

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteTrainee(@PathVariable Integer id) {
		traineeService.deleteTrainee(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Trainee deleted successfully", null));
	}
	
}
