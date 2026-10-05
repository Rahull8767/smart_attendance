package com.company.fieldattendance.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.PresentationAuthManager;
import com.company.fieldattendance.data.local.PresentationDataManager;
import com.company.fieldattendance.ui.ceo.CeoDashboardActivity;
import com.company.fieldattendance.ui.employee.EmployeeDashboardActivity;
import com.company.fieldattendance.ui.provider.ProviderHomeActivity;

public class PresentationAccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation_access);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Role 1: Provider
        findViewById(R.id.btnContinueProvider).setOnClickListener(v -> {
            PresentationAuthManager.loginAsRole(this, PresentationAuthManager.ROLE_PROVIDER);
            Intent intent = new Intent(this, ProviderHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Role 2: CEO
        findViewById(R.id.btnContinueCeo).setOnClickListener(v -> {
            PresentationAuthManager.loginAsRole(this, PresentationAuthManager.ROLE_CEO);
            Intent intent = new Intent(this, CeoDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Role 3: Employee
        findViewById(R.id.btnContinueEmployee).setOnClickListener(v -> {
            PresentationAuthManager.loginAsRole(this, PresentationAuthManager.ROLE_EMPLOYEE);
            Intent intent = new Intent(this, EmployeeDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Reset Presentation Data
        findViewById(R.id.btnResetPresData).setOnClickListener(v -> {
            PresentationDataManager.getInstance(this).resetPresentationState();
            Toast.makeText(this, "Presentation state reset (Rahul Sharma: NOT PUNCHED IN)", Toast.LENGTH_SHORT).show();
        });
    }
}
