package com.company.fieldattendance.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PresentationDataManager {

    private static final String PREF_NAME = "PresentationAttendanceData";
    private static final String KEY_IS_PUNCHED_IN = "pres_is_punched_in";
    private static final String KEY_PUNCH_IN_TIME = "pres_punch_in_time";
    private static final String KEY_PUNCH_OUT_TIME = "pres_punch_out_time";
    private static final String KEY_PUNCH_LAT = "pres_punch_lat";
    private static final String KEY_PUNCH_LON = "pres_punch_lon";

    private static PresentationDataManager instance;
    private final SharedPreferences prefs;

    public static synchronized PresentationDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PresentationDataManager(context.getApplicationContext());
        }
        return instance;
    }

    private PresentationDataManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isPunchedIn() {
        return prefs.getBoolean(KEY_IS_PUNCHED_IN, false);
    }

    public String getPunchInTime() {
        return prefs.getString(KEY_PUNCH_IN_TIME, "08:42 AM");
    }

    public String getPunchOutTime() {
        return prefs.getString(KEY_PUNCH_OUT_TIME, null);
    }

    public double getPunchLat() {
        return Double.longBitsToDouble(prefs.getLong(KEY_PUNCH_LAT, Double.doubleToLongBits(21.1460)));
    }

    public double getPunchLon() {
        return Double.longBitsToDouble(prefs.getLong(KEY_PUNCH_LON, Double.doubleToLongBits(79.0884)));
    }

    public void recordPunchIn(String time, double lat, double lon) {
        if (time == null || time.isEmpty()) {
            time = new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());
        }
        prefs.edit()
                .putBoolean(KEY_IS_PUNCHED_IN, true)
                .putString(KEY_PUNCH_IN_TIME, time)
                .remove(KEY_PUNCH_OUT_TIME)
                .putLong(KEY_PUNCH_LAT, Double.doubleToLongBits(lat))
                .putLong(KEY_PUNCH_LON, Double.doubleToLongBits(lon))
                .apply();
    }

    public void recordPunchOut(String time) {
        if (time == null || time.isEmpty()) {
            time = new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());
        }
        prefs.edit()
                .putBoolean(KEY_IS_PUNCHED_IN, false)
                .putString(KEY_PUNCH_OUT_TIME, time)
                .apply();
    }

    public void resetPresentationState() {
        prefs.edit().clear().apply();
    }

    // ==============================================================
    // JSON GENERATORS FOR PRESENTATION RESPONSES
    // ==============================================================

    public String getProviderDashboardStatsJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("totalOrganizations", 3);
            obj.put("activeCeos", 8);
            obj.put("totalCeos", 8);
            obj.put("totalEmployees", 126);
            obj.put("totalSites", 14);
            obj.put("totalWorkSites", 14);
            obj.put("todayAttendance", "108 / 126");
            obj.put("faceVerificationRate", 98.4);
            obj.put("locationVerificationRate", 97.8);
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public String getProviderCeosJson() {
        try {
            JSONArray arr = new JSONArray();
            JSONObject ceo = new JSONObject();
            ceo.put("id", "bae70754-deeb-4681-b83f-c7f531c5148a");
            ceo.put("name", "Rahul Sharma");
            ceo.put("companyName", "Apex Infrastructure Pvt. Ltd.");
            ceo.put("status", "ACTIVE");
            ceo.put("employeeCount", 24);
            ceo.put("siteCount", 3);
            arr.put(ceo);
            return arr.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getProviderCeoDetailsJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "bae70754-deeb-4681-b83f-c7f531c5148a");
            obj.put("name", "Rahul Sharma");
            obj.put("email", "ceotest@example.com");
            obj.put("phone", "+91 98765 43210");
            obj.put("designation", "Chief Executive Officer");
            obj.put("status", "ACTIVE");
            obj.put("organizationName", "Apex Infrastructure Pvt. Ltd.");
            obj.put("industry", "Civil Infrastructure & Construction");
            obj.put("companyEmail", "contact@apexinfra.com");
            obj.put("companyPhone", "+91 712 2548900");
            obj.put("companyAddress", "Apex Complex, Civil Lines, Nagpur, Maharashtra");
            obj.put("employeeCount", 24);
            obj.put("siteCount", 3);
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public String getCeoDashboardStatsJson() {
        try {
            boolean punched = isPunchedIn();
            JSONObject obj = new JSONObject();
            obj.put("presentToday", punched ? 19 : 18);
            obj.put("absentToday", 4);
            obj.put("onField", punched ? 3 : 2);
            obj.put("activeSites", 3);
            obj.put("totalEmployees", 24);
            obj.put("verificationRate", 98.2);
            obj.put("companyName", "Apex Infrastructure Pvt. Ltd.");

            JSONArray activityArr = new JSONArray();

            if (punched) {
                JSONObject r1 = new JSONObject();
                r1.put("employeeName", "Rahul Sharma");
                r1.put("action", "Punch In");
                r1.put("time", getPunchInTime());
                r1.put("siteName", "Apex Tower Construction Site");
                activityArr.put(r1);
            }

            JSONObject r2 = new JSONObject();
            r2.put("employeeName", "Amit Patil");
            r2.put("action", "Punch In");
            r2.put("time", "08:31 AM");
            r2.put("siteName", "Apex Tower Construction Site");
            activityArr.put(r2);

            JSONObject r3 = new JSONObject();
            r3.put("employeeName", "Sneha Joshi");
            r3.put("action", "Punch In");
            r3.put("time", "08:17 AM");
            r3.put("siteName", "Metro Site");
            activityArr.put(r3);

            obj.put("recentActivity", activityArr);
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public String getCeoEmployeesJson() {
        try {
            JSONArray arr = new JSONArray();

            JSONObject e1 = new JSONObject();
            e1.put("id", "c331f2f4-ccf6-4a2f-8bfc-a5e6ff7319c4");
            e1.put("employeeCode", "EMP-1024");
            e1.put("name", "Rahul Sharma");
            e1.put("department", "Field Operations");
            e1.put("designation", "Site Supervisor");
            e1.put("status", "ACTIVE");
            e1.put("assignedSiteName", "Apex Tower Construction Site");
            e1.put("todayStatus", isPunchedIn() ? "PRESENT" : "ABSENT");
            e1.put("faceEnrolled", true);
            arr.put(e1);

            JSONObject e2 = new JSONObject();
            e2.put("id", "9d3ffd51-70e4-476d-8148-4499ef390e5b");
            e2.put("employeeCode", "EMP-1025");
            e2.put("name", "Amit Patil");
            e2.put("department", "Civil Engineering");
            e2.put("designation", "Site Engineer");
            e2.put("status", "ACTIVE");
            e2.put("assignedSiteName", "Apex Tower Construction Site");
            e2.put("todayStatus", "PRESENT");
            e2.put("faceEnrolled", true);
            arr.put(e2);

            JSONObject e3 = new JSONObject();
            e3.put("id", "5641a34d-ba16-41cc-a2ab-df9f471205b5");
            e3.put("employeeCode", "EMP-1026");
            e3.put("name", "Sneha Joshi");
            e3.put("department", "Operations");
            e3.put("designation", "Operations Executive");
            e3.put("status", "ACTIVE");
            e3.put("assignedSiteName", "Metro Site");
            e3.put("todayStatus", "PRESENT");
            e3.put("faceEnrolled", true);
            arr.put(e3);

            JSONObject e4 = new JSONObject();
            e4.put("id", "1077fe02-c001-40fe-8cfb-423257e414ec");
            e4.put("employeeCode", "EMP-1027");
            e4.put("name", "Vivek Deshmukh");
            e4.put("department", "Electrical");
            e4.put("designation", "Electrical Supervisor");
            e4.put("status", "ACTIVE");
            e4.put("assignedSiteName", "Apex Tower Construction Site");
            e4.put("todayStatus", "ABSENT");
            e4.put("faceEnrolled", true);
            arr.put(e4);

            return arr.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getCeoSitesJson() {
        try {
            JSONArray arr = new JSONArray();
            JSONObject s1 = new JSONObject();
            s1.put("id", "34a82fba-1e3f-4de0-9f33-b90f94529104");
            s1.put("name", "Apex Tower Construction Site");
            s1.put("address", "Nagpur, Maharashtra, India");
            s1.put("latitude", 21.1458);
            s1.put("longitude", 79.0882);
            s1.put("altitude", 312.0);
            s1.put("geofenceRadius", 150);
            s1.put("status", "ACTIVE");
            s1.put("activeEmployeeCount", isPunchedIn() ? 2 : 1);
            arr.put(s1);
            return arr.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getCeoAttendanceJson() {
        try {
            JSONArray arr = new JSONArray();

            if (isPunchedIn()) {
                JSONObject a1 = new JSONObject();
                a1.put("id", "501a3577-d7fc-4e6f-8012-70678d228f01");
                a1.put("employeeId", "c331f2f4-ccf6-4a2f-8bfc-a5e6ff7319c4");
                a1.put("employeeName", "Rahul Sharma");
                a1.put("employeeCode", "EMP-1024");
                a1.put("department", "Field Operations");
                a1.put("designation", "Site Supervisor");
                a1.put("workSiteName", "Apex Tower Construction Site");
                a1.put("punchInTime", getPunchInTime());
                a1.put("status", "PRESENT");
                a1.put("faceVerificationStatus", "VERIFIED");
                a1.put("locationVerificationStatus", "VERIFIED");
                a1.put("latitude", getPunchLat());
                a1.put("longitude", getPunchLon());
                arr.put(a1);
            }

            JSONObject a2 = new JSONObject();
            a2.put("id", "501a3577-d7fc-4e6f-8012-70678d228f02");
            a2.put("employeeId", "9d3ffd51-70e4-476d-8148-4499ef390e5b");
            a2.put("employeeName", "Amit Patil");
            a2.put("employeeCode", "EMP-1025");
            a2.put("department", "Civil Engineering");
            a2.put("designation", "Site Engineer");
            a2.put("workSiteName", "Apex Tower Construction Site");
            a2.put("punchInTime", "08:31 AM");
            a2.put("status", "PRESENT");
            a2.put("faceVerificationStatus", "VERIFIED");
            a2.put("locationVerificationStatus", "VERIFIED");
            a2.put("latitude", 21.1459);
            a2.put("longitude", 79.0883);
            arr.put(a2);

            JSONObject a3 = new JSONObject();
            a3.put("id", "501a3577-d7fc-4e6f-8012-70678d228f03");
            a3.put("employeeId", "5641a34d-ba16-41cc-a2ab-df9f471205b5");
            a3.put("employeeName", "Sneha Joshi");
            a3.put("employeeCode", "EMP-1026");
            a3.put("department", "Operations");
            a3.put("designation", "Operations Executive");
            a3.put("workSiteName", "Metro Site");
            a3.put("punchInTime", "08:17 AM");
            a3.put("status", "PRESENT");
            a3.put("faceVerificationStatus", "VERIFIED");
            a3.put("locationVerificationStatus", "VERIFIED");
            a3.put("latitude", 21.1465);
            a3.put("longitude", 79.0890);
            arr.put(a3);

            return arr.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getEmployeeDashboardStatsJson() {
        try {
            boolean punched = isPunchedIn();
            JSONObject obj = new JSONObject();
            obj.put("todayStatus", punched ? "PRESENT" : "NOT PUNCHED IN");
            obj.put("punchInTime", punched ? getPunchInTime() : "--:--");
            obj.put("punchOutTime", getPunchOutTime());
            obj.put("assignedSiteName", "Apex Tower Construction Site");
            obj.put("siteAddress", "Nagpur, Maharashtra, India");
            obj.put("siteLatitude", 21.1458);
            obj.put("siteLongitude", 79.0882);
            obj.put("siteAltitude", 312.0);
            obj.put("geofenceRadius", 150);
            obj.put("workingDuration", punched ? "0h 15m" : "0h 0m");
            obj.put("employeeCode", "EMP-1024");
            obj.put("employeeName", "Rahul Sharma");
            obj.put("department", "Field Operations");
            obj.put("designation", "Site Supervisor");
            obj.put("isFaceEnrolled", true);
            obj.put("faceEnrolled", true);
            obj.put("assignedSiteId", "34a82fba-1e3f-4de0-9f33-b90f94529104");
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public String getEmployeeHistoryJson() {
        try {
            JSONArray arr = new JSONArray();

            if (isPunchedIn()) {
                JSONObject r1 = new JSONObject();
                r1.put("id", "501a3577-d7fc-4e6f-8012-70678d228f01");
                r1.put("employeeId", "c331f2f4-ccf6-4a2f-8bfc-a5e6ff7319c4");
                r1.put("workSiteId", "34a82fba-1e3f-4de0-9f33-b90f94529104");
                r1.put("workSiteName", "Apex Tower Construction Site");
                r1.put("punchInTime", "2026-10-05T08:42:00");
                r1.put("punchInLatitude", getPunchLat());
                r1.put("punchInLongitude", getPunchLon());
                r1.put("punchInAltitude", 311.0);
                r1.put("punchInAccuracy", 8.0f);
                r1.put("faceVerificationStatus", "VERIFIED");
                r1.put("locationVerificationStatus", "VERIFIED");
                arr.put(r1);
            }

            // Yesterday record
            JSONObject r2 = new JSONObject();
            r2.put("id", "501a3577-d7fc-4e6f-8012-70678d228f00");
            r2.put("employeeId", "c331f2f4-ccf6-4a2f-8bfc-a5e6ff7319c4");
            r2.put("workSiteId", "34a82fba-1e3f-4de0-9f33-b90f94529104");
            r2.put("workSiteName", "Apex Tower Construction Site");
            r2.put("punchInTime", "2026-10-04T08:42:00");
            r2.put("punchOutTime", "2026-10-04T17:37:00");
            r2.put("punchInLatitude", 21.1460);
            r2.put("punchInLongitude", 79.0884);
            r2.put("punchInAltitude", 311.0);
            r2.put("punchInAccuracy", 8.0f);
            r2.put("faceVerificationStatus", "VERIFIED");
            r2.put("locationVerificationStatus", "VERIFIED");
            arr.put(r2);

            return arr.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getLocationVerifyJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("verified", true);
            obj.put("status", "VERIFIED");
            obj.put("message", "Location verified successfully");
            obj.put("calculatedDistance", 42.0);
            obj.put("allowedRadius", 150.0);
            obj.put("siteLatitude", 21.1458);
            obj.put("siteLongitude", 79.0882);
            obj.put("siteAltitude", 312.0);
            obj.put("altitudeDifference", 1.0);
            obj.put("siteName", "Apex Tower Construction Site");
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }
}
