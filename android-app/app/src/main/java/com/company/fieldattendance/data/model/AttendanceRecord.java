package com.company.fieldattendance.data.model;

import java.util.UUID;

public class AttendanceRecord {
    public UUID id;
    public UUID employeeId;
    public UUID workSiteId;
    public UUID providerId;
    public UUID ceoId;
    public String punchInTime;
    public String punchOutTime;
    public Double punchInLatitude;
    public Double punchInLongitude;
    public Double punchInAltitude;
    public Float punchInAccuracy;
    public Double punchOutLatitude;
    public Double punchOutLongitude;
    public Double punchOutAltitude;
    public Float punchOutAccuracy;
    public String faceVerificationStatus;
    public String locationVerificationStatus;
    public Double distanceFromSite;
    public Double altitudeDifference;
    public String failureReason;
    public String workSiteName;
    public String employeeName;
    public String employeeCode;
}

