package com.company.attendance.repository;

import com.company.attendance.entity.ProviderActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderActivityRepository extends JpaRepository<ProviderActivity, UUID> {
    List<ProviderActivity> findTop10ByProviderIdOrderByCreatedAtDesc(UUID providerId);
}
