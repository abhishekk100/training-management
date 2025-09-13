package com.training.trainingmanagement.controller;

import com.training.trainingmanagement.dto.AttendanceDTO;
import com.training.trainingmanagement.dto.Availability;
import com.training.trainingmanagement.dto.Enrollment;
import com.training.trainingmanagement.dto.Trend;
import com.training.trainingmanagement.entity.Batch;
import com.training.trainingmanagement.entity.Trainer;
import com.training.trainingmanagement.service.AttendanceService;
import com.training.trainingmanagement.service.BatchService;
import com.training.trainingmanagement.service.TrainerService;
import com.training.trainingmanagement.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	private final TrainerService trainerService;
	private final BatchService batchService;
	private final AttendanceService attendanceService;

	public ReportController(TrainerService trainerService, BatchService batchService,
			AttendanceService attendanceService) {
		this.trainerService = trainerService;
		this.batchService = batchService;
		this.attendanceService = attendanceService;
	}

	// 1️⃣ Trainer Availability vs Occupancy
	@GetMapping("/trainer-availability-occupancy")
	public ApiResponse<List<Availability>> getTrainerAvailabilityVsOccupancy() {
		List<Trainer> trainers = trainerService.getAllTrainers();
		List<Availability> result = new ArrayList<>();
	    LocalDate today = LocalDate.now();

		for (Trainer t : trainers) {
			// Count available slots (future)
			int availableSlots = Optional.ofNullable(t.getAvailability().stream()
					.filter(a->a.getStatus().equalsIgnoreCase("yes")).toList())
					.orElse(Collections.emptyList()).size();

	        // Count occupied slots = number of batches assigned (today or future)
	        int occupiedSlots = Optional.ofNullable(t.getBatches())
	                .orElse(Collections.emptyList())
	                .stream()
	                .filter(b -> b.getScheduledDate() != null && !b.getScheduledDate().isBefore(today))
	                .toList()
	                .size();

			Availability avail = new Availability();
			avail.setTrainerName(t.getName());
			avail.setAvailableDays(availableSlots);
			avail.setOccupiedDays(occupiedSlots);

			result.add(avail);
		}

		return new ApiResponse<>(true, "Trainer availability vs occupancy fetched", result);
	}

	// 2️⃣ Batch Enrollments
	@GetMapping("/batch-enrollments")
	public ApiResponse<List<Enrollment>> getBatchEnrollments() {
		List<Batch> batches = batchService.getAllBatches();
		List<Enrollment> result = batches.stream().map(b -> {
			Enrollment enrl = new Enrollment();
			int traineeCount = Optional.ofNullable(b.getTrainees()).orElse(Collections.emptyList()).size();
			enrl.setLabel(b.getName());
			enrl.setTraineeCount(traineeCount);
			return enrl;
		}).collect(Collectors.toList());

		return new ApiResponse<>(true, "Batch enrollments fetched", result);
	}

	// 3️⃣ Attendance Trends
	@GetMapping("/attendance-trends")
	public ApiResponse<List<Trend>> getAttendanceTrends() {
		List<AttendanceDTO> allAttendance = attendanceService.getAllAttendance();

		Map<String, Long> grouped = allAttendance.stream().collect(Collectors
				.groupingBy(a -> a.getAttendanceDate().format(DateTimeFormatter.ISO_DATE), Collectors.counting()));

		List<Trend> result = grouped.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> {
			Trend trend = new Trend();
			trend.setDate(e.getKey());
			trend.setPresentCount(e.getValue().intValue());
			return trend;
		}).collect(Collectors.toList());

		return new ApiResponse<>(true, "Attendance trends fetched", result);
	}
}
