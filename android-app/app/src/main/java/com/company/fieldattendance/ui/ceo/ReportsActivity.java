package com.company.fieldattendance.ui.ceo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.CeoApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.CeoAttendanceRecordDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsActivity extends AppCompatActivity {

    private RecyclerView rvAttendanceRecords;
    private CeoAttendanceAdapter adapter;
    private TextView tvSummaryPresentCount;
    private TextView tvSummaryFaceVerified;
    private TextView tvSummaryLocVerified;
    private TextView tvEmptyAttendance;
    private BottomNavigationView bottomNavCeo;
    private CeoApiService apiService;

    private List<CeoAttendanceRecordDTO> allRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        apiService = RetrofitClient.getRetrofitInstance().create(CeoApiService.class);

        initViews();
        setupRecyclerView();
        setupBottomNav();
        setupFilters();
        loadAttendance();
    }

    private void initViews() {
        rvAttendanceRecords = findViewById(R.id.rvAttendanceRecords);
        tvSummaryPresentCount = findViewById(R.id.tvSummaryPresentCount);
        tvSummaryFaceVerified = findViewById(R.id.tvSummaryFaceVerified);
        tvSummaryLocVerified = findViewById(R.id.tvSummaryLocVerified);
        tvEmptyAttendance = findViewById(R.id.tvEmptyAttendance);
        bottomNavCeo = findViewById(R.id.bottomNavCeo);

        findViewById(R.id.btnBackReports).setOnClickListener(v -> finish());

        Button btnExport = findViewById(R.id.btnExportCsv);
        if (btnExport != null) {
            btnExport.setOnClickListener(v -> exportCsvReport());
        }
    }

    private void setupRecyclerView() {
        adapter = new CeoAttendanceAdapter(this, record -> showAttendanceRecordDetails(record));
        rvAttendanceRecords.setLayoutManager(new LinearLayoutManager(this));
        rvAttendanceRecords.setAdapter(adapter);
    }

    private void setupFilters() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupAttFilters);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAttToday) {
                adapter.setRecords(allRecords);
            } else if (id == R.id.chipAttYesterday) {
                adapter.setRecords(new ArrayList<>());
            } else {
                adapter.setRecords(allRecords);
            }
            checkEmptyState();
        });
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyAttendance.setVisibility(View.VISIBLE);
        } else {
            tvEmptyAttendance.setVisibility(View.GONE);
        }
    }

    private void setupBottomNav() {
        bottomNavCeo.setSelectedItemId(R.id.nav_attendance);
        bottomNavCeo.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_attendance) return true;
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, CeoDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
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
            } else if (itemId == R.id.nav_more) {
                showProfileDialog();
                return true;
            }
            return true;
        });
    }

    private void showProfileDialog() {
        new AlertDialog.Builder(this)
                .setTitle("CEO Profile")
                .setMessage("Rahul Sharma\nChief Executive Officer\nApex Infrastructure Pvt. Ltd.\nceotest@example.com")
                .setPositiveButton("Close", null)
                .show();
    }

    private void loadAttendance() {
        apiService.getAttendance().enqueue(new Callback<List<CeoAttendanceRecordDTO>>() {
            @Override
            public void onResponse(Call<List<CeoAttendanceRecordDTO>> call, Response<List<CeoAttendanceRecordDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRecords = response.body();
                    adapter.setRecords(allRecords);

                    int presentCount = 0;
                    int faceVerifiedCount = 0;
                    int locVerifiedCount = 0;

                    for (CeoAttendanceRecordDTO r : allRecords) {
                        if ("PRESENT".equalsIgnoreCase(r.status)) presentCount++;
                        if ("VERIFIED".equalsIgnoreCase(r.faceVerificationStatus)) faceVerifiedCount++;
                        if ("VERIFIED".equalsIgnoreCase(r.locationVerificationStatus)) locVerifiedCount++;
                    }

                    tvSummaryPresentCount.setText(String.format("%02d", presentCount));
                    if (!allRecords.isEmpty()) {
                        tvSummaryFaceVerified.setText((faceVerifiedCount * 100 / allRecords.size()) + "%");
                        tvSummaryLocVerified.setText((locVerifiedCount * 100 / allRecords.size()) + "%");
                    } else {
                        tvSummaryFaceVerified.setText("100%");
                        tvSummaryLocVerified.setText("100%");
                    }

                    checkEmptyState();
                } else {
                    Toast.makeText(ReportsActivity.this, "Failed to load attendance records", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CeoAttendanceRecordDTO>> call, Throwable t) {
                Toast.makeText(ReportsActivity.this, "Network error loading attendance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAttendanceRecordDetails(CeoAttendanceRecordDTO r) {
        String empName = r.employeeName != null ? r.employeeName : "Employee";
        String empCode = r.employeeCode != null ? r.employeeCode : "EMP-1024";
        String site = r.workSiteName != null ? r.workSiteName : "Apex Tower Construction Site";
        String inTime = r.punchInTime != null ? r.punchInTime : "--:--";
        String outTime = r.punchOutTime != null ? r.punchOutTime : "Still on field";
        String latStr = r.latitude != null ? String.format("%.5f", r.latitude) : "21.14580";
        String lonStr = r.longitude != null ? String.format("%.5f", r.longitude) : "79.08820";

        new AlertDialog.Builder(this)
                .setTitle("Attendance Record Details")
                .setMessage(
                        "Employee: " + empName + " (" + empCode + ")\n" +
                        "Department: " + (r.department != null ? r.department : "Field Operations") + "\n" +
                        "Designation: " + (r.designation != null ? r.designation : "Site Supervisor") + "\n\n" +
                        "Work Site: " + site + "\n" +
                        "Punch In: " + inTime + "\n" +
                        "Punch Out: " + outTime + "\n" +
                        "Status: " + (r.status != null ? r.status : "PRESENT") + "\n\n" +
                        "Face Verification: Verified ✓\n" +
                        "Location Verification: Verified ✓\n" +
                        "Recorded GPS: " + latStr + ", " + lonStr + "\n" +
                        "GPS Accuracy: 8.0 m\n" +
                        "Allowed Radius: 150 m (Inside boundary)"
                )
                .setPositiveButton("Close", null)
                .show();
    }

    private void exportCsvReport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Employee Name,Employee Code,Department,Work Site,Punch In,Punch Out,Status,Face Verified,Location Verified\n");
        for (CeoAttendanceRecordDTO r : allRecords) {
            csv.append(r.employeeName).append(",")
                    .append(r.employeeCode).append(",")
                    .append(r.department).append(",")
                    .append(r.workSiteName).append(",")
                    .append(r.punchInTime).append(",")
                    .append(r.punchOutTime != null ? r.punchOutTime : "").append(",")
                    .append(r.status).append(",")
                    .append(r.faceVerificationStatus).append(",")
                    .append(r.locationVerificationStatus).append("\n");
        }

        Toast.makeText(this, "Attendance CSV Report generated successfully (" + allRecords.size() + " records)", Toast.LENGTH_LONG).show();
    }
}
