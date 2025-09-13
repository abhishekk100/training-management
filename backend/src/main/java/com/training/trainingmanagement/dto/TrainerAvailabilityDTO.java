package com.training.trainingmanagement.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TrainerAvailabilityDTO {
	private Integer id;
	private String timeSlot;
	private LocalDate availableDate;
	private Integer trainerId;
	private String trainerName;
	private String status;
}
