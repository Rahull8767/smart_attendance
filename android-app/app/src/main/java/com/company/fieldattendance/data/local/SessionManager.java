package com.company.fieldattendance.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FieldAttendanceSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMPLOYEE_ID = "employeeId";
    private static final String KEY_CEO_ID = "ceoId";
    private static final String KEY_PROVIDER_ID = "providerId";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ORGANIZATION_ID = "organizationId";
    private static final String KEY_IS_PRESENTATION_MODE = "isPresentationMode";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token, String role, String employeeId, String ceoId, String providerId, String displayName) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_EMPLOYEE_ID, employeeId);
        editor.putString(KEY_CEO_ID, ceoId);
        editor.putString(KEY_PROVIDER_ID, providerId);
        editor.putString(KEY_DISPLAY_NAME, displayName);
        editor.putBoolean(KEY_IS_PRESENTATION_MODE, false);
        editor.apply();
    }

    public void savePresentationSession(String role, String userId, String name, String email, String organizationId, String employeeId, String ceoId, String providerId) {
        editor.putString(KEY_TOKEN, "presentation-mock-jwt-token");
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_DISPLAY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ORGANIZATION_ID, organizationId);
        editor.putString(KEY_EMPLOYEE_ID, employeeId);
        editor.putString(KEY_CEO_ID, ceoId);
        editor.putString(KEY_PROVIDER_ID, providerId);
        editor.putBoolean(KEY_IS_PRESENTATION_MODE, true);
        editor.apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }
    public String getEmployeeId() { return prefs.getString(KEY_EMPLOYEE_ID, null); }
    public String getCeoId() { return prefs.getString(KEY_CEO_ID, null); }
    public String getProviderId() { return prefs.getString(KEY_PROVIDER_ID, null); }
    public String getDisplayName() { return prefs.getString(KEY_DISPLAY_NAME, null); }
    public String getUsername() { return prefs.getString(KEY_DISPLAY_NAME, null); }
    public String getUserId() { return prefs.getString(KEY_USER_ID, null); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getOrganizationId() { return prefs.getString(KEY_ORGANIZATION_ID, null); }
    
    public boolean isPresentationMode() {
        return prefs.getBoolean(KEY_IS_PRESENTATION_MODE, false);
    }

    public void setPresentationMode(boolean isPres) {
        editor.putBoolean(KEY_IS_PRESENTATION_MODE, isPres).apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public void logout() {
        clearSession();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
