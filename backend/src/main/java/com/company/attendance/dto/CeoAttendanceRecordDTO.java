package com.company.attendance.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CeoAttendanceRecordDTO {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String employeeCode;
    private String department;
    private String designation;
    private UUID workSiteId;
    private String workSiteName;
    private String punchInTime;
    private String punchOutTime;
    private String status; // "PRESENT" or "PUNCHED OUT"
    private String faceVerificationStatus;
    private String locationVerificationStatus;
    private Double latitude;
    private Double longitude;
}
