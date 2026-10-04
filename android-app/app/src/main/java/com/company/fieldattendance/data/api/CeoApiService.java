package com.company.fieldattendance.data.api;

import com.company.fieldattendance.data.model.Employee;
import com.company.fieldattendance.data.model.WorkSite;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CeoApiService {
    @GET("/api/employees")
    Call<List<Employee>> getEmployees();

    @POST("/api/employees")
    Call<Employee> createEmployee(@Body Employee employee);

    @GET("/api/sites")
    Call<List<WorkSite>> getSites();

    @POST("/api/sites")
    Call<WorkSite> createSite(@Body WorkSite site);
}
