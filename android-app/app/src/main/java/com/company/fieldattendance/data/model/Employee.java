package com.company.fieldattendance.data.model;

import java.util.UUID;

public class Employee {
    public UUID id;
    public String name;
    public String employeeCode;
    public String department;
    public String designation;
    public String status;
    public UUID assignedSiteId;
    public String assignedSiteName;
    public String attendanceStatus; // e.g. "PRESENT", "NOT PUNCHED IN"
    public boolean isFaceEnrolled;
}
