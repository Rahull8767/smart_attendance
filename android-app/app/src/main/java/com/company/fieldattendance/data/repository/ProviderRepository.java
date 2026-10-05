package com.company.fieldattendance.data.repository;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.company.fieldattendance.data.api.ProviderApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.CeoDetailsResponse;
import com.company.fieldattendance.data.model.CeoResponse;
import com.company.fieldattendance.data.model.CreateCeoRequest;
import com.company.fieldattendance.utils.Resource;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderRepository {
    private ProviderApiService apiService;
    
    public ProviderRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ProviderApiService.class);
    }
    
    private String getAuthHeader(String token) {
        return "Bearer " + token;
    }
    
    public LiveData<Resource<List<CeoResponse>>> getCeos(String token) {
        MutableLiveData<Resource<List<CeoResponse>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        apiService.getCeos(getAuthHeader(token)).enqueue(new Callback<List<CeoResponse>>() {
            @Override
            public void onResponse(Call<List<CeoResponse>> call, Response<List<CeoResponse>> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load CEOs", null));
                }
            }
            @Override
            public void onFailure(Call<List<CeoResponse>> call, Throwable t) {
                data.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return data;
    }
    
    public LiveData<Resource<CeoDetailsResponse>> getCeoDetails(String token, String id) {
        MutableLiveData<Resource<CeoDetailsResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        apiService.getCeoDetails(getAuthHeader(token), id).enqueue(new Callback<CeoDetailsResponse>() {
            @Override
            public void onResponse(Call<CeoDetailsResponse> call, Response<CeoDetailsResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to load CEO details", null));
                }
            }
            @Override
            public void onFailure(Call<CeoDetailsResponse> call, Throwable t) {
                data.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return data;
    }
    
    public LiveData<Resource<CeoResponse>> createCeo(String token, CreateCeoRequest request) {
        MutableLiveData<Resource<CeoResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        apiService.createCeo(getAuthHeader(token), request).enqueue(new Callback<CeoResponse>() {
            @Override
            public void onResponse(Call<CeoResponse> call, Response<CeoResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to create CEO", null));
                }
            }
            @Override
            public void onFailure(Call<CeoResponse> call, Throwable t) {
                data.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return data;
    }
    
    public LiveData<Resource<CeoResponse>> updateCeoStatus(String token, String id, String status) {
        MutableLiveData<Resource<CeoResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("status", status);
        apiService.updateCeoStatus(getAuthHeader(token), id, body).enqueue(new Callback<CeoResponse>() {
            @Override
            public void onResponse(Call<CeoResponse> call, Response<CeoResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    data.setValue(Resource.error("Failed to update status", null));
                }
            }
            @Override
            public void onFailure(Call<CeoResponse> call, Throwable t) {
                data.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return data;
    }
}
