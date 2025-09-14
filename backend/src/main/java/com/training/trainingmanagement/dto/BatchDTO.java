package com.training.trainingmanagement.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class BatchDTO {
    private Integer id;
    private String name;
    private Integer courseId;
    private String courseName;
    private String location;
    private LocalDate scheduledDate;
    private String timeSlot;
    private Integer trainerId;
    private String trainerName;
    private Integer availabilityId;
    private List<TraineeDTO> trainees;
    private List<AttendanceDTO> attendance;
}
