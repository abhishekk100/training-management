package com.training.trainingmanagement.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "trainer_availability")
public class TrainerAvailability {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "time_slot", nullable = false)
	private String timeSlot;
	
	@Column(name = "available_date", nullable = false)
	private LocalDate availableDate;

	@Column(name ="status")
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "trainer_id", nullable = false)
	@JsonBackReference
	private Trainer trainer;
}
