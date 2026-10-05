package com.company.fieldattendance.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class ProviderDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvProviderGreeting;
    private TextView tvTotalCeos;
    private TextView tvActiveCeos;
    private TextView tvNetworkEmployees;
    private TextView tvNetworkSites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        sessionManager = new SessionManager(this);
        tvProviderGreeting = findViewById(R.id.tvProviderGreeting);
        tvTotalCeos = findViewById(R.id.tvTotalCeos);
        tvActiveCeos = findViewById(R.id.tvActiveCeos);
        tvNetworkEmployees = findViewById(R.id.tvNetworkEmployees);
        tvNetworkSites = findViewById(R.id.tvNetworkSites);

        String name = sessionManager.getDisplayName();
        if (name != null) {
            tvProviderGreeting.setText("Good morning, " + name);
        }

        tvTotalCeos.setText("-");
        tvActiveCeos.setText("-");
        tvNetworkEmployees.setText("-");
        tvNetworkSites.setText("-");

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavProvider);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                android.widget.Toast.makeText(this, "Profile screen coming soon", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        com.company.fieldattendance.data.api.ApiService apiService = com.company.fieldattendance.data.api.RetrofitClient.getRetrofitInstance().create(com.company.fieldattendance.data.api.ApiService.class);
        apiService.getProviderDashboardStats().enqueue(new retrofit2.Callback<com.company.fieldattendance.data.model.ProviderDashboardStatsDTO>() {
            @Override
            public void onResponse(retrofit2.Call<com.company.fieldattendance.data.model.ProviderDashboardStatsDTO> call, retrofit2.Response<com.company.fieldattendance.data.model.ProviderDashboardStatsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.company.fieldattendance.data.model.ProviderDashboardStatsDTO stats = response.body();
                    tvTotalCeos.setText(String.valueOf(stats.totalCeos));
                    tvActiveCeos.setText(String.valueOf(stats.activeCeos));
                    tvNetworkEmployees.setText(String.valueOf(stats.totalEmployees));
                    tvNetworkSites.setText(String.valueOf(stats.totalSites));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.company.fieldattendance.data.model.ProviderDashboardStatsDTO> call, Throwable t) {
                android.widget.Toast.makeText(ProviderDashboardActivity.this, "Failed to load dashboard data", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
