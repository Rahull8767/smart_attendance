package com.company.attendance.repository;

import com.company.attendance.entity.FaceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FaceProfileRepository extends JpaRepository<FaceProfile, UUID> {
    Optional<FaceProfile> findByEmployeeId(UUID employeeId);
}
