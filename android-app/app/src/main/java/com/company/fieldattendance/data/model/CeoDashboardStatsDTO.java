package com.company.fieldattendance.data.model;

import java.util.List;

public class CeoDashboardStatsDTO {
    public long totalEmployees;
    public long presentToday;
    public long absentToday;
    public long lateToday;
    public int attendancePercentage;
    public long activeSites;
    public List<ActiveEmployeeLocation> activeLocations;
    
    public static class ActiveEmployeeLocation {
        public String employeeName;
        public String siteName;
        public Double latitude;
        public Double longitude;
        public String punchInTime;
    }
}
