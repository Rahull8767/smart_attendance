package com.company.attendance.controller;

import com.company.attendance.dto.AuthRequest;
import com.company.attendance.dto.AuthResponse;
import com.company.attendance.repository.UserRepository;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.entity.User;
import com.company.attendance.entity.Employee;
import com.company.attendance.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        
        User dbUser = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();
        java.util.UUID providerId = dbUser.getProviderId();
        
        Employee emp = employeeRepository.findByUserId(dbUser.getId());
        java.util.UUID employeeId = emp != null ? emp.getId() : null;
        
        return ResponseEntity.ok(new AuthResponse(jwt, dbUser.getId(), role, providerId, employeeId));
    }
}
