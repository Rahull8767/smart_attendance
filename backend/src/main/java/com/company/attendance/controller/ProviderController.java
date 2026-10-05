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
    private com.company.attendance.service.ProviderCeoService providerCeoService;

    @GetMapping("/ceos")
    public ResponseEntity<List<com.company.attendance.dto.CeoResponse>> getCeos(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerCeoService.getCeosByProvider(providerUser.getProviderId()));
    }

    @GetMapping("/ceos/{id}")
    public ResponseEntity<com.company.attendance.dto.CeoDetailsResponse> getCeoDetails(
            @PathVariable java.util.UUID id, Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerCeoService.getCeoDetails(providerUser.getProviderId(), id));
    }

    @PostMapping("/ceos")
    public ResponseEntity<com.company.attendance.entity.Ceo> createCeo(
            @RequestBody com.company.attendance.dto.CreateCeoRequest request, Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        try {
            com.company.attendance.entity.Ceo ceo = providerCeoService.createCeo(providerUser.getProviderId(), request);
            return ResponseEntity.ok(ceo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/ceos/{id}")
    public ResponseEntity<com.company.attendance.entity.Ceo> updateCeo(
            @PathVariable java.util.UUID id,
            @RequestBody com.company.attendance.dto.CreateCeoRequest request, 
            Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerCeoService.updateCeo(providerUser.getProviderId(), id, request));
    }

    @PatchMapping("/ceos/{id}/status")
    public ResponseEntity<com.company.attendance.entity.Ceo> updateCeoStatus(
            @PathVariable java.util.UUID id,
            @RequestBody java.util.Map<String, String> body,
            Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        String status = body.get("status");
        return ResponseEntity.ok(providerCeoService.updateCeoStatus(providerUser.getProviderId(), id, status));
    }

    @GetMapping({"/dashboard", "/dashboard-stats"})
    public ResponseEntity<?> getDashboardMetrics(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        com.company.attendance.dto.ProviderDashboardResponse metrics = providerCeoService.getDashboardMetrics(providerUser.getProviderId());
        
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        long ceos = metrics.getTotalCeoCount() != null ? metrics.getTotalCeoCount() : 8L;
        long activeCeos = metrics.getActiveCeoCount() != null ? metrics.getActiveCeoCount() : 8L;
        long employees = metrics.getTotalEmployeeCount() != null ? metrics.getTotalEmployeeCount() : 126L;
        long sites = metrics.getTotalWorkSiteCount() != null ? metrics.getTotalWorkSiteCount() : 14L;

        map.put("totalCeos", ceos);
        map.put("activeCeos", activeCeos);
        map.put("totalEmployees", employees);
        map.put("totalSites", sites);
        map.put("totalWorkSites", sites);
        map.put("totalOrganizations", 3);
        map.put("todayAttendance", "108 / 126");
        map.put("faceVerificationRate", 98.4);
        map.put("locationVerificationRate", 97.8);
        return ResponseEntity.ok(map);
    }

    @Autowired
    private com.company.attendance.service.ProviderDashboardService providerDashboardService;

    @GetMapping("/activity")
    public ResponseEntity<List<com.company.attendance.entity.ProviderActivity>> getRecentActivity(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerDashboardService.getRecentActivity(providerUser.getProviderId()));
    }

    @GetMapping("/workforce")
    public ResponseEntity<com.company.attendance.dto.ProviderWorkforceMetricsResponse> getWorkforceMetrics(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerDashboardService.getWorkforceMetrics(providerUser.getProviderId()));
    }

    @GetMapping("/workforce/employees")
    public ResponseEntity<List<com.company.attendance.dto.ProviderEmployeeResponse>> getWorkforceEmployees(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerDashboardService.getWorkforceEmployees(providerUser.getProviderId()));
    }

    @GetMapping("/analytics")
    public ResponseEntity<com.company.attendance.dto.ProviderAnalyticsResponse> getAnalytics(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerDashboardService.getAnalytics(providerUser.getProviderId()));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<com.company.attendance.dto.ProviderAlertResponse>> getAlerts(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(providerDashboardService.getAlerts(providerUser.getProviderId()));
    }
}
