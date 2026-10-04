package com.company.fieldattendance.data.api;

import com.company.fieldattendance.data.model.AuthRequest;
import com.company.fieldattendance.data.model.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.company.fieldattendance.data.model.PunchRequest;
import com.company.fieldattendance.data.model.OfflinePunchRequest;
import com.company.fieldattendance.data.model.ApiResponse;

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
    Call<com.company.fieldattendance.data.model.LocationVerificationResponse> verifyLocation(@Body com.company.fieldattendance.data.model.LocationVerificationRequest request);
}
