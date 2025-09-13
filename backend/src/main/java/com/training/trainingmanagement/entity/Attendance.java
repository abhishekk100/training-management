package com.training.trainingmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@Table(name ="attendance")
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name ="attendance_date")
	private LocalDate attendanceDate;

	@Enumerated(EnumType.STRING)
	@Column(name ="status")
	private Status status;

	@ManyToOne
	@JoinColumn(name = "trainee_id", nullable = false)
	@JsonBackReference
	private Trainee trainee;

	@ManyToOne
	@JoinColumn(name = "batch_id", nullable = false)
	@JsonBackReference
	private Batch batch;

	public enum Status {
		PRESENT, ABSENT
	}
}
