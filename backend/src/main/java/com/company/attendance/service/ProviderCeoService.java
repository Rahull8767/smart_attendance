package com.company.attendance.service;

import com.company.attendance.dto.CeoDetailsResponse;
import com.company.attendance.dto.CeoResponse;
import com.company.attendance.dto.CreateCeoRequest;
import com.company.attendance.entity.Ceo;

import java.util.List;
import java.util.UUID;

public interface ProviderCeoService {
    List<CeoResponse> getCeosByProvider(UUID providerId);
    CeoDetailsResponse getCeoDetails(UUID providerId, UUID ceoId);
    Ceo createCeo(UUID providerId, CreateCeoRequest request);
    Ceo updateCeo(UUID providerId, UUID ceoId, CreateCeoRequest request);
    Ceo updateCeoStatus(UUID providerId, UUID ceoId, String status);
    com.company.attendance.dto.ProviderDashboardResponse getDashboardMetrics(UUID providerId);
}
