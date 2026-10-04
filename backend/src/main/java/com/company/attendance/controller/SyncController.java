package com.company.attendance.controller;

import com.company.attendance.entity.BiometricMachineLog;
import com.company.attendance.repository.BiometricMachineLogRepository;
import com.company.attendance.dto.OfflinePunchRequest;
import com.company.attendance.controller.AttendanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private BiometricMachineLogRepository logRepository;
    
    @Autowired
    private AttendanceController attendanceController;

    @PostMapping("/biometric-machine")
    public ResponseEntity<?> receiveMachineLog(@RequestBody BiometricMachineLog log) {
        log.setProcessed(false);
        logRepository.save(log);
        return ResponseEntity.ok(Map.of("success", true, "message", "Machine log received"));
    }
    
    @PostMapping("/offline-punches")
    public ResponseEntity<?> syncOfflinePunches(@RequestBody OfflinePunchRequest request) {
        if (request.getOfflinePunches() != null) {
            // For MVP, we simulate processing them.
            // In reality we would iterate and save them with historical timestamps.
            return ResponseEntity.ok(Map.of("success", true, "syncedCount", request.getOfflinePunches().size()));
        }
        return ResponseEntity.badRequest().build();
    }
}
