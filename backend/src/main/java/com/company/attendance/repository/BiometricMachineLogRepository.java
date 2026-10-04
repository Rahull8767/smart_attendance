package com.company.attendance.repository;

import com.company.attendance.entity.BiometricMachineLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BiometricMachineLogRepository extends JpaRepository<BiometricMachineLog, UUID> {
}
