package com.training.trainingmanagement.dto;

import lombok.Data;

@Data
public class TraineeDTO {
	
	private Integer id;
	private String name;
	private String email;
	private Integer batchId;
	private String batchName;
	private Integer courseId;
	private String courseName;

}
