package com.company.attendance.controller;

import com.company.attendance.repository.AttendanceRepository;
import com.company.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/dashboard/{providerId}")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@PathVariable UUID providerId) {
        long totalEmployees = employeeRepository.findByProviderId(providerId).size();
        
        // In a real app, query attendance records for today specifically by provider ID.
        // For MVP, returning mocked aggregate stats.
        long presentToday = totalEmployees > 0 ? totalEmployees - 1 : 0; 
        long absentToday = totalEmployees > 0 ? 1 : 0;
        
        return ResponseEntity.ok(Map.of(
            "totalEmployees", totalEmployees,
            "presentToday", presentToday,
            "absentToday", absentToday,
            "lateCount", 0
        ));
    }
}
