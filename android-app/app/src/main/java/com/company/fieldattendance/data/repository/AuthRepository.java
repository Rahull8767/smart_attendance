package com.company.fieldattendance.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.company.fieldattendance.data.api.ApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.AuthRequest;
import com.company.fieldattendance.data.model.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository() {
        this.apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        AuthRequest request = new AuthRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error("Login failed. Check credentials."));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public static class Resource<T> {
        public enum Status { SUCCESS, ERROR, LOADING }
        public final Status status;
        public final T data;
        public final String message;

        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> Resource<T> success(T data) {
            return new Resource<>(Status.SUCCESS, data, null);
        }

        public static <T> Resource<T> error(String msg) {
            return new Resource<>(Status.ERROR, null, msg);
        }

        public static <T> Resource<T> loading() {
            return new Resource<>(Status.LOADING, null, null);
        }
    }
}
