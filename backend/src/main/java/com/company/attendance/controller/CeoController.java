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
    private PasswordEncoder passwordEncoder;

    // --- Provider Management ---

    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getAllProviders() {
        return ResponseEntity.ok(providerRepository.findAll());
    }

    @PostMapping("/providers")
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        provider.setStatus("ACTIVE");
        return ResponseEntity.ok(providerRepository.save(provider));
    }

    // --- Work Site Management ---

    @GetMapping("/sites")
    public ResponseEntity<List<WorkSite>> getAllWorkSites() {
        return ResponseEntity.ok(workSiteRepository.findAll());
    }

    @PostMapping("/sites")
    public ResponseEntity<WorkSite> createWorkSite(@RequestBody WorkSite workSite) {
        workSite.setStatus("ACTIVE");
        return ResponseEntity.ok(workSiteRepository.save(workSite));
    }

    // --- Employee Management ---

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, String> request) {
        try {
            UUID providerId = UUID.fromString(request.get("providerId"));
            UUID siteId = request.containsKey("siteId") ? UUID.fromString(request.get("siteId")) : null;

            User user = new User();
            user.setProviderId(providerId);
            user.setRole("ROLE_EMPLOYEE");
            user.setEmail(request.get("email"));
            user.setPasswordHash(passwordEncoder.encode(request.get("password")));
            user = userRepository.save(user);

            Employee emp = new Employee();
            emp.setUserId(user.getId());
            emp.setProviderId(providerId);
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
}
