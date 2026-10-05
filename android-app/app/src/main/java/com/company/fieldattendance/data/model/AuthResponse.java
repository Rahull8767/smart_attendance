package com.company.fieldattendance.data.model;

public class AuthResponse {
    private String accessToken;
    private String userId;
    private String role;
    private String providerId;
    private String employeeId;
    private String ceoId;
    private String displayName;

    public String getAccessToken() { return accessToken; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getProviderId() { return providerId; }
    public String getEmployeeId() { return employeeId; }
    public String getCeoId() { return ceoId; }
    public String getDisplayName() { return displayName; }
}
