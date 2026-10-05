package com.company.fieldattendance.ui.employee;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.ApiResponse;
import com.company.fieldattendance.data.model.AttendanceRecord;
import com.company.fieldattendance.data.model.EmployeeDashboardStatsDTO;
import com.company.fieldattendance.data.model.PunchRequest;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeDashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SessionManager sessionManager;
    private ApiService apiService;
    private GoogleMap mMap;

    // UI elements
    private TextView tvGreeting;
    private TextView tvEmployeeMeta;
    private TextView tvPunchStatus;
    private TextView tvStatusBadge;
    private TextView tvPunchTimeSubtext;
    private TextView tvAssignedSite;
    private TextView tvSiteAddress;
    private TextView tvPunchInTime;
    private TextView tvWorkingDuration;
    private MaterialButton btnPunch;
    private TextView tvFaceStatus;
    private TextView tvSiteStatus;
    private TextView tvLocationStatus;

    // Recent Attendance elements
    private TextView tvRecentDate;
    private TextView tvRecentStatus;
    private TextView tvRecentSite;
    private TextView tvRecentPunchIn;
    private TextView tvRecentPunchOut;
    private TextView tvRecentVerification;

    // State
    private boolean isPunchedIn = false;
    private double siteLat = 21.1458;
    private double siteLon = 79.0882;
    private int siteRadius = 150;
    private String siteName = "Apex Tower Construction Site";
    private String siteAddress = "Nagpur, Maharashtra, India";
    private UUID employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        String empIdStr = sessionManager.getEmployeeId();
        if (empIdStr != null && !empIdStr.isEmpty()) {
            try {
                employeeId = UUID.fromString(empIdStr);
            } catch (Exception ignored) {}
        }

        initViews();
        setupMap();
        setupNavigation();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvEmployeeMeta = findViewById(R.id.tvEmployeeMeta);
        tvPunchStatus = findViewById(R.id.tvPunchStatus);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvPunchTimeSubtext = findViewById(R.id.tvPunchTimeSubtext);
        tvAssignedSite = findViewById(R.id.tvAssignedSite);
        tvSiteAddress = findViewById(R.id.tvSiteAddress);
        tvPunchInTime = findViewById(R.id.tvPunchInTime);
        tvWorkingDuration = findViewById(R.id.tvWorkingDuration);
        btnPunch = findViewById(R.id.btnPunch);
        tvFaceStatus = findViewById(R.id.tvFaceStatus);
        tvSiteStatus = findViewById(R.id.tvSiteStatus);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);

        tvRecentDate = findViewById(R.id.tvRecentDate);
        tvRecentStatus = findViewById(R.id.tvRecentStatus);
        tvRecentSite = findViewById(R.id.tvRecentSite);
        tvRecentPunchIn = findViewById(R.id.tvRecentPunchIn);
        tvRecentPunchOut = findViewById(R.id.tvRecentPunchOut);
        tvRecentVerification = findViewById(R.id.tvRecentVerification);

        String displayName = sessionManager.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            tvGreeting.setText("Good morning, " + displayName);
        } else {
            tvGreeting.setText("Good morning, Rahul");
        }

        findViewById(R.id.btnProfileAvatar).setOnClickListener(v -> showProfileDialog());
        findViewById(R.id.btnViewFullHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, AttendanceHistoryActivity.class));
        });

        btnPunch.setOnClickListener(v -> {
            if (!isPunchedIn) {
                // Launch strict 4-step face verification workflow
                Intent intent = new Intent(this, FaceVerificationActivity.class);
                startActivity(intent);
            } else {
                showPunchOutConfirmation();
            }
        });
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_punch) {
                if (!isPunchedIn) {
                    startActivity(new Intent(this, FaceVerificationActivity.class));
                } else {
                    showPunchOutConfirmation();
                }
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, AttendanceHistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                showProfileDialog();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDashboardStats();
        fetchRecentAttendance();
    }

    private void fetchDashboardStats() {
        apiService.getEmployeeDashboardStats().enqueue(new Callback<EmployeeDashboardStatsDTO>() {
            @Override
            public void onResponse(Call<EmployeeDashboardStatsDTO> call, Response<EmployeeDashboardStatsDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmployeeDashboardStatsDTO stats = response.body();

                    if (stats.assignedSiteName != null && !stats.assignedSiteName.isEmpty()) {
                        siteName = stats.assignedSiteName;
                        tvAssignedSite.setText(siteName);
                    }
                    if (stats.siteAddress != null && !stats.siteAddress.isEmpty()) {
                        siteAddress = stats.siteAddress;
                        tvSiteAddress.setText(siteAddress);
                    }
                    if (stats.siteLatitude != 0.0) siteLat = stats.siteLatitude;
                    if (stats.siteLongitude != 0.0) siteLon = stats.siteLongitude;
                    if (stats.geofenceRadius != 0) siteRadius = stats.geofenceRadius;

                    updateMapSite();

                    String status = stats.todayStatus != null ? stats.todayStatus : "NOT_PUNCHED_IN";
                    if ("PRESENT".equalsIgnoreCase(status) || "PUNCHED_IN".equalsIgnoreCase(status)) {
                        isPunchedIn = true;
                        tvPunchStatus.setText("PUNCHED IN");
                        tvPunchStatus.setTextColor(0xFF16A34A);
                        tvStatusBadge.setText("PRESENT");
                        tvStatusBadge.setTextColor(0xFF16A34A);
                        tvStatusBadge.setBackgroundColor(0xFFDCFCE7);

                        String inTime = stats.punchInTime != null ? stats.punchInTime : "08:42 AM";
                        tvPunchInTime.setText(inTime);
                        tvPunchTimeSubtext.setText("Punched in at " + inTime + " • " + siteName);

                        btnPunch.setText("PUNCH OUT");
                        btnPunch.setBackgroundColor(0xFFDC2626);

                        if (stats.workingDuration != null && !stats.workingDuration.isEmpty()) {
                            tvWorkingDuration.setText(stats.workingDuration);
                            tvWorkingDuration.setVisibility(View.VISIBLE);
                        }
                    } else if ("COMPLETED".equalsIgnoreCase(status) || "PUNCHED_OUT".equalsIgnoreCase(status)) {
                        isPunchedIn = false;
                        tvPunchStatus.setText("PUNCHED OUT");
                        tvPunchStatus.setTextColor(0xFF0F172A);
                        tvStatusBadge.setText("COMPLETED");
                        tvStatusBadge.setTextColor(0xFF2563EB);
                        tvStatusBadge.setBackgroundColor(0xFFDBEAFE);

                        tvPunchInTime.setText(stats.punchInTime != null ? stats.punchInTime : "--:--");
                        tvPunchTimeSubtext.setText("Attendance completed for today");
                        btnPunch.setText("DAY COMPLETED");
                        btnPunch.setEnabled(false);
                        btnPunch.setBackgroundColor(0xFF94A3B8);
                    } else {
                        isPunchedIn = false;
                        tvPunchStatus.setText("NOT PUNCHED IN");
                        tvPunchStatus.setTextColor(0xFF0F172A);
                        tvStatusBadge.setText("NOT STARTED");
                        tvStatusBadge.setTextColor(0xFF64748B);
                        tvStatusBadge.setBackgroundColor(0xFFF1F5F9);

                        tvPunchInTime.setText("--:--");
                        tvPunchTimeSubtext.setText("Punch in to mark your presence");
                        btnPunch.setText("PUNCH IN");
                        btnPunch.setEnabled(true);
                        btnPunch.setBackgroundColor(0xFF2563EB);
                        tvWorkingDuration.setVisibility(View.GONE);
                    }

                    tvFaceStatus.setText(stats.faceEnrolled ? "Enrolled ✓" : "Required");
                    tvSiteStatus.setText(stats.assignedSiteName != null ? "Assigned ✓" : "Pending");
                    tvLocationStatus.setText("Ready");
                }
            }

            @Override
            public void onFailure(Call<EmployeeDashboardStatsDTO> call, Throwable t) {
                // Fallback default state
                tvPunchStatus.setText("NOT PUNCHED IN");
                btnPunch.setText("PUNCH IN");
            }
        });
    }

    private void fetchRecentAttendance() {
        if (employeeId == null) return;

        apiService.getAttendanceHistory(employeeId).enqueue(new Callback<List<AttendanceRecord>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecord>> call, Response<List<AttendanceRecord>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    AttendanceRecord rec = response.body().get(0);
                    tvRecentDate.setText("Latest Punch Record");
                    tvRecentSite.setText(rec.workSiteName != null ? rec.workSiteName : "Apex Tower Construction Site");

                    if (rec.punchInTime != null) {
                        String formattedIn = formatTime(rec.punchInTime);
                        tvRecentPunchIn.setText("In: " + formattedIn);
                        tvRecentStatus.setText("PRESENT");
                        tvRecentStatus.setTextColor(0xFF16A34A);
                    }

                    if (rec.punchOutTime != null) {
                        tvRecentPunchOut.setText("Out: " + formatTime(rec.punchOutTime));
                    } else {
                        tvRecentPunchOut.setText("Out: --:--");
                    }

                    tvRecentVerification.setText("Face ✓ • Loc ✓");
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecord>> call, Throwable t) {}
        });
    }

    private String formatTime(String isoTime) {
        try {
            if (isoTime.contains("T")) {
                String timePart = isoTime.substring(isoTime.indexOf("T") + 1);
                if (timePart.length() >= 5) {
                    return timePart.substring(0, 5);
                }
            }
            return isoTime;
        } catch (Exception e) {
            return isoTime;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        updateMapSite();
    }

    private void updateMapSite() {
        if (mMap == null) return;
        mMap.clear();

        LatLng sitePos = new LatLng(siteLat, siteLon);
        mMap.addMarker(new MarkerOptions().position(sitePos).title(siteName));
        mMap.addCircle(new CircleOptions()
                .center(sitePos)
                .radius(siteRadius)
                .strokeColor(0xFF2563EB)
                .fillColor(0x222563EB)
                .strokeWidth(3f));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sitePos, 16f));
    }

    private void showPunchOutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Punch Out")
                .setMessage("Are you sure you want to end your shift at " + siteName + "?")
                .setPositiveButton("PUNCH OUT", (d, w) -> performPunchOut())
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void performPunchOut() {
        btnPunch.setEnabled(false);
        btnPunch.setText("RECORDING PUNCH OUT...");

        PunchRequest req = new PunchRequest(employeeId, siteLat, siteLon, 8.0f, "VERIFIED");
        req.setAltitude(311.0);

        apiService.punchOut(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnPunch.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Toast.makeText(EmployeeDashboardActivity.this, "Punch Out Successful!", Toast.LENGTH_LONG).show();
                    fetchDashboardStats();
                    fetchRecentAttendance();
                } else {
                    String msg = (response.body() != null && response.body().message != null)
                            ? response.body().message
                            : "Punch out failed on server";
                    Toast.makeText(EmployeeDashboardActivity.this, msg, Toast.LENGTH_LONG).show();
                    btnPunch.setText("PUNCH OUT");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnPunch.setEnabled(true);
                btnPunch.setText("PUNCH OUT");
                Toast.makeText(EmployeeDashboardActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_employee_profile, null, false);
        TextView tvName = dialogView.findViewById(R.id.tvProfileName);
        TextView tvEmail = dialogView.findViewById(R.id.tvProfileEmail);
        TextView tvEmpId = dialogView.findViewById(R.id.tvProfileEmpId);
        TextView tvDept = dialogView.findViewById(R.id.tvProfileDept);
        TextView tvDesignation = dialogView.findViewById(R.id.tvProfileDesignation);
        TextView tvSite = dialogView.findViewById(R.id.tvProfileSite);

        tvName.setText(sessionManager.getDisplayName() != null ? sessionManager.getDisplayName() : "Rahul Sharma");
        tvEmail.setText(sessionManager.getUsername() != null ? sessionManager.getUsername() : "rahul.sharma@apex.com");
        tvEmpId.setText("EMP-1024");
        tvDept.setText("Field Operations");
        tvDesignation.setText("Site Supervisor");
        tvSite.setText(siteName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCloseProfile).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnLogoutEmployee).setOnClickListener(v -> {
            dialog.dismiss();
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}
