package com.company.fieldattendance.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {
    
    private SessionManager sessionManager;
    private Button btnLogout, btnPunchIn;
    private TextView tvPunchStatus, tvDemoBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        sessionManager = new SessionManager(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnPunchIn = findViewById(R.id.btnPunch);
        tvPunchStatus = findViewById(R.id.tvPunchStatus);
        tvDemoBadge = findViewById(R.id.tvDemoBadge);

        tvDemoBadge.setVisibility(View.GONE);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnPunchIn.setOnClickListener(v -> {
            checkLocationPermissionsAndStart();
        });
    }

    private void checkLocationPermissionsAndStart() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            fetchLocationAndVerify();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndVerify();
            } else {
                android.widget.Toast.makeText(this, "Location permission is required to punch in.", android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchLocationAndVerify() {
        android.widget.Toast.makeText(this, "Fetching location...", android.widget.Toast.LENGTH_SHORT).show();
        com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);

        try {
            fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            verifyLocationWithBackend(location);
                        } else {
                            android.widget.Toast.makeText(this, "Failed to get location. Ensure GPS is on.", android.widget.Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void verifyLocationWithBackend(android.location.Location location) {
        android.widget.Toast.makeText(this, "Verifying geofence...", android.widget.Toast.LENGTH_SHORT).show();
        
        com.company.fieldattendance.data.api.ApiService apiService = com.company.fieldattendance.data.api.RetrofitClient.getRetrofitInstance().create(com.company.fieldattendance.data.api.ApiService.class);
        
        com.company.fieldattendance.data.model.LocationVerificationRequest request = new com.company.fieldattendance.data.model.LocationVerificationRequest(
                java.util.UUID.fromString(sessionManager.getEmployeeId()),
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.isFromMockProvider()
        );

        apiService.verifyLocation(request).enqueue(new retrofit2.Callback<com.company.fieldattendance.data.model.LocationVerificationResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.company.fieldattendance.data.model.LocationVerificationResponse> call, retrofit2.Response<com.company.fieldattendance.data.model.LocationVerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().verified) {
                        // Location verified successfully, proceed to Face Verification
                        Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                        intent.putExtra("LAT", location.getLatitude());
                        intent.putExtra("LON", location.getLongitude());
                        intent.putExtra("ACC", location.getAccuracy());
                        startActivity(intent);
                    } else {
                        android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Geofence failed: " + response.body().message, android.widget.Toast.LENGTH_LONG).show();
                    }
                } else {
                    android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Location verification failed on server", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.company.fieldattendance.data.model.LocationVerificationResponse> call, Throwable t) {
                android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Network error during location verification", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
