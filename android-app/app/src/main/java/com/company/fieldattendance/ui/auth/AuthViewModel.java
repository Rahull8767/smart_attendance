package com.company.fieldattendance.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.company.fieldattendance.data.model.AuthResponse;
import com.company.fieldattendance.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    public AuthViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<AuthRepository.Resource<AuthResponse>> login(String employeeId, String password) {
        return repository.login(employeeId, password);
    }
}
