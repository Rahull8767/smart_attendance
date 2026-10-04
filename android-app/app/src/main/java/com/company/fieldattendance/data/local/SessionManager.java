package com.company.fieldattendance.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FieldAttendanceSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMPLOYEE_ID = "employeeId";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token, String role, String employeeId) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_EMPLOYEE_ID, employeeId);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public String getEmployeeId() {
        return prefs.getString(KEY_EMPLOYEE_ID, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
