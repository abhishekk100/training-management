package com.training.trainingmanagement.controller;

import com.training.trainingmanagement.dto.AttendanceDTO;
import com.training.trainingmanagement.dto.BatchDTO;
import com.training.trainingmanagement.dto.TraineeDTO;
import com.training.trainingmanagement.entity.Batch;
import com.training.trainingmanagement.entity.Course;
import com.training.trainingmanagement.entity.Trainer;
import com.training.trainingmanagement.response.ApiResponse;
import com.training.trainingmanagement.service.AttendanceService;
import com.training.trainingmanagement.service.BatchService;
import com.training.trainingmanagement.service.CourseService;
import com.training.trainingmanagement.service.TraineeService;
import com.training.trainingmanagement.service.TrainerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

	private final BatchService batchService;
	private final TrainerService trainerService;
	private final CourseService courseService;
	private final AttendanceService attendanceService;
	private final TraineeService traineeService;
	public BatchController(BatchService batchService, TrainerService trainerService, CourseService courseService, 
			AttendanceService attendanceService, TraineeService traineeService) {
		this.batchService = batchService;
		this.trainerService = trainerService;
		this.courseService = courseService;
		this.attendanceService = attendanceService;
		this.traineeService = traineeService;
	}

	@GetMapping("/list")
	public ResponseEntity<ApiResponse<List<BatchDTO>>> getAllBatches() {
		List<BatchDTO> batches = batchService.getAllBatches().stream().map(this::mapToDTO).collect(Collectors.toList());
		return ResponseEntity.ok(new ApiResponse<>(true, "All batches fetched successfully", batches));
	}
	
	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<List<BatchDTO>>> getAllBatcheSummary() {
		List<BatchDTO> batches = batchService.getAllBatches().stream().map(b -> {
			BatchDTO d = mapToDTO(b);
			// get all trainees and there attendance records
//			List<TraineeDTO> traineeDTOs  = traineeService.getTraineeByBatchId(b.getId());
			List<TraineeDTO> traineeDTOs = b.getTrainees().stream().map(t -> traineeService.mapToDTO(t))
					.collect(Collectors.toList());
			d.setTrainees(traineeDTOs);
//			List<AttendanceDTO> attendanceDTOs = attendanceService.getAllAttendance();
			List<AttendanceDTO> attendanceDTOs = b.getAttendanceRecords().stream()
					.map(a -> attendanceService.mapToDTO(a)).collect(Collectors.toList());
			d.setAttendance(attendanceDTOs);
			return d;
		}).collect(Collectors.toList());
		
		return ResponseEntity.ok(new ApiResponse<>(true, "All batches fetched successfully", batches));
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<BatchDTO>> createBatch(@RequestBody BatchDTO bDto) {
		// validate basic required fields
		if (bDto.getCourseId() == null || bDto.getCourseId() == 0) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "course is required", null));
		}
		if (bDto.getTrainerId() == null || bDto.getTrainerId() == 0) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "trainer is required", null));
		}
		if (bDto.getLocation() == null || bDto.getLocation().isBlank()) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "location is required", null));
		}
		if (bDto.getScheduledDate() == null) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "scheduled date is required", null));
		}
		if (bDto.getTimeSlot() == null || bDto.getTimeSlot().isBlank()) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "time slot is required", null));
		}

		// find course
		Course course = courseService.getCourseById(bDto.getCourseId()).orElse(null);
		if (course == null) {
			return ResponseEntity.status(404)
					.body(new ApiResponse<>(false, "Course not found with id " + bDto.getCourseId(), null));
		}
		Trainer trainer = trainerService.getTrainerById(bDto.getTrainerId()).orElse(null); 
		if (trainer == null) {
			return ResponseEntity.status(404)
					.body(new ApiResponse<>(false, "Trainer not found with id " + bDto.getTrainerId(), null));
		}
		// find & reserve trainer availability
		trainerService.findAndReserveAvailability(bDto.getLocation(),
				bDto.getScheduledDate(), bDto.getTimeSlot());

		// Save batch with assigned trainer
		Batch batch = new Batch();
		batch.setName(bDto.getName());
		batch.setCourse(course);
		batch.setLocation(bDto.getLocation());
		batch.setScheduledDate(bDto.getScheduledDate());
		batch.setTimeSlot(bDto.getTimeSlot());
		batch.setTrainer(trainer);

		// update
		if(bDto.getId() != null && bDto.getId() != 0) {
			batch.setId(bDto.getId());
		}
		
		Batch saved = batchService.saveBatch(batch);
		BatchDTO dto = mapToDTO(saved);
		return ResponseEntity.status(200).body(new ApiResponse<>(true, "Batch saved and trainer auto-assigned", dto));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteBatch(@PathVariable Integer id) {
		batchService.deleteBatch(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Batch deleted successfully", null));
	}

	private BatchDTO mapToDTO(Batch batch) {
		BatchDTO dto = new BatchDTO();
		dto.setId(batch.getId());
		dto.setName(batch.getName());
		dto.setLocation(batch.getLocation());
		dto.setScheduledDate(batch.getScheduledDate());
		dto.setTimeSlot(batch.getTimeSlot());

		if (batch.getTrainer() != null) {
			dto.setTrainerId(batch.getTrainer().getId());
			dto.setTrainerName(batch.getTrainer().getName());
		}
		if(batch.getCourse() != null) {
			dto.setCourseId(batch.getCourse().getId());
			dto.setCourseName(batch.getCourse().getName());
		}
		return dto;
	}

}
