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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmployeeDashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SessionManager sessionManager;
    private Button btnPunchIn;
    private TextView tvPunchStatus;
    private TextView tvGreeting;
    private TextView tvEmployeeMeta;
    private TextView tvLocationStatus;
    private TextView tvFaceStatus;
    private TextView tvLocationDetails;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        sessionManager = new SessionManager(this);
        btnPunchIn = findViewById(R.id.btnPunch);
        tvPunchStatus = findViewById(R.id.tvPunchStatus);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvEmployeeMeta = findViewById(R.id.tvEmployeeMeta);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        tvFaceStatus = findViewById(R.id.tvFaceStatus);
        tvLocationDetails = findViewById(R.id.tvLocationDetails);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Bind data from session
        String name = sessionManager.getDisplayName();
        if (name != null) {
            tvGreeting.setText("Good morning, " + name + " \uD83D\uDC4B");
        }
        tvEmployeeMeta.setText("Employee Role Active");

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                android.widget.Toast.makeText(this, "Profile screen coming soon", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            }
            return true;
        });

        btnPunchIn.setOnClickListener(v -> {
            checkLocationPermissionsAndStart();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        com.company.fieldattendance.data.api.ApiService apiService = com.company.fieldattendance.data.api.RetrofitClient.getRetrofitInstance().create(com.company.fieldattendance.data.api.ApiService.class);
        apiService.getEmployeeDashboardStats().enqueue(new retrofit2.Callback<com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO>() {
            @Override
            public void onResponse(retrofit2.Call<com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO> call, retrofit2.Response<com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO stats = response.body();
                    tvPunchStatus.setText(stats.todayStatus.replace("_", " "));
                    tvFaceStatus.setText(stats.workingDuration);
                    
                    if (mMap != null && stats.siteLatitude != 0.0) {
                        LatLng sitePos = new LatLng(stats.siteLatitude, stats.siteLongitude);
                        mMap.clear();
                        mMap.addCircle(new com.google.android.gms.maps.model.CircleOptions()
                            .center(sitePos)
                            .radius(stats.geofenceRadius)
                            .strokeColor(getResources().getColor(R.color.primary, null))
                            .fillColor(0x2219C3B1) // 10% opacity primary
                            .strokeWidth(2f));
                        mMap.addMarker(new MarkerOptions().position(sitePos).title(stats.assignedSiteName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sitePos, 15f));
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO> call, Throwable t) {
                android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Failed to load dashboard data", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@androidx.annotation.NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // We will move the camera when we fetch the actual location
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
                tvLocationStatus.setText("Permission Denied");
                tvLocationStatus.setTextColor(android.graphics.Color.RED);
            }
        }
    }

    private void fetchLocationAndVerify() {
        tvLocationStatus.setText("Acquiring...");
        tvLocationStatus.setTextColor(getResources().getColor(R.color.warning, null));
        android.widget.Toast.makeText(this, "Fetching location...", android.widget.Toast.LENGTH_SHORT).show();
        com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);

        try {
            fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            tvLocationDetails.setText(String.format("Lat: %.5f, Lng: %.5f\nAccuracy: %.1fm", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
                            if (mMap != null) {
                                LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(currentPos).title("You are here"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 16f));
                            }
                            verifyLocationWithBackend(location);
                        } else {
                            tvLocationStatus.setText("Failed");
                            android.widget.Toast.makeText(this, "Failed to get location. Ensure GPS is on.", android.widget.Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void verifyLocationWithBackend(android.location.Location location) {
        tvLocationStatus.setText("Verifying...");
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
                        tvLocationStatus.setText("Verified");
                        tvLocationStatus.setTextColor(getResources().getColor(R.color.success, null));
                        // Location verified successfully, proceed to Face Verification
                        Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                        intent.putExtra("LAT", location.getLatitude());
                        intent.putExtra("LON", location.getLongitude());
                        intent.putExtra("ACC", location.getAccuracy());
                        startActivity(intent);
                    } else {
                        tvLocationStatus.setText("Out of Range");
                        tvLocationStatus.setTextColor(android.graphics.Color.RED);
                        android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Geofence failed: " + response.body().message, android.widget.Toast.LENGTH_LONG).show();
                    }
                } else {
                    tvLocationStatus.setText("Server Error");
                    android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Location verification failed on server", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.company.fieldattendance.data.model.LocationVerificationResponse> call, Throwable t) {
                tvLocationStatus.setText("Network Error");
                android.widget.Toast.makeText(EmployeeDashboardActivity.this, "Network error during location verification", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
