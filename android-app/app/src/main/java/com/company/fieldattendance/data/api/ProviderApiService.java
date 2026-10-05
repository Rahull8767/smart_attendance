package com.company.fieldattendance.data.api;
import com.company.fieldattendance.data.model.CeoDetailsResponse;
import com.company.fieldattendance.data.model.CeoResponse;
import com.company.fieldattendance.data.model.CreateCeoRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProviderApiService {
    @GET("api/provider/ceos")
    Call<List<CeoResponse>> getCeos(@Header("Authorization") String token);

    @GET("api/provider/ceos/{id}")
    Call<CeoDetailsResponse> getCeoDetails(@Header("Authorization") String token, @Path("id") String id);

    @POST("api/provider/ceos")
    Call<CeoResponse> createCeo(@Header("Authorization") String token, @Body CreateCeoRequest request);

    @PATCH("api/provider/ceos/{id}/status")
    Call<CeoResponse> updateCeoStatus(@Header("Authorization") String token, @Path("id") String id, @Body java.util.Map<String, String> status);
}
