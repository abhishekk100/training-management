package com.training.trainingmanagement.controller;

import com.training.trainingmanagement.dto.AttendanceDTO;
import com.training.trainingmanagement.response.ApiResponse;
import com.training.trainingmanagement.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAllAttendance() {
        List<AttendanceDTO> allAttendance = attendanceService.getAllAttendance();
        return ResponseEntity.ok(new ApiResponse<>(true, "All attendance records fetched successfully", allAttendance));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AttendanceDTO>> createAttendance(@RequestBody AttendanceDTO attendance) {
    	AttendanceDTO saved = attendanceService.saveAttendance(attendance);
        return ResponseEntity.status(200).body(new ApiResponse<>(true, "Attendance marked successfully", saved));
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Integer id) {
    	attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance deleted successfully", null));
    }
}
