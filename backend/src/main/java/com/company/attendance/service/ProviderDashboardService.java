package com.company.attendance.service;

import com.company.attendance.entity.ProviderActivity;
import java.util.List;
import java.util.UUID;

public interface ProviderDashboardService {
    List<ProviderActivity> getRecentActivity(UUID providerId);
    void logActivity(UUID providerId, String eventType, String entityType, UUID entityId, String title, String description);
    
    com.company.attendance.dto.ProviderWorkforceMetricsResponse getWorkforceMetrics(UUID providerId);
    List<com.company.attendance.dto.ProviderEmployeeResponse> getWorkforceEmployees(UUID providerId);
    
    com.company.attendance.dto.ProviderAnalyticsResponse getAnalytics(UUID providerId);
    List<com.company.attendance.dto.ProviderAlertResponse> getAlerts(UUID providerId);
}
