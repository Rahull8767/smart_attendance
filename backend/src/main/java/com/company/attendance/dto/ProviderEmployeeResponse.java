package com.company.attendance.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ProviderEmployeeResponse {
    private UUID id;
    private String employeeCode;
    private String name;
    private String department;
    private String designation;
    private String status;
    private String ceoName;
    private String organizationName;
}
