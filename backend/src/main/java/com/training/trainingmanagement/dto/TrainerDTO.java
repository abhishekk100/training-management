package com.training.trainingmanagement.dto;

import java.util.List;
import lombok.Data;

@Data
public class TrainerDTO {
	private Integer id;
	private String name;
	private String location;
	private List<TrainerAvailabilityDTO> availability;
}
