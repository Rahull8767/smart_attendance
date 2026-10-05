package com.company.attendance.service.impl;

import com.company.attendance.entity.ProviderActivity;
import com.company.attendance.repository.ProviderActivityRepository;
import com.company.attendance.service.ProviderDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProviderDashboardServiceImpl implements ProviderDashboardService {

    @Autowired
    private ProviderActivityRepository providerActivityRepository;

    @Override
    public List<ProviderActivity> getRecentActivity(UUID providerId) {
        return providerActivityRepository.findTop10ByProviderIdOrderByCreatedAtDesc(providerId);
    }

    @Override
    @Transactional
    public void logActivity(UUID providerId, String eventType, String entityType, UUID entityId, String title, String description) {
        ProviderActivity activity = new ProviderActivity();
        activity.setProviderId(providerId);
        activity.setEventType(eventType);
        activity.setEntityType(entityType);
        activity.setEntityId(entityId);
        activity.setTitle(title);
        activity.setDescription(description);
        providerActivityRepository.save(activity);
    }

    @Autowired
    private com.company.attendance.repository.EmployeeRepository employeeRepository;
    
    @Autowired
    private com.company.attendance.repository.CeoRepository ceoRepository;
    
    @Autowired
    private com.company.attendance.repository.OrganizationRepository organizationRepository;

    @Override
    public com.company.attendance.dto.ProviderWorkforceMetricsResponse getWorkforceMetrics(UUID providerId) {
        com.company.attendance.dto.ProviderWorkforceMetricsResponse response = new com.company.attendance.dto.ProviderWorkforceMetricsResponse();
        response.setTotalEmployees(employeeRepository.countByProviderId(providerId));
        response.setActiveEmployees(employeeRepository.countByProviderIdAndStatus(providerId, "ACTIVE"));
        response.setInactiveEmployees(employeeRepository.countByProviderIdAndStatus(providerId, "INACTIVE"));
        response.setOnLeaveEmployees(employeeRepository.countByProviderIdAndStatus(providerId, "ON_LEAVE"));
        return response;
    }
    
    @Override
    public List<com.company.attendance.dto.ProviderEmployeeResponse> getWorkforceEmployees(UUID providerId) {
        return employeeRepository.findByProviderId(providerId).stream().map(employee -> {
            com.company.attendance.dto.ProviderEmployeeResponse dto = new com.company.attendance.dto.ProviderEmployeeResponse();
            dto.setId(employee.getId());
            dto.setEmployeeCode(employee.getEmployeeCode());
            dto.setName(employee.getName());
            dto.setDepartment(employee.getDepartment());
            dto.setDesignation(employee.getDesignation());
            dto.setStatus(employee.getStatus());
            
            if (employee.getCeoId() != null) {
                ceoRepository.findById(employee.getCeoId()).ifPresent(ceo -> {
                    dto.setCeoName(ceo.getName());
                    if (ceo.getOrganizationId() != null) {
                        organizationRepository.findById(ceo.getOrganizationId())
                            .ifPresent(org -> dto.setOrganizationName(org.getName()));
                    }
                });
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Autowired
    private com.company.attendance.repository.AttendanceRepository attendanceRepository;

    @Override
    public com.company.attendance.dto.ProviderAnalyticsResponse getAnalytics(UUID providerId) {
        com.company.attendance.dto.ProviderAnalyticsResponse response = new com.company.attendance.dto.ProviderAnalyticsResponse();
        
        Long faceSuccess = attendanceRepository.countFaceVerificationStatus(providerId, "VERIFIED");
        Long faceFailed = attendanceRepository.countFaceVerificationStatus(providerId, "FAILED");
        response.setFaceVerificationSuccesses(faceSuccess != null ? faceSuccess : 0L);
        response.setFaceVerificationFailures(faceFailed != null ? faceFailed : 0L);
        
        Long locSuccess = attendanceRepository.countLocationVerificationStatus(providerId, "VERIFIED");
        Long locFailed = attendanceRepository.countLocationVerificationStatus(providerId, "OUTSIDE_GEOFENCE");
        response.setLocationVerificationSuccesses(locSuccess != null ? locSuccess : 0L);
        response.setLocationVerificationFailures(locFailed != null ? locFailed : 0L);
        
        response.setTotalPunchIns(response.getFaceVerificationSuccesses() + response.getFaceVerificationFailures());
        
        return response;
    }
    
    @Override
    public List<com.company.attendance.dto.ProviderAlertResponse> getAlerts(UUID providerId) {
        List<com.company.attendance.dto.ProviderAlertResponse> alerts = new java.util.ArrayList<>();
        
        com.company.attendance.dto.ProviderAlertResponse alert1 = new com.company.attendance.dto.ProviderAlertResponse();
        alert1.setId(UUID.randomUUID());
        alert1.setTitle("Verification failures spike");
        alert1.setDescription("Global Corp has experienced a 20% increase in face verification failures in the last 2 hours.");
        alert1.setSeverity("HIGH");
        alert1.setTimestamp(java.time.LocalDateTime.now().minusHours(2));
        alert1.setIsRead(false);
        
        com.company.attendance.dto.ProviderAlertResponse alert2 = new com.company.attendance.dto.ProviderAlertResponse();
        alert2.setId(UUID.randomUUID());
        alert2.setTitle("New CEO Onboarded");
        alert2.setDescription("Tech Solutions Inc. has successfully completed their onboarding process.");
        alert2.setSeverity("LOW");
        alert2.setTimestamp(java.time.LocalDateTime.now().minusDays(1));
        alert2.setIsRead(true);
        
        alerts.add(alert1);
        alerts.add(alert2);
        
        return alerts;
    }
}
