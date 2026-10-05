import os

base_dir = r"d:\studioProject\smart_attendance\android-app\app\src\main\java\com\company\fieldattendance"
dto_dir = os.path.join(base_dir, "data", "model")
service_dir = os.path.join(base_dir, "data", "api")
repo_dir = os.path.join(base_dir, "data", "repository")
vm_dir = os.path.join(base_dir, "ui", "provider")

# 1. DTOs
dto_create_ceo_request = """package com.company.fieldattendance.data.model;
public class CreateCeoRequest {
    public String name;
    public String email;
    public String phone;
    public String designation;
    
    public String companyName;
    public String industry;
    public String companyPhone;
    public String companyEmail;
    public String address;
    public String city;
    public String state;
    public String postalCode;
    
    public String loginEmail;
    public String password;
    public String status;
}
"""

dto_ceo_response = """package com.company.fieldattendance.data.model;
public class CeoResponse {
    public String id;
    public String name;
    public String companyName;
    public String status;
    public long employeeCount;
    public long siteCount;
    public String lastActive;
}
"""

dto_ceo_details_response = """package com.company.fieldattendance.data.model;
public class CeoDetailsResponse {
    public String id;
    public String name;
    public String email;
    public String phone;
    public String designation;
    public String status;
    public String createdAt;
    
    public String organizationId;
    public String companyName;
    public String industry;
    public String companyEmail;
    public String companyPhone;
    
    public long employeeCount;
    public long siteCount;
}
"""

with open(os.path.join(dto_dir, "CreateCeoRequest.java"), "w") as f: f.write(dto_create_ceo_request)
with open(os.path.join(dto_dir, "CeoResponse.java"), "w") as f: f.write(dto_ceo_response)
with open(os.path.join(dto_dir, "CeoDetailsResponse.java"), "w") as f: f.write(dto_ceo_details_response)

# 2. Retrofit API interface additions
# We'll just create a ProviderApiService.java directly
api_service = """package com.company.fieldattendance.data.api;
import com.company.fieldattendance.data.model.CeoDetailsResponse;
import com.company.fieldattendance.data.model.CeoResponse;
import com.company.fieldattendance.data.model.CreateCeoRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProviderApiService {
    @GET("api/provider/ceos")
    Call<List<CeoResponse>> getCeos();

    @GET("api/provider/ceos/{id}")
    Call<CeoDetailsResponse> getCeoDetails(@Path("id") String id);

    @POST("api/provider/ceos")
    Call<CeoResponse> createCeo(@Body CreateCeoRequest request);

    @PATCH("api/provider/ceos/{id}/status")
    Call<CeoResponse> updateCeoStatus(@Path("id") String id, @Body java.util.Map<String, String> status);
}
"""
with open(os.path.join(service_dir, "ProviderApiService.java"), "w") as f: f.write(api_service)

# 3. ProviderRepository
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
    
    public LiveData<Resource<List<CeoResponse>>> getCeos() {
        MutableLiveData<Resource<List<CeoResponse>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        apiService.getCeos().enqueue(new Callback<List<CeoResponse>>() {
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
    
    public LiveData<Resource<CeoDetailsResponse>> getCeoDetails(String id) {
        MutableLiveData<Resource<CeoDetailsResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        apiService.getCeoDetails(id).enqueue(new Callback<CeoDetailsResponse>() {
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
    
    public LiveData<Resource<CeoResponse>> createCeo(CreateCeoRequest request) {
        MutableLiveData<Resource<CeoResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        apiService.createCeo(request).enqueue(new Callback<CeoResponse>() {
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
    
    public LiveData<Resource<CeoResponse>> updateCeoStatus(String id, String status) {
        MutableLiveData<Resource<CeoResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("status", status);
        apiService.updateCeoStatus(id, body).enqueue(new Callback<CeoResponse>() {
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

# 4. ProviderViewModel
vm = """package com.company.fieldattendance.ui.provider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.company.fieldattendance.data.model.CeoDetailsResponse;
import com.company.fieldattendance.data.model.CeoResponse;
import com.company.fieldattendance.data.model.CreateCeoRequest;
import com.company.fieldattendance.data.repository.ProviderRepository;
import com.company.fieldattendance.utils.Resource;
import java.util.List;

public class ProviderViewModel extends ViewModel {
    private ProviderRepository repository;
    
    public ProviderViewModel() {
        repository = new ProviderRepository();
    }
    
    public LiveData<Resource<List<CeoResponse>>> getCeos() {
        return repository.getCeos();
    }
    
    public LiveData<Resource<CeoDetailsResponse>> getCeoDetails(String id) {
        return repository.getCeoDetails(id);
    }
    
    public LiveData<Resource<CeoResponse>> createCeo(CreateCeoRequest request) {
        return repository.createCeo(request);
    }
    
    public LiveData<Resource<CeoResponse>> updateCeoStatus(String id, String status) {
        return repository.updateCeoStatus(id, status);
    }
}
"""
with open(os.path.join(vm_dir, "ProviderViewModel.java"), "w") as f: f.write(vm)

print("Android integration code generated")
