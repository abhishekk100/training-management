package com.training.trainingmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
@Table(name ="trainee")
public class Trainee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name")
	private String name;
	
   @Column(name = "email", nullable = false, unique = true)
    private String email;
	
   @ManyToOne
	@JoinColumn(name = "batch_id", nullable = false)
	@JsonBackReference
	private Batch batch;

	@OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Attendance> attendanceRecords;
}
