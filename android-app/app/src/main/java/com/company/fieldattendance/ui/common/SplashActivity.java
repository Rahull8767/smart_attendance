package com.company.fieldattendance.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.company.fieldattendance.ui.employee.EmployeeDashboardActivity;
import com.company.fieldattendance.ui.ceo.CeoDashboardActivity;
import com.company.fieldattendance.ui.provider.ProviderDashboardActivity;

public class SplashActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        
        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                String role = sessionManager.getRole();
                if ("ROLE_CEO".equals(role)) {
                    startActivity(new Intent(this, CeoDashboardActivity.class));
                } else if ("ROLE_PROVIDER".equals(role)) {
                    startActivity(new Intent(this, ProviderDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, EmployeeDashboardActivity.class));
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}
