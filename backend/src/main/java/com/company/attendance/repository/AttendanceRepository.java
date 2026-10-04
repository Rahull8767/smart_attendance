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
}
