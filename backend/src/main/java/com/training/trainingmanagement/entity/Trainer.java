package com.training.trainingmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
@Table(name = "trainer")
public class Trainer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;
	@Column(name = "location", nullable = false)
	private String location;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<TrainerAvailability> availability;

	@OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Batch> batches;
}
