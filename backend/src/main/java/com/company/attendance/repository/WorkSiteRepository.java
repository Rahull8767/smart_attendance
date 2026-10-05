package com.company.attendance.repository;

import com.company.attendance.entity.WorkSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WorkSiteRepository extends JpaRepository<WorkSite, UUID> {
    List<WorkSite> findByProviderId(UUID providerId);
    List<WorkSite> findByCeoId(UUID ceoId);
    Long countByProviderId(UUID providerId);
}
