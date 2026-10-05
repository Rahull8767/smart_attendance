import os

base_dir = r"d:\studioProject\smart_attendance\android-app\app\src\main\java\com\company\fieldattendance"
repo_dir = os.path.join(base_dir, "data", "repository")
vm_dir = os.path.join(base_dir, "ui", "provider")

repo = """package com.company.fieldattendance.data.repository;
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
"""
with open(os.path.join(repo_dir, "ProviderRepository.java"), "w") as f: f.write(repo)

vm = """package com.company.fieldattendance.ui.provider;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.CeoDetailsResponse;
import com.company.fieldattendance.data.model.CeoResponse;
import com.company.fieldattendance.data.model.CreateCeoRequest;
import com.company.fieldattendance.data.repository.ProviderRepository;
import com.company.fieldattendance.utils.Resource;
import java.util.List;

public class ProviderViewModel extends AndroidViewModel {
    private ProviderRepository repository;
    private SessionManager sessionManager;
    
    public ProviderViewModel(@NonNull Application application) {
        super(application);
        repository = new ProviderRepository();
        sessionManager = new SessionManager(application);
    }
    
    public LiveData<Resource<List<CeoResponse>>> getCeos() {
        return repository.getCeos(sessionManager.getAuthToken());
    }
    
    public LiveData<Resource<CeoDetailsResponse>> getCeoDetails(String id) {
        return repository.getCeoDetails(sessionManager.getAuthToken(), id);
    }
    
    public LiveData<Resource<CeoResponse>> createCeo(CreateCeoRequest request) {
        return repository.createCeo(sessionManager.getAuthToken(), request);
    }
    
    public LiveData<Resource<CeoResponse>> updateCeoStatus(String id, String status) {
        return repository.updateCeoStatus(sessionManager.getAuthToken(), id, status);
    }
}
"""
with open(os.path.join(vm_dir, "ProviderViewModel.java"), "w") as f: f.write(vm)

print("Repository and ViewModel updated")
