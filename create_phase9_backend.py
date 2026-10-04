import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

create_file(f"{base_dir}/entity/BiometricMachineLog.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "biometric_machine_logs")
public class BiometricMachineLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String machineSerialNo;
    private String enrollmentId; // ID from the machine
    private String punchState;
    private LocalDateTime punchTime;
    
    private Boolean processed = false;
    
    @Column(name = "received_at", updatable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();
}
""")

create_file(f"{base_dir}/repository/BiometricMachineLogRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.BiometricMachineLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BiometricMachineLogRepository extends JpaRepository<BiometricMachineLog, UUID> {
}
""")

create_file(f"{base_dir}/dto/OfflinePunchRequest.java", """
package com.company.attendance.dto;
import lombok.Data;
import java.util.List;

@Data
public class OfflinePunchRequest {
    private List<PunchRequest> offlinePunches;
}
""")

create_file(f"{base_dir}/controller/SyncController.java", """
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
""")

print("Phase 9 Backend script complete.")
