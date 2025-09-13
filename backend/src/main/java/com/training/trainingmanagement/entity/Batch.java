package com.training.trainingmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
@Table(name ="batch")
public class Batch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@Column(name = "location", nullable = false)
	private String location;
	
	@Column(name = "scheduled_date", nullable = false)
	private LocalDate scheduledDate;

    @Column(name = "time_slot", nullable = false, length = 50)
    private String timeSlot;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
	@ManyToOne
	@JoinColumn(name = "trainer_id", nullable = false)
	@JsonBackReference
	private Trainer trainer;

	@OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Trainee> trainees;

	@OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Attendance> attendanceRecords;
}
