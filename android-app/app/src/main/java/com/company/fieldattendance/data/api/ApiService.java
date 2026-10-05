package com.company.fieldattendance.data.api;

import com.company.fieldattendance.data.model.ApiResponse;
import com.company.fieldattendance.data.model.AttendanceRecord;
import com.company.fieldattendance.data.model.AuthRequest;
import com.company.fieldattendance.data.model.AuthResponse;
import com.company.fieldattendance.data.model.CeoDashboardStatsDTO;
import com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO;
import com.company.fieldattendance.data.model.LocationVerificationRequest;
import com.company.fieldattendance.data.model.LocationVerificationResponse;
import com.company.fieldattendance.data.model.OfflinePunchRequest;
import com.company.fieldattendance.data.model.ProviderDashboardStatsDTO;
import com.company.fieldattendance.data.model.PunchRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("/api/attendance/punch-in")
    Call<ApiResponse> punchIn(@Body PunchRequest request);

    @POST("/api/attendance/punch-out")
    Call<ApiResponse> punchOut(@Body PunchRequest request);

    @POST("/api/sync/offline-punches")
    Call<ApiResponse> syncOfflinePunches(@Body OfflinePunchRequest request);

    @POST("/api/attendance/verify-location")
    Call<LocationVerificationResponse> verifyLocation(@Body LocationVerificationRequest request);

    @GET("/api/attendance/history/{employeeId}")
    Call<List<AttendanceRecord>> getAttendanceHistory(@Path("employeeId") UUID employeeId);

    @POST("/api/face/verify")
    Call<ApiResponse> verifyFace(@Body Map<String, Object> payload);

    @GET("/api/face/profile/{employeeId}")
    Call<Map<String, Object>> getFaceProfile(@Path("employeeId") UUID employeeId);

    @GET("/api/ceo/dashboard-stats")
    Call<CeoDashboardStatsDTO> getCeoDashboardStats();

    @GET("/api/employees/dashboard-stats")
    Call<EmployeeDashboardStatsDTO> getEmployeeDashboardStats();

    @GET("/api/provider/dashboard-stats")
    Call<ProviderDashboardStatsDTO> getProviderDashboardStats();
}
