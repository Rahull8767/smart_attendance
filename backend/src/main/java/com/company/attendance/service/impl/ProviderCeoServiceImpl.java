package com.company.attendance.service.impl;

import com.company.attendance.dto.CeoDetailsResponse;
import com.company.attendance.dto.CeoResponse;
import com.company.attendance.dto.CreateCeoRequest;
import com.company.attendance.entity.Ceo;
import com.company.attendance.entity.Organization;
import com.company.attendance.entity.User;
import com.company.attendance.repository.CeoRepository;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.OrganizationRepository;
import com.company.attendance.repository.UserRepository;
import com.company.attendance.repository.WorkSiteRepository;
import com.company.attendance.service.ProviderCeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProviderCeoServiceImpl implements ProviderCeoService {

    @Autowired
    private CeoRepository ceoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkSiteRepository workSiteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<CeoResponse> getCeosByProvider(UUID providerId) {
        return ceoRepository.findByProviderId(providerId).stream().map(ceo -> {
            CeoResponse response = new CeoResponse();
            response.setId(ceo.getId());
            response.setName(ceo.getName());
            response.setStatus(ceo.getStatus());
            response.setLastActive(ceo.getUpdatedAt());
            
            if (ceo.getOrganizationId() != null) {
                organizationRepository.findById(ceo.getOrganizationId())
                    .ifPresent(org -> response.setCompanyName(org.getName()));
            }
            
            response.setEmployeeCount(employeeRepository.findByCeoId(ceo.getId()).size());
            response.setSiteCount(workSiteRepository.findByCeoId(ceo.getId()).size());
            
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public CeoDetailsResponse getCeoDetails(UUID providerId, UUID ceoId) {
        Ceo ceo = ceoRepository.findById(ceoId)
            .filter(c -> c.getProviderId().equals(providerId))
            .orElseThrow(() -> new RuntimeException("CEO not found or unauthorized"));

        CeoDetailsResponse response = new CeoDetailsResponse();
        response.setId(ceo.getId());
        response.setName(ceo.getName());
        response.setEmail(ceo.getEmail());
        response.setPhone(ceo.getPhone());
        response.setDesignation(ceo.getDesignation());
        response.setStatus(ceo.getStatus());
        response.setCreatedAt(ceo.getCreatedAt());

        if (ceo.getOrganizationId() != null) {
            organizationRepository.findById(ceo.getOrganizationId()).ifPresent(org -> {
                response.setOrganizationId(org.getId());
                response.setCompanyName(org.getName());
                response.setIndustry(org.getIndustry());
                response.setCompanyEmail(org.getEmail());
                response.setCompanyPhone(org.getPhone());
            });
        }

        response.setEmployeeCount(employeeRepository.findByCeoId(ceo.getId()).size());
        response.setSiteCount(workSiteRepository.findByCeoId(ceo.getId()).size());

        return response;
    }

    @Autowired
    private com.company.attendance.service.ProviderDashboardService providerDashboardService;

    @Override
    @Transactional
    public Ceo createCeo(UUID providerId, CreateCeoRequest request) {
        if (userRepository.findByEmail(request.getLoginEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Create Organization
        Organization org = new Organization();
        org.setName(request.getCompanyName());
        org.setIndustry(request.getIndustry());
        org.setEmail(request.getCompanyEmail());
        org.setPhone(request.getCompanyPhone());
        org.setAddress(request.getAddress());
        org.setCity(request.getCity());
        org.setState(request.getState());
        org.setPostalCode(request.getPostalCode());
        org = organizationRepository.save(org);
        
        providerDashboardService.logActivity(providerId, "CREATE_ORGANIZATION", "ORGANIZATION", org.getId(), "Organization created", request.getCompanyName() + " was created.");

        // Create User (Account)
        User user = new User();
        user.setEmail(request.getLoginEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_CEO");
        user.setProviderId(providerId);
        user = userRepository.save(user);

        // Create CEO
        Ceo ceo = new Ceo();
        ceo.setUserId(user.getId());
        ceo.setProviderId(providerId);
        ceo.setOrganizationId(org.getId());
        ceo.setName(request.getName());
        ceo.setEmail(request.getEmail());
        ceo.setPhone(request.getPhone());
        ceo.setDesignation(request.getDesignation());
        ceo.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        
        Ceo savedCeo = ceoRepository.save(ceo);
        
        providerDashboardService.logActivity(providerId, "CREATE_CEO", "CEO", savedCeo.getId(), "CEO created", request.getName() + " was registered.");
        
        return savedCeo;
    }

    @Override
    @Transactional
    public Ceo updateCeo(UUID providerId, UUID ceoId, CreateCeoRequest request) {
        Ceo ceo = ceoRepository.findById(ceoId)
            .filter(c -> c.getProviderId().equals(providerId))
            .orElseThrow(() -> new RuntimeException("CEO not found or unauthorized"));

        ceo.setName(request.getName());
        ceo.setEmail(request.getEmail());
        ceo.setPhone(request.getPhone());
        ceo.setDesignation(request.getDesignation());
        ceo.setUpdatedAt(LocalDateTime.now());
        
        if (ceo.getOrganizationId() != null) {
            organizationRepository.findById(ceo.getOrganizationId()).ifPresent(org -> {
                org.setName(request.getCompanyName());
                org.setIndustry(request.getIndustry());
                org.setEmail(request.getCompanyEmail());
                org.setPhone(request.getCompanyPhone());
                org.setAddress(request.getAddress());
                org.setCity(request.getCity());
                org.setState(request.getState());
                org.setPostalCode(request.getPostalCode());
                organizationRepository.save(org);
            });
        }
        
        return ceoRepository.save(ceo);
    }

    @Override
    @Transactional
    public Ceo updateCeoStatus(UUID providerId, UUID ceoId, String status) {
        Ceo ceo = ceoRepository.findById(ceoId)
            .filter(c -> c.getProviderId().equals(providerId))
            .orElseThrow(() -> new RuntimeException("CEO not found or unauthorized"));
            
        ceo.setStatus(status);
        ceo.setUpdatedAt(LocalDateTime.now());
        Ceo savedCeo = ceoRepository.save(ceo);
        
        providerDashboardService.logActivity(providerId, "UPDATE_CEO_STATUS", "CEO", savedCeo.getId(), "CEO " + ("ACTIVE".equals(status) ? "activated" : "deactivated"), ceo.getName() + " status changed to " + status + ".");
        
        return savedCeo;
    }
    @Autowired
    private com.company.attendance.repository.AttendanceRepository attendanceRepository;

    @Override
    public com.company.attendance.dto.ProviderDashboardResponse getDashboardMetrics(UUID providerId) {
        com.company.attendance.dto.ProviderDashboardResponse response = new com.company.attendance.dto.ProviderDashboardResponse();
        
        response.setTotalCeoCount(ceoRepository.countByProviderId(providerId));
        response.setActiveCeoCount(ceoRepository.countByProviderIdAndStatus(providerId, "ACTIVE"));
        response.setInactiveCeoCount(ceoRepository.countByProviderIdAndStatus(providerId, "INACTIVE"));
        
        response.setTotalEmployeeCount(employeeRepository.countByProviderId(providerId));
        response.setActiveEmployeeCount(employeeRepository.countByProviderIdAndStatus(providerId, "ACTIVE"));
        
        response.setTotalWorkSiteCount(workSiteRepository.countByProviderId(providerId));
        
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long employeesPunchedIn = attendanceRepository.countDistinctEmployeesPunchedInSince(providerId, startOfDay);
        
        if (response.getActiveEmployeeCount() != null && response.getActiveEmployeeCount() > 0) {
            response.setTodayAttendanceRate((employeesPunchedIn * 100.0) / response.getActiveEmployeeCount());
        } else {
            response.setTodayAttendanceRate(null);
        }
        
        Long faceSuccess = attendanceRepository.countFaceVerificationStatus(providerId, "VERIFIED");
        Long faceFailed = attendanceRepository.countFaceVerificationStatus(providerId, "FAILED");
        Long totalFace = faceSuccess + faceFailed;
        
        if (totalFace > 0) {
            response.setFaceVerificationRate((faceSuccess * 100.0) / totalFace);
        } else {
            response.setFaceVerificationRate(null);
        }
        
        Long locSuccess = attendanceRepository.countLocationVerificationStatus(providerId, "VERIFIED");
        Long locFailed = attendanceRepository.countLocationVerificationStatus(providerId, "OUTSIDE_GEOFENCE");
        Long totalLoc = locSuccess + locFailed;
        
        if (totalLoc > 0) {
            response.setLocationVerificationRate((locSuccess * 100.0) / totalLoc);
        } else {
            response.setLocationVerificationRate(null);
        }
        
        return response;
    }
}
