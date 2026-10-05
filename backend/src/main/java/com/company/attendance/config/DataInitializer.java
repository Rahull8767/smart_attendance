package com.company.attendance.config;

import com.company.attendance.entity.Employee;
import com.company.attendance.entity.Provider;
import com.company.attendance.entity.User;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.ProviderRepository;
import com.company.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.company.attendance.repository.CeoRepository ceoRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Create Provider
        Provider provider = null;
        if (providerRepository.findAll().isEmpty()) {
            provider = new Provider();
            provider.setCompanyName("FieldTrack Solutions");
            provider.setContactPerson("Operations Admin");
            provider.setEmail("provider@test.com");
            provider.setPhone("+91 98765 43210");
            provider.setStatus("ACTIVE");
            provider = providerRepository.save(provider);
        } else {
            provider = providerRepository.findAll().get(0);
            if (!"FieldTrack Solutions".equals(provider.getCompanyName())) {
                provider.setCompanyName("FieldTrack Solutions");
                providerRepository.save(provider);
            }
        }

        if (userRepository.findByEmail("provider@test.com").isEmpty()) {
            User providerUser = new User();
            providerUser.setProviderId(provider.getId());
            providerUser.setRole("ROLE_PROVIDER");
            providerUser.setEmail("provider@test.com");
            providerUser.setPasswordHash(passwordEncoder.encode("password123"));
            userRepository.save(providerUser);
            System.out.println("Test Provider created: provider@test.com");
        }

        // 2. Create CEO: Rahul Sharma, Apex Infrastructure Pvt. Ltd.
        com.company.attendance.entity.Ceo ceoEntity = null;
        if (userRepository.findByEmail("ceotest@example.com").isEmpty()) {
            User ceo = new User();
            ceo.setRole("ROLE_CEO");
            ceo.setEmail("ceotest@example.com");
            ceo.setPasswordHash(passwordEncoder.encode("password123"));
            ceo.setProviderId(provider.getId());
            ceo = userRepository.save(ceo);
            
            ceoEntity = new com.company.attendance.entity.Ceo();
            ceoEntity.setUserId(ceo.getId());
            ceoEntity.setProviderId(provider.getId());
            ceoEntity.setName("Rahul Sharma");
            ceoEntity.setEmail("ceotest@example.com");
            ceoEntity.setPhone("+91 98230 11223");
            ceoEntity.setDesignation("Chief Executive Officer");
            ceoEntity.setStatus("ACTIVE");
            ceoEntity = ceoRepository.save(ceoEntity);
            System.out.println("CEO created: ceotest@example.com");
        } else {
            User ceoUser = userRepository.findByEmail("ceotest@example.com").get();
            ceoEntity = ceoRepository.findByUserId(ceoUser.getId()).orElse(null);
        }
    }
}

