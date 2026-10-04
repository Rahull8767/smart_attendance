package com.company.attendance.repository;

import com.company.attendance.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByCompanyId(UUID companyId);
}
