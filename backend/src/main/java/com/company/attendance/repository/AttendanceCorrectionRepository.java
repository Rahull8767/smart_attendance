package com.company.attendance.repository;

import com.company.attendance.entity.AttendanceCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, UUID> {
    List<AttendanceCorrection> findByEmployeeIdOrderByRequestedAtDesc(UUID employeeId);
}
