package com.company.fieldattendance.data.local;

import android.content.Context;
import com.company.fieldattendance.BuildConfig;

public class PresentationAuthManager {

    public static final String ROLE_PROVIDER = "ROLE_PROVIDER";
    public static final String ROLE_CEO = "ROLE_CEO";
    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

    // Credentials specified by user
    public static final String PROVIDER_EMAIL = "provider.demo@fieldtrack.app";
    public static final String PROVIDER_PASSWORD = "Provider@123";

    public static final String CEO_EMAIL = "ceo.demo@fieldtrack.app";
    public static final String CEO_PASSWORD = "Ceo@123";

    public static final String EMPLOYEE_EMAIL = "employee.demo@fieldtrack.app";
    public static final String EMPLOYEE_PASSWORD = "Employee@123";

    public static boolean isPresentationAccessEnabled() {
        return BuildConfig.PRESENTATION_ACCESS_ENABLED;
    }

    public static boolean isPresentationCredential(String email) {
        if (email == null) return false;
        String e = email.trim().toLowerCase();
        return e.equals(PROVIDER_EMAIL.toLowerCase()) ||
               e.equals(CEO_EMAIL.toLowerCase()) ||
               e.equals(EMPLOYEE_EMAIL.toLowerCase());
    }

    public static String authenticate(Context context, String email, String password) {
        if (!isPresentationAccessEnabled() || email == null || password == null) {
            return null;
        }

        String e = email.trim();
        String p = password.trim();

        if (PROVIDER_EMAIL.equalsIgnoreCase(e) && PROVIDER_PASSWORD.equals(p)) {
            loginAsRole(context, ROLE_PROVIDER);
            return ROLE_PROVIDER;
        } else if (CEO_EMAIL.equalsIgnoreCase(e) && CEO_PASSWORD.equals(p)) {
            loginAsRole(context, ROLE_CEO);
            return ROLE_CEO;
        } else if (EMPLOYEE_EMAIL.equalsIgnoreCase(e) && EMPLOYEE_PASSWORD.equals(p)) {
            loginAsRole(context, ROLE_EMPLOYEE);
            return ROLE_EMPLOYEE;
        }

        return null;
    }

    public static void loginAsRole(Context context, String role) {
        SessionManager sessionManager = new SessionManager(context);

        if (ROLE_PROVIDER.equals(role)) {
            sessionManager.savePresentationSession(
                    ROLE_PROVIDER,
                    "presentation-provider",
                    "FieldTrack Solutions",
                    PROVIDER_EMAIL,
                    null,
                    null,
                    null,
                    "6584f2d7-9468-4172-8a8f-4e1eeaf8d9d5"
            );
        } else if (ROLE_CEO.equals(role)) {
            sessionManager.savePresentationSession(
                    ROLE_CEO,
                    "presentation-ceo",
                    "Rahul Sharma",
                    CEO_EMAIL,
                    "0baf6d53-49fb-4aae-b057-8646fc7d7375",
                    null,
                    "bae70754-deeb-4681-b83f-c7f531c5148a",
                    "6584f2d7-9468-4172-8a8f-4e1eeaf8d9d5"
            );
        } else {
            sessionManager.savePresentationSession(
                    ROLE_EMPLOYEE,
                    "presentation-employee",
                    "Rahul Sharma",
                    EMPLOYEE_EMAIL,
                    "0baf6d53-49fb-4aae-b057-8646fc7d7375",
                    "c331f2f4-ccf6-4a2f-8bfc-a5e6ff7319c4",
                    "bae70754-deeb-4681-b83f-c7f531c5148a",
                    "6584f2d7-9468-4172-8a8f-4e1eeaf8d9d5"
            );
        }
    }
}
