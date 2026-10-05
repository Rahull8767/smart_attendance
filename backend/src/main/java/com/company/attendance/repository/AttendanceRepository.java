package com.company.attendance.repository;

import com.company.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {
    Optional<AttendanceRecord> findTopByEmployeeIdAndPunchInTimeBetweenOrderByPunchInTimeDesc(UUID employeeId, LocalDateTime start, LocalDateTime end);
    List<AttendanceRecord> findByEmployeeIdOrderByPunchInTimeDesc(UUID employeeId);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT a.employeeId) FROM AttendanceRecord a WHERE a.providerId = :providerId AND a.punchInTime >= :startOfDay")
    Long countDistinctEmployeesPunchedInSince(@org.springframework.data.repository.query.Param("providerId") UUID providerId, @org.springframework.data.repository.query.Param("startOfDay") LocalDateTime startOfDay);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.providerId = :providerId AND a.faceVerificationStatus = :status")
    Long countFaceVerificationStatus(@org.springframework.data.repository.query.Param("providerId") UUID providerId, @org.springframework.data.repository.query.Param("status") String status);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.providerId = :providerId AND a.locationVerificationStatus = :status")
    Long countLocationVerificationStatus(@org.springframework.data.repository.query.Param("providerId") UUID providerId, @org.springframework.data.repository.query.Param("status") String status);
}
