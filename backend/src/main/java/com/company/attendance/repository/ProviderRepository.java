package com.company.attendance.repository;

import com.company.attendance.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProviderRepository extends JpaRepository<Provider, UUID> {
}
