package com.company.attendance.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CeoDashboardStatsDTO {
    private long totalEmployees;
    private long presentToday;
    private long absentToday;
    private long lateToday;
    private int attendancePercentage;
    private long activeSites;
    private List<ActiveEmployeeLocation> activeLocations;
    private long onField;
    private double verificationRate;
    private String companyName;
    private List<RecentFieldActivityDTO> recentActivity;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActiveEmployeeLocation {
        private String employeeName;
        private String siteName;
        private Double latitude;
        private Double longitude;
        private String punchInTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentFieldActivityDTO {
        private String employeeName;
        private String action;
        private String time;
        private String siteName;
        private boolean faceVerified;
        private boolean locationVerified;
    }
}
