package com.company.attendance.repository;

import com.company.attendance.entity.Ceo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CeoRepository extends JpaRepository<Ceo, UUID> {
    Optional<Ceo> findByUserId(UUID userId);
    List<Ceo> findByProviderId(UUID providerId);
}
