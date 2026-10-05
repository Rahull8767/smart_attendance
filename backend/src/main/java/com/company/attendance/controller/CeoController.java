package com.company.attendance.controller;

import com.company.attendance.entity.Employee;
import com.company.attendance.entity.Provider;
import com.company.attendance.entity.User;
import com.company.attendance.entity.WorkSite;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.ProviderRepository;
import com.company.attendance.repository.UserRepository;
import com.company.attendance.repository.WorkSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.company.attendance.repository.CeoRepository;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/ceo")
public class CeoController {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkSiteRepository workSiteRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CeoRepository ceoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private com.company.attendance.entity.Ceo getAuthenticatedCeo(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return ceoRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("CEO profile not found"));
    }

    // --- Work Site Management ---

    @GetMapping("/sites")
    public ResponseEntity<List<WorkSite>> getAllWorkSites(Authentication auth) {
        com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
        return ResponseEntity.ok(workSiteRepository.findByCeoId(ceo.getId()));
    }

    @PostMapping("/sites")
    public ResponseEntity<WorkSite> createWorkSite(@RequestBody WorkSite workSite, Authentication auth) {
        com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
        workSite.setProviderId(ceo.getProviderId());
        workSite.setCeoId(ceo.getId());
        workSite.setStatus("ACTIVE");
        return ResponseEntity.ok(workSiteRepository.save(workSite));
    }

    // --- Employee Management ---

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(Authentication auth) {
        com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
        return ResponseEntity.ok(employeeRepository.findByCeoId(ceo.getId()));
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, String> request, Authentication auth) {
        try {
            com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
            UUID providerId = ceo.getProviderId();
            UUID siteId = (request.containsKey("siteId") && request.get("siteId") != null && !request.get("siteId").isEmpty()) 
                ? UUID.fromString(request.get("siteId")) : null;

            User user = new User();
            user.setProviderId(providerId);
            user.setRole("ROLE_EMPLOYEE");
            user.setEmail(request.get("email"));
            user.setPasswordHash(passwordEncoder.encode(request.get("password")));
            user = userRepository.save(user);

            Employee emp = new Employee();
            emp.setUserId(user.getId());
            emp.setProviderId(providerId);
            emp.setCeoId(ceo.getId());
            emp.setAssignedSiteId(siteId);
            emp.setEmployeeCode(request.get("employeeCode"));
            emp.setName(request.get("name"));
            emp.setDepartment(request.get("department"));
            emp.setDesignation(request.get("designation"));
            emp.setStatus("ACTIVE");
            
            return ResponseEntity.ok(employeeRepository.save(emp));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Autowired
    private com.company.attendance.repository.AttendanceRepository attendanceRepository;

    @Autowired
    private com.company.attendance.repository.OrganizationRepository organizationRepository;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<com.company.attendance.dto.CeoDashboardStatsDTO> getDashboardStats(Authentication auth) {
        com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
        
        List<Employee> emps = employeeRepository.findByCeoId(ceo.getId());
        long totalEmployees = emps.size();
        long activeSites = workSiteRepository.findByCeoId(ceo.getId()).stream().filter(s -> "ACTIVE".equals(s.getStatus())).count();
        
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MIN);
        java.time.LocalDateTime endOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MAX);
        
        List<UUID> empIds = emps.stream().map(Employee::getId).collect(java.util.stream.Collectors.toList());
        
        long presentToday = 0;
        long lateToday = 0;
        long onField = 0;
        List<com.company.attendance.dto.CeoDashboardStatsDTO.ActiveEmployeeLocation> activeLocs = new java.util.ArrayList<>();
        List<com.company.attendance.dto.CeoDashboardStatsDTO.RecentFieldActivityDTO> recentActivity = new java.util.ArrayList<>();
        
        if (!empIds.isEmpty()) {
            List<com.company.attendance.entity.AttendanceRecord> records = attendanceRepository.findAll().stream()
                .filter(r -> r.getEmployeeId() != null && empIds.contains(r.getEmployeeId()))
                .filter(r -> r.getPunchInTime() != null && r.getPunchInTime().isAfter(startOfDay) && r.getPunchInTime().isBefore(endOfDay))
                .sorted((a, b) -> b.getPunchInTime().compareTo(a.getPunchInTime()))
                .collect(java.util.stream.Collectors.toList());
                
            presentToday = records.size();
            lateToday = records.stream().filter(r -> r.getPunchInTime().getHour() >= 10).count();
            
            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
            
            for (com.company.attendance.entity.AttendanceRecord r : records) {
                Employee e = emps.stream().filter(emp -> emp.getId().equals(r.getEmployeeId())).findFirst().orElse(null);
                WorkSite s = (r.getWorkSiteId() != null) ? workSiteRepository.findById(r.getWorkSiteId()).orElse(null) : null;
                String empName = (e != null) ? e.getName() : "Employee";
                String siteName = (s != null) ? s.getName() : "Field Site";
                
                if (r.getPunchOutTime() == null) {
                    onField++;
                    if (r.getPunchInLatitude() != null && r.getPunchInLongitude() != null) {
                        activeLocs.add(new com.company.attendance.dto.CeoDashboardStatsDTO.ActiveEmployeeLocation(
                            empName, siteName, r.getPunchInLatitude(), r.getPunchInLongitude(), r.getPunchInTime().toString()
                        ));
                    }
                }
                
                recentActivity.add(new com.company.attendance.dto.CeoDashboardStatsDTO.RecentFieldActivityDTO(
                    empName,
                    (r.getPunchOutTime() == null) ? "Punch In" : "Punch Out",
                    (r.getPunchOutTime() == null) ? r.getPunchInTime().format(timeFmt) : r.getPunchOutTime().format(timeFmt),
                    siteName,
                    "VERIFIED".equalsIgnoreCase(r.getFaceVerificationStatus()),
                    "VERIFIED".equalsIgnoreCase(r.getLocationVerificationStatus())
                ));
            }
        }
        
        long absentToday = Math.max(0, totalEmployees - presentToday);
        int pct = totalEmployees > 0 ? (int)((presentToday * 100.0) / totalEmployees) : 0;
        
        String orgName = "Apex Infrastructure Pvt. Ltd.";
        if (ceo.getOrganizationId() != null) {
            com.company.attendance.entity.Organization org = organizationRepository.findById(ceo.getOrganizationId()).orElse(null);
            if (org != null && org.getName() != null) {
                orgName = org.getName();
            }
        }
        
        return ResponseEntity.ok(new com.company.attendance.dto.CeoDashboardStatsDTO(
            totalEmployees, presentToday, absentToday, lateToday, pct, activeSites, activeLocs,
            onField, 98.2, orgName, recentActivity
        ));
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<com.company.attendance.dto.CeoAttendanceRecordDTO>> getCeoAttendance(Authentication auth) {
        com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
        List<Employee> emps = employeeRepository.findByCeoId(ceo.getId());
        List<UUID> empIds = emps.stream().map(Employee::getId).collect(java.util.stream.Collectors.toList());
        
        List<com.company.attendance.dto.CeoAttendanceRecordDTO> dtoList = new java.util.ArrayList<>();
        if (empIds.isEmpty()) {
            return ResponseEntity.ok(dtoList);
        }
        
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MIN);
        java.time.LocalDateTime endOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MAX);
        
        List<com.company.attendance.entity.AttendanceRecord> records = attendanceRepository.findAll().stream()
            .filter(r -> r.getEmployeeId() != null && empIds.contains(r.getEmployeeId()))
            .filter(r -> r.getPunchInTime() != null && r.getPunchInTime().isAfter(startOfDay) && r.getPunchInTime().isBefore(endOfDay))
            .sorted((a, b) -> b.getPunchInTime().compareTo(a.getPunchInTime()))
            .collect(java.util.stream.Collectors.toList());
            
        java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
        
        for (com.company.attendance.entity.AttendanceRecord r : records) {
            Employee e = emps.stream().filter(emp -> emp.getId().equals(r.getEmployeeId())).findFirst().orElse(null);
            WorkSite s = (r.getWorkSiteId() != null) ? workSiteRepository.findById(r.getWorkSiteId()).orElse(null) : null;
            
            String inTime = r.getPunchInTime() != null ? r.getPunchInTime().format(timeFmt) : "--:--";
            String outTime = r.getPunchOutTime() != null ? r.getPunchOutTime().format(timeFmt) : null;
            String status = (r.getPunchOutTime() == null) ? "PRESENT" : "PUNCHED OUT";
            
            dtoList.add(new com.company.attendance.dto.CeoAttendanceRecordDTO(
                r.getId(),
                r.getEmployeeId(),
                e != null ? e.getName() : "Unknown",
                e != null ? e.getEmployeeCode() : "EMP-000",
                e != null ? e.getDepartment() : "General",
                e != null ? e.getDesignation() : "Staff",
                r.getWorkSiteId(),
                s != null ? s.getName() : "Work Site",
                inTime,
                outTime,
                status,
                r.getFaceVerificationStatus() != null ? r.getFaceVerificationStatus() : "VERIFIED",
                r.getLocationVerificationStatus() != null ? r.getLocationVerificationStatus() : "VERIFIED",
                r.getPunchInLatitude(),
                r.getPunchInLongitude()
            ));
        }
        
        return ResponseEntity.ok(dtoList);
    }
}
