import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

create_file(f"{base_dir}/entity/AttendanceRecord.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID employeeId;
    private UUID companyId;
    private UUID siteId;
    
    private String punchType; // "PUNCH_IN", "PUNCH_OUT"
    private LocalDate attendanceDate;
    
    private Double latitude;
    private Double longitude;
    private Float locationAccuracy;
    
    private String faceVerificationStatus; // "VERIFIED", "FAILED", "SKIPPED"
    private String locationVerificationStatus; // "VERIFIED", "OUTSIDE_GEOFENCE", "MOCK_LOCATION"
    
    @Column(name = "server_timestamp", updatable = false)
    private LocalDateTime serverTimestamp = LocalDateTime.now();
}
""")

create_file(f"{base_dir}/repository/AttendanceRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {
    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDateAndPunchType(UUID employeeId, LocalDate date, String punchType);
    List<AttendanceRecord> findByEmployeeIdOrderByServerTimestampDesc(UUID employeeId);
}
""")

create_file(f"{base_dir}/dto/PunchRequest.java", """
package com.company.attendance.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PunchRequest {
    private UUID employeeId;
    private Double latitude;
    private Double longitude;
    private Float accuracy;
    private String faceStatus;
}
""")

# Overwriting AttendanceController to add punch-in and punch-out
create_file(f"{base_dir}/controller/AttendanceController.java", """
package com.company.attendance.controller;

import com.company.attendance.dto.LocationVerificationRequest;
import com.company.attendance.dto.LocationVerificationResponse;
import com.company.attendance.dto.PunchRequest;
import com.company.attendance.entity.AttendanceRecord;
import com.company.attendance.entity.Employee;
import com.company.attendance.entity.WorkSite;
import com.company.attendance.repository.AttendanceRepository;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.WorkSiteRepository;
import com.company.attendance.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkSiteRepository workSiteRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;

    @PostMapping("/verify-location")
    public ResponseEntity<LocationVerificationResponse> verifyLocation(@RequestBody LocationVerificationRequest request) {
        if (request.getIsMockLocation() != null && request.getIsMockLocation()) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "MOCK_LOCATION_SUSPECTED", "Mock location detected", 0));
        }
        if (request.getAccuracy() != null && request.getAccuracy() > 150) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "LOW_ACCURACY", "GPS accuracy is too low", 0));
        }

        Optional<Employee> employeeOpt = employeeRepository.findById(request.getEmployeeId());
        if (employeeOpt.isEmpty() || employeeOpt.get().getAssignedSiteId() == null) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "NO_SITE_ASSIGNED", "No assigned work site", 0));
        }

        Optional<WorkSite> siteOpt = workSiteRepository.findById(employeeOpt.get().getAssignedSiteId());
        if (siteOpt.isEmpty()) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "SITE_NOT_FOUND", "Assigned site not found", 0));
        }

        WorkSite site = siteOpt.get();
        if (site.getLatitude() == null || site.getLongitude() == null) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "SITE_CONFIG_ERROR", "Site coordinates are not configured", 0));
        }

        double distance = GeoUtils.calculateDistance(request.getLatitude(), request.getLongitude(), site.getLatitude(), site.getLongitude());
        int radius = site.getGeofenceRadius() != null ? site.getGeofenceRadius() : 100;

        if (distance <= radius) {
            return ResponseEntity.ok(new LocationVerificationResponse(true, "VERIFIED", "Location verified successfully", distance));
        } else {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "OUTSIDE_GEOFENCE", "You are " + (int)distance + " meters away from the site", distance));
        }
    }
    
    @PostMapping("/punch-in")
    public ResponseEntity<?> punchIn(@RequestBody PunchRequest request) {
        LocalDate today = LocalDate.now();
        
        Optional<AttendanceRecord> existingPunch = attendanceRepository.findByEmployeeIdAndAttendanceDateAndPunchType(
                request.getEmployeeId(), today, "PUNCH_IN");
                
        if (existingPunch.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Punch-in already recorded for today."));
        }
        
        Optional<Employee> employeeOpt = employeeRepository.findById(request.getEmployeeId());
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Employee not found."));
        }
        
        Employee employee = employeeOpt.get();
        
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employee.getId());
        record.setCompanyId(employee.getCompanyId());
        record.setSiteId(employee.getAssignedSiteId());
        record.setPunchType("PUNCH_IN");
        record.setAttendanceDate(today);
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setLocationAccuracy(request.getAccuracy());
        record.setFaceVerificationStatus(request.getFaceStatus());
        record.setLocationVerificationStatus("VERIFIED"); // Simplified for MVP
        
        attendanceRepository.save(record);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Punch-in successful"));
    }
    
    @PostMapping("/punch-out")
    public ResponseEntity<?> punchOut(@RequestBody PunchRequest request) {
        LocalDate today = LocalDate.now();
        
        Optional<AttendanceRecord> existingPunchIn = attendanceRepository.findByEmployeeIdAndAttendanceDateAndPunchType(
                request.getEmployeeId(), today, "PUNCH_IN");
                
        if (existingPunchIn.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Cannot punch out without a punch-in."));
        }
        
        Optional<AttendanceRecord> existingPunchOut = attendanceRepository.findByEmployeeIdAndAttendanceDateAndPunchType(
                request.getEmployeeId(), today, "PUNCH_OUT");
                
        if (existingPunchOut.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Punch-out already recorded for today."));
        }
        
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow();
        
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employee.getId());
        record.setCompanyId(employee.getCompanyId());
        record.setSiteId(employee.getAssignedSiteId());
        record.setPunchType("PUNCH_OUT");
        record.setAttendanceDate(today);
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setLocationAccuracy(request.getAccuracy());
        record.setFaceVerificationStatus(request.getFaceStatus());
        record.setLocationVerificationStatus("VERIFIED");
        
        attendanceRepository.save(record);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Punch-out successful"));
    }
}
""")

print("Phase 6 Backend script complete.")
