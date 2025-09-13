package com.training.trainingmanagement.dto;

import lombok.Data;

@Data
public class Availability {
	  private String trainerName;
	  private Integer availableDays;
	  private Integer occupiedDays;
}
