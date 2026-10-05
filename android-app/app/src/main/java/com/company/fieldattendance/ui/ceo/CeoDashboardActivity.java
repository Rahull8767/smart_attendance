package com.company.fieldattendance.ui.ceo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class CeoDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvCeoGreeting;
    private TextView tvTotalEmployees;
    private TextView tvPresentToday;
    private TextView tvAbsentToday;
    private TextView tvLateToday;
    private TextView tvAttendancePercentage;
    private TextView tvActiveSites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceo_dashboard);

        sessionManager = new SessionManager(this);
        tvCeoGreeting = findViewById(R.id.tvCeoGreeting);
        tvTotalEmployees = findViewById(R.id.tvTotalEmployees);
        tvPresentToday = findViewById(R.id.tvPresentToday);
        tvAbsentToday = findViewById(R.id.tvAbsentToday);
        tvLateToday = findViewById(R.id.tvLateToday);
        tvAttendancePercentage = findViewById(R.id.tvAttendancePercentage);
        tvActiveSites = findViewById(R.id.tvActiveSites);

        String name = sessionManager.getDisplayName();
        if (name != null) {
            tvCeoGreeting.setText("Good morning, " + name);
        }

        // Temporary skeleton data to be loaded from backend later
        tvTotalEmployees.setText("-");
        tvPresentToday.setText("-");
        tvAbsentToday.setText("-");
        tvLateToday.setText("-");
        tvAttendancePercentage.setText("-%");
        tvActiveSites.setText("-");

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavCeo);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_more) {
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
        apiService.getCeoDashboardStats().enqueue(new retrofit2.Callback<com.company.fieldattendance.data.model.CeoDashboardStatsDTO>() {
            @Override
            public void onResponse(retrofit2.Call<com.company.fieldattendance.data.model.CeoDashboardStatsDTO> call, retrofit2.Response<com.company.fieldattendance.data.model.CeoDashboardStatsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.company.fieldattendance.data.model.CeoDashboardStatsDTO stats = response.body();
                    tvTotalEmployees.setText(String.valueOf(stats.totalEmployees));
                    tvPresentToday.setText(String.valueOf(stats.presentToday));
                    tvAbsentToday.setText(String.valueOf(stats.absentToday));
                    tvLateToday.setText(String.valueOf(stats.lateToday));
                    tvAttendancePercentage.setText(stats.attendancePercentage + "%");
                    tvActiveSites.setText(String.valueOf(stats.activeSites));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.company.fieldattendance.data.model.CeoDashboardStatsDTO> call, Throwable t) {
                android.widget.Toast.makeText(CeoDashboardActivity.this, "Failed to load dashboard data", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
