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

    @Override
    public void run(String... args) throws Exception {
        // 1. Create CEO
        if (userRepository.findByEmail("ceo@test.com").isEmpty()) {
            User ceo = new User();
            ceo.setRole("ROLE_CEO");
            ceo.setEmail("ceo@test.com");
            ceo.setPasswordHash(passwordEncoder.encode("ChangeMe123!"));
            userRepository.save(ceo);
            System.out.println("Test CEO created: ceo@test.com");
        }

        // 2. Create Provider
        Provider provider = null;
        if (providerRepository.findAll().isEmpty()) {
            provider = new Provider();
            provider.setCompanyName("Test Provider Agency");
            provider.setContactPerson("John Smith");
            provider.setEmail("provider@test.com");
            provider.setPhone("555-0100");
            provider.setStatus("ACTIVE");
            provider = providerRepository.save(provider);
        } else {
            provider = providerRepository.findAll().get(0);
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

        // 3. Create Employee
        if (userRepository.findByEmail("employee@test.com").isEmpty()) {
            User empUser = new User();
            empUser.setProviderId(provider.getId());
            empUser.setRole("ROLE_EMPLOYEE");
            empUser.setEmail("employee@test.com");
            empUser.setPasswordHash(passwordEncoder.encode("password123"));
            empUser = userRepository.save(empUser);

            Employee emp = new Employee();
            emp.setUserId(empUser.getId());
            emp.setProviderId(provider.getId());
            emp.setEmployeeCode("EMP001");
            emp.setName("Jane Doe");
            emp.setDepartment("Field Operations");
            emp.setStatus("ACTIVE");
            employeeRepository.save(emp);
            System.out.println("Test Employee created: employee@test.com");
        }
    }
}
