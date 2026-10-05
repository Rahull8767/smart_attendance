package com.company.fieldattendance.data.api;

import android.content.Context;
import androidx.annotation.NonNull;

import com.company.fieldattendance.data.local.PresentationDataManager;
import com.company.fieldattendance.data.local.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PresentationInterceptor implements Interceptor {

    private final Context context;
    private final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public PresentationInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        SessionManager sessionManager = new SessionManager(context);

        // If not in presentation access mode, execute the normal network request against Spring Boot
        if (!sessionManager.isPresentationMode()) {
            return chain.proceed(request);
        }

        // Handle presentation requests locally without touching the network
        String path = request.url().encodedPath();
        String method = request.method();
        PresentationDataManager dataManager = PresentationDataManager.getInstance(context);

        String jsonResponse = "{}";

        if (path.contains("/api/provider/dashboard")) {
            jsonResponse = dataManager.getProviderDashboardStatsJson();
        } else if (path.equals("/api/provider/ceos")) {
            jsonResponse = dataManager.getProviderCeosJson();
        } else if (path.startsWith("/api/provider/ceos/")) {
            jsonResponse = dataManager.getProviderCeoDetailsJson();
        } else if (path.contains("/api/ceo/dashboard-stats")) {
            jsonResponse = dataManager.getCeoDashboardStatsJson();
        } else if (path.equals("/api/ceo/employees")) {
            jsonResponse = dataManager.getCeoEmployeesJson();
        } else if (path.equals("/api/ceo/sites")) {
            jsonResponse = dataManager.getCeoSitesJson();
        } else if (path.equals("/api/ceo/attendance")) {
            jsonResponse = dataManager.getCeoAttendanceJson();
        } else if (path.contains("/api/employees/dashboard-stats")) {
            jsonResponse = dataManager.getEmployeeDashboardStatsJson();
        } else if (path.contains("/api/face/verify")) {
            jsonResponse = "{\"success\":true,\"message\":\"Identity verified \u2713\"}";
        } else if (path.contains("/api/face/profile")) {
            jsonResponse = "{\"enrolled\":true,\"employeeId\":\"c331f2f4-ccf6-4a2f-8bfc-a5e6ff7319c4\"}";
        } else if (path.contains("/api/attendance/verify-location")) {
            jsonResponse = dataManager.getLocationVerifyJson();
        } else if (path.contains("/api/attendance/punch-in")) {
            dataManager.recordPunchIn("08:42 AM", 21.1460, 79.0884);
            jsonResponse = "{\"success\":true,\"message\":\"Punch-in successful\"}";
        } else if (path.contains("/api/attendance/punch-out")) {
            dataManager.recordPunchOut("05:37 PM");
            jsonResponse = "{\"success\":true,\"message\":\"Punch-out successful\"}";
        } else if (path.contains("/api/attendance/history")) {
            jsonResponse = dataManager.getEmployeeHistoryJson();
        } else {
            jsonResponse = "{\"success\":true,\"message\":\"Operation completed\"}";
        }

        return new Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(JSON_MEDIA_TYPE, jsonResponse))
                .build();
    }
}
