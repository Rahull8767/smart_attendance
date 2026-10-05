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

    @GetMapping("/dashboard-stats")
    public ResponseEntity<com.company.attendance.dto.CeoDashboardStatsDTO> getDashboardStats(Authentication auth) {
        com.company.attendance.entity.Ceo ceo = getAuthenticatedCeo(auth);
        
        long totalEmployees = employeeRepository.findByCeoId(ceo.getId()).size();
        long activeSites = workSiteRepository.findByCeoId(ceo.getId()).stream().filter(s -> "ACTIVE".equals(s.getStatus())).count();
        
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MIN);
        java.time.LocalDateTime endOfDay = java.time.LocalDateTime.now().with(java.time.LocalTime.MAX);
        
        // Find all punches for today for this CEO's employees
        // Since attendance table doesn't have ceo_id mapped in a dedicated repository method yet, we fetch employees and filter
        // Wait, we added ceoId to AttendanceRecord! Let's just use it safely if repository supports it. 
        // Or we can just get all employees and map. Let's do it simply:
        List<Employee> emps = employeeRepository.findByCeoId(ceo.getId());
        List<java.util.UUID> empIds = emps.stream().map(Employee::getId).collect(java.util.stream.Collectors.toList());
        
        long presentToday = 0;
        long lateToday = 0;
        List<com.company.attendance.dto.CeoDashboardStatsDTO.ActiveEmployeeLocation> activeLocs = new java.util.ArrayList<>();
        
        if (!empIds.isEmpty()) {
            List<com.company.attendance.entity.AttendanceRecord> records = attendanceRepository.findAll().stream()
                .filter(r -> empIds.contains(r.getEmployeeId()))
                .filter(r -> r.getPunchInTime().isAfter(startOfDay) && r.getPunchInTime().isBefore(endOfDay))
                .collect(java.util.stream.Collectors.toList());
                
            presentToday = records.size();
            // Just simulate late based on time > 10:00 AM
            lateToday = records.stream().filter(r -> r.getPunchInTime().getHour() >= 10).count();
            
            // Map live locations
            for (com.company.attendance.entity.AttendanceRecord r : records) {
                if (r.getPunchOutTime() == null) {
                    Employee e = emps.stream().filter(emp -> emp.getId().equals(r.getEmployeeId())).findFirst().orElse(null);
                    WorkSite s = workSiteRepository.findById(r.getWorkSiteId()).orElse(null);
                    if (e != null && s != null) {
                        activeLocs.add(new com.company.attendance.dto.CeoDashboardStatsDTO.ActiveEmployeeLocation(
                            e.getName(),
                            s.getName(),
                            r.getPunchInLatitude(),
                            r.getPunchInLongitude(),
                            r.getPunchInTime().toString()
                        ));
                    }
                }
            }
        }
        
        long absentToday = totalEmployees - presentToday;
        int pct = totalEmployees > 0 ? (int)((presentToday * 100.0) / totalEmployees) : 0;
        
        return ResponseEntity.ok(new com.company.attendance.dto.CeoDashboardStatsDTO(
            totalEmployees, presentToday, absentToday, lateToday, pct, activeSites, activeLocs
        ));
    }
}
