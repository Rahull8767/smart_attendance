package com.company.attendance.repository;

import com.company.attendance.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByProviderId(UUID providerId);
    Employee findByUserId(UUID userId);
}
