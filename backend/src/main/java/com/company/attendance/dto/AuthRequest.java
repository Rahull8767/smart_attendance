package com.company.attendance.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
