package com.company.attendance.controller;

import com.company.attendance.entity.AttendanceRecord;
import com.company.attendance.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance/history")
public class HistoryController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<AttendanceRecord>> getHistory(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(attendanceRepository.findByEmployeeIdOrderByPunchInTimeDesc(employeeId));
    }
}
