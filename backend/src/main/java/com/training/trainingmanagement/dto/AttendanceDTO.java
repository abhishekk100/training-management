package com.training.trainingmanagement.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AttendanceDTO {
	private Integer id;
	private LocalDate attendanceDate;
	private String status;
	private Integer batchId;
	private String batchName;
	private Integer courseId;
	private String courseName;
	private Integer traineeId;
	private String  traineeName;
	private String  traineeEmail;
}
