import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

# Entities
create_file(f"{base_dir}/entity/Notification.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID userId;
    private String title;
    private String body;
    private String type;
    private Boolean readStatus = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
""")

create_file(f"{base_dir}/entity/AttendanceCorrection.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance_corrections")
public class AttendanceCorrection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID attendanceId; // Optional: Can be null if missing a punch-in completely
    private UUID employeeId;
    
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED
    
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();
    
    private UUID reviewedBy;
    private LocalDateTime reviewedAt;
    private String managerComment;
}
""")

# Repositories
create_file(f"{base_dir}/repository/NotificationRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
""")

create_file(f"{base_dir}/repository/AttendanceCorrectionRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.AttendanceCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, UUID> {
    List<AttendanceCorrection> findByEmployeeIdOrderByRequestedAtDesc(UUID employeeId);
}
""")

# Controllers
create_file(f"{base_dir}/controller/NotificationController.java", """
package com.company.attendance.controller;

import com.company.attendance.entity.Notification;
import com.company.attendance.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }
}
""")

create_file(f"{base_dir}/controller/HistoryController.java", """
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
        return ResponseEntity.ok(attendanceRepository.findByEmployeeIdOrderByServerTimestampDesc(employeeId));
    }
}
""")

create_file(f"{base_dir}/controller/CorrectionController.java", """
package com.company.attendance.controller;

import com.company.attendance.entity.AttendanceCorrection;
import com.company.attendance.repository.AttendanceCorrectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/corrections")
public class CorrectionController {

    @Autowired
    private AttendanceCorrectionRepository correctionRepository;

    @PostMapping("/request")
    public ResponseEntity<AttendanceCorrection> requestCorrection(@RequestBody AttendanceCorrection correction) {
        correction.setStatus("PENDING");
        return ResponseEntity.ok(correctionRepository.save(correction));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<AttendanceCorrection> reviewCorrection(@PathVariable UUID id, 
                                                                 @RequestParam String status, 
                                                                 @RequestParam UUID managerId,
                                                                 @RequestParam(required=false) String comment) {
        return correctionRepository.findById(id).map(correction -> {
            correction.setStatus(status);
            correction.setReviewedBy(managerId);
            correction.setReviewedAt(LocalDateTime.now());
            correction.setManagerComment(comment);
            return ResponseEntity.ok(correctionRepository.save(correction));
        }).orElse(ResponseEntity.notFound().build());
    }
}
""")

print("Phase 7 Backend script complete.")
