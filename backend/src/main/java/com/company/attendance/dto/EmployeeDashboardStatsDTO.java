package com.company.attendance.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardStatsDTO {
    private String todayStatus; // "NOT_PUNCHED_IN", "PUNCHED_IN", "PUNCHED_OUT"
    private String punchInTime;
    private String punchOutTime;
    private String assignedSiteName;
    private Double siteLatitude;
    private Double siteLongitude;
    private Integer geofenceRadius;
    private String workingDuration; // e.g. "4h 30m"
}
