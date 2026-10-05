package com.company.fieldattendance.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.PresentationAuthManager;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.employee.EmployeeDashboardActivity;
import com.company.fieldattendance.ui.ceo.CeoDashboardActivity;
import com.company.fieldattendance.ui.provider.ProviderHomeActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private View btnPresentationAccess;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        btnPresentationAccess = findViewById(R.id.btnPresentationAccess);

        btnLogin.setOnClickListener(v -> attemptLogin());

        // Presentation Access Fallback
        if (PresentationAuthManager.isPresentationAccessEnabled() && btnPresentationAccess != null) {
            btnPresentationAccess.setVisibility(View.VISIBLE);
            btnPresentationAccess.setOnClickListener(v -> {
                startActivity(new Intent(this, PresentationAccessActivity.class));
            });
        } else if (btnPresentationAccess != null) {
            btnPresentationAccess.setVisibility(View.GONE);
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if presentation credentials were submitted directly in the login fields
        if (PresentationAuthManager.isPresentationAccessEnabled() && PresentationAuthManager.isPresentationCredential(email)) {
            String role = PresentationAuthManager.authenticate(this, email, password);
            if (role != null) {
                navigateToRoleDashboard(role);
                return;
            } else {
                Toast.makeText(this, "Invalid presentation credentials. Use Provider@123, Ceo@123, or Employee@123", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Normal Production Authentication Flow (Android -> Retrofit -> Spring Boot -> PostgreSQL)
        authViewModel.login(email, password).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    if (resource.data != null) {
                        sessionManager.saveSession(
                            resource.data.getAccessToken(),
                            resource.data.getRole(),
                            resource.data.getEmployeeId(),
                            resource.data.getCeoId(),
                            resource.data.getProviderId(),
                            resource.data.getDisplayName()
                        );
                        navigateToRoleDashboard(resource.data.getRole());
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void navigateToRoleDashboard(String role) {
        Intent intent;
        if ("ROLE_CEO".equals(role)) {
            intent = new Intent(this, CeoDashboardActivity.class);
        } else if ("ROLE_PROVIDER".equals(role)) {
            intent = new Intent(this, ProviderHomeActivity.class);
        } else {
            intent = new Intent(this, EmployeeDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
