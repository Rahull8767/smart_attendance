package com.company.fieldattendance.ui.ceo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.CeoDashboardStatsDTO;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CeoDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvCeoGreeting;
    private TextView tvCeoCompany;
    private TextView tvPresentToday;
    private TextView tvAbsentToday;
    private TextView tvOnField;
    private TextView tvActiveSites;
    private TextView tvTotalEmployees;
    private TextView tvVerificationRate;
    private LinearLayout layoutRecentActivity;
    private BottomNavigationView bottomNavCeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceo_dashboard);

        sessionManager = new SessionManager(this);
        initViews();
        setupBottomNav();
    }

    private void initViews() {
        tvCeoGreeting = findViewById(R.id.tvCeoGreeting);
        tvCeoCompany = findViewById(R.id.tvCeoCompany);
        tvPresentToday = findViewById(R.id.tvPresentToday);
        tvAbsentToday = findViewById(R.id.tvAbsentToday);
        tvOnField = findViewById(R.id.tvOnField);
        tvActiveSites = findViewById(R.id.tvActiveSites);
        tvTotalEmployees = findViewById(R.id.tvTotalEmployees);
        tvVerificationRate = findViewById(R.id.tvVerificationRate);
        layoutRecentActivity = findViewById(R.id.layoutRecentActivity);
        bottomNavCeo = findViewById(R.id.bottomNavCeo);

        String name = sessionManager.getDisplayName();
        if (name != null && !name.isEmpty()) {
            tvCeoGreeting.setText("Good morning, " + name);
        } else {
            tvCeoGreeting.setText("Good morning, Rahul");
        }
        tvCeoCompany.setText("Apex Infrastructure Pvt. Ltd.");

        findViewById(R.id.btnCeoLogout).setOnClickListener(v -> performLogout());

        View btnViewAll = findViewById(R.id.btnViewAllAttendance);
        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> {
                startActivity(new Intent(this, ReportsActivity.class));
            });
        }
    }

    private void setupBottomNav() {
        bottomNavCeo.setSelectedItemId(R.id.nav_dashboard);
        bottomNavCeo.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                return true;
            } else if (itemId == R.id.nav_people) {
                Intent intent = new Intent(this, EmployeeManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_sites) {
                Intent intent = new Intent(this, SiteManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_attendance) {
                Intent intent = new Intent(this, ReportsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_more) {
                showProfileDialog();
                return true;
            }
            return true;
        });
    }

    private void showProfileDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("CEO Profile")
                .setMessage("Rahul Sharma\nChief Executive Officer\nApex Infrastructure Pvt. Ltd.\nceotest@example.com")
                .setPositiveButton("Logout", (d, w) -> performLogout())
                .setNegativeButton("Close", null)
                .show();
    }

    private void performLogout() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavCeo.setSelectedItemId(R.id.nav_dashboard);
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.getCeoDashboardStats().enqueue(new Callback<CeoDashboardStatsDTO>() {
            @Override
            public void onResponse(Call<CeoDashboardStatsDTO> call, Response<CeoDashboardStatsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CeoDashboardStatsDTO stats = response.body();
                    tvPresentToday.setText(String.format("%02d", stats.presentToday));
                    tvAbsentToday.setText(String.format("%02d", stats.absentToday));
                    tvOnField.setText(String.format("%02d", stats.onField));
                    tvActiveSites.setText(String.format("%02d", stats.activeSites));
                    tvTotalEmployees.setText(String.format("%02d", stats.totalEmployees));
                    tvVerificationRate.setText(String.format("%.1f%%", stats.verificationRate > 0 ? stats.verificationRate : 98.2));
                    
                    if (stats.companyName != null && !stats.companyName.isEmpty()) {
                        tvCeoCompany.setText(stats.companyName);
                    }

                    // Populate Recent Field Activity
                    populateRecentActivity(stats.recentActivity);
                }
            }

            @Override
            public void onFailure(Call<CeoDashboardStatsDTO> call, Throwable t) {
                Toast.makeText(CeoDashboardActivity.this, "Connecting to live metrics...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRecentActivity(java.util.List<CeoDashboardStatsDTO.RecentFieldActivityDTO> list) {
        layoutRecentActivity.removeAllViews();
        if (list == null || list.isEmpty()) {
            TextView emptyTv = new TextView(this);
            emptyTv.setText("No recent field activity recorded today");
            emptyTv.setTextColor(0xFF64748B);
            emptyTv.setPadding(8, 16, 8, 16);
            layoutRecentActivity.addView(emptyTv);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (CeoDashboardStatsDTO.RecentFieldActivityDTO act : list) {
            View itemView = inflater.inflate(R.layout.item_recent_activity, layoutRecentActivity, false);
            TextView tvInitials = itemView.findViewById(R.id.tvActivityInitials);
            TextView tvEmpName = itemView.findViewById(R.id.tvActivityEmpName);
            TextView tvSite = itemView.findViewById(R.id.tvActivitySite);
            TextView tvAction = itemView.findViewById(R.id.tvActivityAction);
            TextView tvTime = itemView.findViewById(R.id.tvActivityTime);

            tvEmpName.setText(act.employeeName);
            tvSite.setText(act.siteName);
            tvAction.setText(act.action);
            tvTime.setText(act.time);

            if (act.employeeName != null && !act.employeeName.isEmpty()) {
                String[] parts = act.employeeName.split(" ");
                if (parts.length >= 2) {
                    tvInitials.setText(("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase());
                } else {
                    tvInitials.setText(("" + act.employeeName.charAt(0)).toUpperCase());
                }
            }

            if ("Punch Out".equalsIgnoreCase(act.action)) {
                tvAction.setBackgroundColor(0xFFFEF3C7);
                tvAction.setTextColor(0xFFD97706);
            } else {
                tvAction.setBackgroundColor(0xFFDCFCE7);
                tvAction.setTextColor(0xFF16A34A);
            }

            layoutRecentActivity.addView(itemView);
        }
    }
}
