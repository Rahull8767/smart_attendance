package com.company.attendance.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDashboardStatsDTO {
    private long totalCeos;
    private long activeCeos;
    private long totalEmployees;
    private long totalSites;
}
