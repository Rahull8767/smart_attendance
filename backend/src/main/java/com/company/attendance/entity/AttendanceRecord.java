package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID employeeId;
    private UUID workSiteId;
    private UUID providerId; // Keep provider ID just in case
    private UUID ceoId;
    
    private LocalDateTime punchInTime;
    private LocalDateTime punchOutTime;
    
    private Double punchInLatitude;
    private Double punchInLongitude;
    private Double punchInAltitude;
    private Float punchInAccuracy;
    private LocalDateTime punchInLocationTimestamp;
    
    private Double punchOutLatitude;
    private Double punchOutLongitude;
    private Double punchOutAltitude;
    private Float punchOutAccuracy;
    private LocalDateTime punchOutLocationTimestamp;
    
    private String faceVerificationStatus; // "VERIFIED", "FAILED", "SKIPPED"
    private String locationVerificationStatus; // "VERIFIED", "OUTSIDE_GEOFENCE"
    
    private LocalDateTime verificationTimestamp;
    
    private Double distanceFromSite;
    private Double altitudeDifference;
    private Double faceSimilarityScore;
    private String failureReason;
    
    @Transient
    private String workSiteName;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
