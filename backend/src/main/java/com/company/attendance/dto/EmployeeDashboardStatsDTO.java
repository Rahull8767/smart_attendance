package com.company.attendance.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardStatsDTO {
    private String todayStatus; // "NOT PUNCHED IN", "PUNCHED IN", "PUNCHED OUT"
    private String punchInTime;
    private String punchOutTime;
    private String assignedSiteName;
    private Double siteLatitude;
    private Double siteLongitude;
    private Integer geofenceRadius;
    private String workingDuration; // e.g. "4h 30m"
    private String siteAddress;
    private Double siteAltitude;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private boolean isFaceEnrolled;
    private UUID assignedSiteId;
}
