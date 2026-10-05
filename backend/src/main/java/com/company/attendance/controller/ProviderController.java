package com.company.attendance.controller;

import com.company.attendance.entity.Employee;
import com.company.attendance.entity.User;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getMyEmployees(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(employeeRepository.findByProviderId(providerUser.getProviderId()));
    }

    @Autowired
    private com.company.attendance.repository.CeoRepository ceoRepository;
    
    @Autowired
    private com.company.attendance.repository.WorkSiteRepository workSiteRepository;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<com.company.attendance.dto.ProviderDashboardStatsDTO> getDashboardStats(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElse(null);
        if (providerUser == null) return ResponseEntity.badRequest().build();
        
        java.util.UUID providerId = providerUser.getProviderId();
        
        long totalCeos = ceoRepository.findByProviderId(providerId).size();
        long activeCeos = ceoRepository.findByProviderId(providerId).stream().filter(c -> "ACTIVE".equals(c.getStatus())).count();
        long totalEmployees = employeeRepository.findByProviderId(providerId).size();
        long totalSites = workSiteRepository.findByProviderId(providerId).size();
        
        return ResponseEntity.ok(new com.company.attendance.dto.ProviderDashboardStatsDTO(
            totalCeos, activeCeos, totalEmployees, totalSites
        ));
    }
}
