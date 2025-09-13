package com.training.trainingmanagement.service;

import com.training.trainingmanagement.dto.AttendanceDTO;
import com.training.trainingmanagement.entity.Attendance;
import com.training.trainingmanagement.entity.Batch;
import com.training.trainingmanagement.entity.Trainee;
import com.training.trainingmanagement.repository.AttendanceRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	private final BatchService batchService;
	private final TraineeService traineeService;
	public AttendanceService(AttendanceRepository attendanceRepository, BatchService batchService, TraineeService traineeService) {
		this.attendanceRepository = attendanceRepository;
		this.batchService = batchService;
		this.traineeService = traineeService;
	}

	public List<AttendanceDTO> getAllAttendance() {
		return attendanceRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}
	
	public List<AttendanceDTO> getAttendanceByBatchId(Integer id) {
		return attendanceRepository.findByBatchId(id)
				.stream().map(this::mapToDTO).collect(Collectors.toList());
	}
	public Optional<Attendance> getAttendanceById(Integer id) {
		return attendanceRepository.findById(id);
	}

	@Transactional
	public AttendanceDTO saveAttendance(AttendanceDTO dto) {
		Attendance attendance = new Attendance();
		Batch batch = batchService.getBatchById(dto.getBatchId()).orElse(null);
		if(batch == null) {	
            throw new IllegalArgumentException("Batch not found with id: " + dto.getBatchId());
		}
		Trainee trainee = traineeService.getTraineeById(dto.getTraineeId()).orElse(null);
		if(trainee == null) {
            throw new IllegalArgumentException("Trainee not found with id: " + dto.getTraineeId());
		}
		attendance.setStatus(
			    "ABSENT".equalsIgnoreCase(dto.getStatus())
			        ? Attendance.Status.ABSENT
			        : Attendance.Status.PRESENT
			);

		attendance.setId(dto.getId());
		attendance.setBatch(batch);
		attendance.setTrainee(trainee);
		attendance.setAttendanceDate(dto.getAttendanceDate());
		return mapToDTO(attendanceRepository.save(attendance));
	}

	@Transactional
	public void deleteAttendance(Integer id) {
		attendanceRepository.deleteById(id);
	}

	public AttendanceDTO mapToDTO(Attendance attd) {
		AttendanceDTO dto = new AttendanceDTO();
		dto.setId(attd.getId());
		dto.setAttendanceDate(attd.getAttendanceDate());
		dto.setStatus(attd.getStatus().name());
		if (attd.getBatch() != null) {
			dto.setBatchId(attd.getBatch().getId());
			dto.setBatchName(attd.getBatch().getName());
		}
		if (attd.getTrainee() != null) {
			dto.setTraineeId(attd.getTrainee().getId());
			dto.setTraineeName(attd.getTrainee().getName());
			dto.setTraineeEmail(attd.getTrainee().getEmail());
		}
		if (attd.getBatch().getCourse() != null) {
			dto.setCourseId(attd.getBatch().getCourse().getId());
			dto.setCourseName(attd.getBatch().getCourse().getName());
		}
		return dto;
	}
}
