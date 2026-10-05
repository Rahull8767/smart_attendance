package com.company.attendance.controller;

import com.company.attendance.entity.Employee;
import com.company.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        employee.setStatus("ACTIVE");
        return ResponseEntity.ok(employeeRepository.save(employee));
    }
    
    @PutMapping("/{id}/assign-site")
    public ResponseEntity<Employee> assignSite(@PathVariable UUID id, @RequestParam UUID siteId) {
        return employeeRepository.findById(id).map(emp -> {
            emp.setAssignedSiteId(siteId);
            return ResponseEntity.ok(employeeRepository.save(emp));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Autowired
    private com.company.attendance.repository.AttendanceRepository attendanceRepository;
    
    @Autowired
    private com.company.attendance.repository.WorkSiteRepository workSiteRepository;
    
    @Autowired
    private com.company.attendance.repository.UserRepository userRepository;

    @Autowired
    private com.company.attendance.repository.FaceProfileRepository faceProfileRepository;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<com.company.attendance.dto.EmployeeDashboardStatsDTO> getDashboardStats(org.springframework.security.core.Authentication auth) {
        String email = auth.getName();
        com.company.attendance.entity.User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        
        Employee emp = employeeRepository.findByUserId(user.getId());
        if (emp == null) return ResponseEntity.badRequest().build();
        
        String assignedSiteName = "Apex Tower Construction Site";
        String siteAddress = "Nagpur, Maharashtra, India";
        Double siteLat = 21.1458;
        Double siteLng = 79.0882;
        Double siteAlt = 312.0;
        Integer geoRadius = 150;
        UUID assignedSiteId = emp.getAssignedSiteId();
        
        if (emp.getAssignedSiteId() != null) {
            com.company.attendance.entity.WorkSite site = workSiteRepository.findById(emp.getAssignedSiteId()).orElse(null);
            if (site != null) {
                assignedSiteName = site.getName();
                if (site.getAddress() != null) siteAddress = site.getAddress();
                if (site.getLatitude() != null) siteLat = site.getLatitude();
                if (site.getLongitude() != null) siteLng = site.getLongitude();
                if (site.getAltitude() != null) siteAlt = site.getAltitude();
                if (site.getGeofenceRadius() != null) geoRadius = site.getGeofenceRadius();
            }
        }
        
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MIN);
        java.time.LocalDateTime endOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MAX);
        
        com.company.attendance.entity.AttendanceRecord record = attendanceRepository
                .findTopByEmployeeIdAndPunchInTimeBetweenOrderByPunchInTimeDesc(emp.getId(), startOfDay, endOfDay)
                .orElse(null);
                
        String status = "NOT PUNCHED IN";
        String inTime = "--:--";
        String outTime = "--:--";
        String duration = "0h 0m";
        
        if (record != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
            inTime = record.getPunchInTime().format(formatter);
            if (record.getPunchOutTime() == null) {
                status = "PUNCHED IN";
                java.time.Duration d = java.time.Duration.between(record.getPunchInTime(), java.time.LocalDateTime.now());
                duration = d.toHours() + "h " + (d.toMinutes() % 60) + "m";
            } else {
                status = "PUNCHED OUT";
                outTime = record.getPunchOutTime().format(formatter);
                java.time.Duration d = java.time.Duration.between(record.getPunchInTime(), record.getPunchOutTime());
                duration = d.toHours() + "h " + (d.toMinutes() % 60) + "m";
            }
        }
        
        boolean faceEnrolled = faceProfileRepository.findByEmployeeId(emp.getId()).isPresent();
        
        return ResponseEntity.ok(new com.company.attendance.dto.EmployeeDashboardStatsDTO(
            status, inTime, outTime, assignedSiteName, siteLat, siteLng, geoRadius, duration,
            siteAddress, siteAlt, emp.getEmployeeCode(), emp.getName(), emp.getDepartment(),
            emp.getDesignation(), faceEnrolled, assignedSiteId
        ));
    }
}
