package com.company.attendance.controller;

import com.company.attendance.entity.AttendanceRecord;
import com.company.attendance.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/export/csv/{employeeId}")
    public ResponseEntity<byte[]> exportAttendanceCsv(@PathVariable UUID employeeId) {
        List<AttendanceRecord> records = attendanceRepository.findByEmployeeIdOrderByPunchInTimeDesc(employeeId);
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Punch In,Punch Out,Status,Location Verified\n");
        
        for (AttendanceRecord r : records) {
            csv.append(r.getId()).append(",")
               .append(r.getPunchInTime()).append(",")
               .append(r.getPunchOutTime() != null ? r.getPunchOutTime() : "").append(",")
               .append(r.getFaceVerificationStatus()).append(",")
               .append(r.getLocationVerificationStatus()).append("\n");
        }
        
        byte[] csvBytes = csv.toString().getBytes();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "attendance_report.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
}
