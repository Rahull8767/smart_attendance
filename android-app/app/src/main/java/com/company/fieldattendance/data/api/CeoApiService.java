package com.company.fieldattendance.data.api;

import com.company.fieldattendance.data.model.ApiResponse;
import com.company.fieldattendance.data.model.CeoAttendanceRecordDTO;
import com.company.fieldattendance.data.model.CeoDashboardStatsDTO;
import com.company.fieldattendance.data.model.Employee;
import com.company.fieldattendance.data.model.WorkSite;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CeoApiService {
    @GET("/api/ceo/dashboard-stats")
    Call<CeoDashboardStatsDTO> getDashboardStats();

    @GET("/api/ceo/sites")
    Call<List<WorkSite>> getSites();

    @POST("/api/ceo/sites")
    Call<WorkSite> createSite(@Body WorkSite site);

    @GET("/api/ceo/employees")
    Call<List<Employee>> getEmployees();

    @POST("/api/ceo/employees")
    Call<Employee> createEmployee(@Body Map<String, String> request);

    @GET("/api/ceo/attendance")
    Call<List<CeoAttendanceRecordDTO>> getAttendance();

    @POST("/api/face/enroll")
    Call<ApiResponse> enrollFace(@Body Map<String, Object> payload);

    @GET("/api/face/profile/{employeeId}")
    Call<Map<String, Object>> getFaceProfile(@Path("employeeId") UUID employeeId);
}
