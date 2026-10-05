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
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkSiteRepository workSiteRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private com.company.attendance.service.FaceVerificationService faceVerificationService;

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
        Double siteAlt = site.getAltitude() != null ? site.getAltitude() : 312.0;
        Double altDiff = (request.getAltitude() != null) ? Math.abs(request.getAltitude() - siteAlt) : 0.0;

        LocationVerificationResponse response = new LocationVerificationResponse();
        response.setCalculatedDistance(Math.round(distance * 10.0) / 10.0);
        response.setAllowedRadius(radius);
        response.setSiteLatitude(site.getLatitude());
        response.setSiteLongitude(site.getLongitude());
        response.setSiteAltitude(siteAlt);
        response.setAltitudeDifference(Math.round(altDiff * 10.0) / 10.0);
        response.setSiteName(site.getName());

        if (distance <= radius) {
            response.setVerified(true);
            response.setStatus("VERIFIED");
            response.setMessage("Location verified successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setVerified(false);
            response.setStatus("OUTSIDE_GEOFENCE");
            response.setMessage("You are " + (int)distance + " meters away from the site. Allowed: " + radius + "m");
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/punch-in")
    public ResponseEntity<?> punchIn(@RequestBody PunchRequest request) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        
        Optional<AttendanceRecord> existingPunch = attendanceRepository.findTopByEmployeeIdAndPunchInTimeBetweenOrderByPunchInTimeDesc(
                request.getEmployeeId(), startOfDay, endOfDay);
                
        if (existingPunch.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Punch-in already recorded for today."));
        }
        
        Optional<Employee> employeeOpt = employeeRepository.findById(request.getEmployeeId());
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Employee not found."));
        }
        
        Employee employee = employeeOpt.get();
        
        // Perform Face Verification
        if (request.getFaceEmbedding() != null && !request.getFaceEmbedding().isEmpty()) {
            try {
                boolean isFaceVerified = faceVerificationService.verifyFace(request.getEmployeeId(), request.getFaceEmbedding());
                if (!isFaceVerified) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Face verification failed. Face does not match profile."));
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Face verification error: " + e.getMessage()));
            }
        } else {
             return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Face embedding is required for punch in."));
        }
        
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employee.getId());
        record.setProviderId(employee.getProviderId());
        record.setCeoId(employee.getCeoId());
        record.setWorkSiteId(employee.getAssignedSiteId());
        record.setPunchInTime(LocalDateTime.now());
        record.setPunchInLatitude(request.getLatitude());
        record.setPunchInLongitude(request.getLongitude());
        record.setPunchInAltitude(request.getAltitude());
        record.setPunchInAccuracy(request.getAccuracy());
        record.setPunchInLocationTimestamp(LocalDateTime.now());
        record.setFaceVerificationStatus("VERIFIED");
        record.setLocationVerificationStatus("VERIFIED"); 
        
        attendanceRepository.save(record);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Punch-in successful"));
    }
    
    @PostMapping("/punch-out")
    public ResponseEntity<?> punchOut(@RequestBody PunchRequest request) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        
        Optional<AttendanceRecord> existingRecordOpt = attendanceRepository.findTopByEmployeeIdAndPunchInTimeBetweenOrderByPunchInTimeDesc(
                request.getEmployeeId(), startOfDay, endOfDay);
                
        if (existingRecordOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Cannot punch out without a punch-in."));
        }
        
        AttendanceRecord record = existingRecordOpt.get();
        if (record.getPunchOutTime() != null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Punch-out already recorded for today."));
        }
        
        record.setPunchOutTime(LocalDateTime.now());
        record.setPunchOutLatitude(request.getLatitude());
        record.setPunchOutLongitude(request.getLongitude());
        record.setPunchOutAltitude(request.getAltitude());
        record.setPunchOutAccuracy(request.getAccuracy());
        record.setPunchOutLocationTimestamp(LocalDateTime.now());
        
        attendanceRepository.save(record);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Punch-out successful"));
    }
}
