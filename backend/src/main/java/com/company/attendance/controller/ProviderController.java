package com.company.attendance.controller;

import com.company.attendance.entity.Employee;
import com.company.attendance.entity.User;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getMyEmployees(Authentication authentication) {
        String email = authentication.getName();
        User providerUser = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(employeeRepository.findByProviderId(providerUser.getProviderId()));
    }
}
