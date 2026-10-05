package com.company.fieldattendance.ui.employee;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.AttendanceRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private EmployeeHistoryAdapter adapter;
    private ProgressBar pbHistoryLoading;
    private View layoutEmptyHistory;
    private TextView tvPresentCount;
    private TextView tvTotalHours;

    private ApiService apiService;
    private SessionManager sessionManager;
    private UUID employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        sessionManager = new SessionManager(this);

        String empIdStr = sessionManager.getEmployeeId();
        if (empIdStr != null && !empIdStr.isEmpty()) {
            try {
                employeeId = UUID.fromString(empIdStr);
            } catch (Exception ignored) {}
        }

        initViews();
        loadHistory();
    }

    private void initViews() {
        findViewById(R.id.btnBackHistory).setOnClickListener(v -> finish());

        rvHistory = findViewById(R.id.rvHistory);
        pbHistoryLoading = findViewById(R.id.pbHistoryLoading);
        layoutEmptyHistory = findViewById(R.id.layoutEmptyHistory);
        tvPresentCount = findViewById(R.id.tvPresentCount);
        tvTotalHours = findViewById(R.id.tvTotalHours);

        adapter = new EmployeeHistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void loadHistory() {
        if (employeeId == null) {
            layoutEmptyHistory.setVisibility(View.VISIBLE);
            return;
        }

        pbHistoryLoading.setVisibility(View.VISIBLE);
        layoutEmptyHistory.setVisibility(View.GONE);

        apiService.getAttendanceHistory(employeeId).enqueue(new Callback<List<AttendanceRecord>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecord>> call, Response<List<AttendanceRecord>> response) {
                pbHistoryLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<AttendanceRecord> records = response.body();
                    if (records.isEmpty()) {
                        layoutEmptyHistory.setVisibility(View.VISIBLE);
                        tvPresentCount.setText("0");
                        tvTotalHours.setText("0h 0m");
                    } else {
                        layoutEmptyHistory.setVisibility(View.GONE);
                        adapter.setRecords(records);
                        computeSummary(records);
                    }
                } else {
                    layoutEmptyHistory.setVisibility(View.VISIBLE);
                    Toast.makeText(AttendanceHistoryActivity.this, "Unable to load attendance history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecord>> call, Throwable t) {
                pbHistoryLoading.setVisibility(View.GONE);
                layoutEmptyHistory.setVisibility(View.VISIBLE);
                Toast.makeText(AttendanceHistoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void computeSummary(List<AttendanceRecord> records) {
        int presentCount = records.size();
        long totalMinutes = 0;

        for (AttendanceRecord r : records) {
            if (r.punchInTime != null && r.punchOutTime != null) {
                try {
                    LocalDateTime start = LocalDateTime.parse(r.punchInTime);
                    LocalDateTime end = LocalDateTime.parse(r.punchOutTime);
                    totalMinutes += Duration.between(start, end).toMinutes();
                } catch (Exception ignored) {}
            } else if (r.punchInTime != null) {
                // In-progress shift estimate
                totalMinutes += 120; // 2h default
            }
        }

        tvPresentCount.setText(String.valueOf(presentCount));
        long hours = totalMinutes / 60;
        long mins = totalMinutes % 60;
        tvTotalHours.setText(String.format("%02dh %02dm", hours, mins));
    }
}
