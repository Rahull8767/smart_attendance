package com.company.attendance.dto;

import lombok.Data;

@Data
public class CreateCeoRequest {
    // Personal Info
    private String name;
    private String email; // CEO email (personal/contact)
    private String phone;
    private String designation;
    
    // Organization Info
    private String companyName;
    private String industry;
    private String companyPhone;
    private String companyEmail;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    
    // Account Info
    private String loginEmail;
    private String password;
    
    // Settings
    private String status;
}
