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

    @Autowired
    private com.company.attendance.repository.WorkSiteRepository workSiteRepository;

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<AttendanceRecord>> getHistory(@PathVariable UUID employeeId) {
        List<AttendanceRecord> records = attendanceRepository.findByEmployeeIdOrderByPunchInTimeDesc(employeeId);
        for (AttendanceRecord r : records) {
            if (r.getWorkSiteId() != null) {
                workSiteRepository.findById(r.getWorkSiteId()).ifPresent(s -> r.setWorkSiteName(s.getName()));
            }
        }
        return ResponseEntity.ok(records);
    }
}
