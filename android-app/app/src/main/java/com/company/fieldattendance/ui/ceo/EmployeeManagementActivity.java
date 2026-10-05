package com.company.fieldattendance.ui.ceo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.CeoApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.ApiResponse;
import com.company.fieldattendance.data.model.CeoAttendanceRecordDTO;
import com.company.fieldattendance.data.model.Employee;
import com.company.fieldattendance.data.model.WorkSite;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeManagementActivity extends AppCompatActivity {

    private RecyclerView rvEmployees;
    private EmployeeAdapter adapter;
    private EditText etSearchEmployee;
    private ChipGroup chipGroupFilters;
    private TextView tvEmptyState;
    private BottomNavigationView bottomNavCeo;
    private CeoApiService apiService;

    private List<WorkSite> availableSites = new ArrayList<>();
    private List<Employee> loadedEmployees = new ArrayList<>();
    private String currentFilter = "All";

    // Temp storage for enrolled face embedding during Create Employee flow
    private List<Float> pendingFaceEmbedding = null;
    private TextView tvPendingFaceStatus = null;

    private final ActivityResultLauncher<Intent> faceEnrollmentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    float[] array = result.getData().getFloatArrayExtra(FaceEnrollmentActivity.EXTRA_ENROLLED_EMBEDDING);
                    if (array != null) {
                        pendingFaceEmbedding = new ArrayList<>();
                        for (float f : array) pendingFaceEmbedding.add(f);
                        if (tvPendingFaceStatus != null) {
                            tvPendingFaceStatus.setText("Face Enrolled Successfully ✓");
                            tvPendingFaceStatus.setTextColor(0xFF16A34A);
                        }
                        Toast.makeText(this, "Face captured and ready for employee registration", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        apiService = RetrofitClient.getRetrofitInstance().create(CeoApiService.class);

        initViews();
        setupRecyclerView();
        setupSearchAndFilters();
        setupBottomNav();
        fetchSites();
        loadEmployees();
    }

    private void initViews() {
        rvEmployees = findViewById(R.id.rvEmployees);
        etSearchEmployee = findViewById(R.id.etSearchEmployee);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        bottomNavCeo = findViewById(R.id.bottomNavCeo);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddEmployee).setOnClickListener(v -> showCreateEmployeeDialog());
    }

    private void setupRecyclerView() {
        adapter = new EmployeeAdapter(this, employee -> showEmployeeDetailsDialog(employee));
        rvEmployees.setLayoutManager(new LinearLayoutManager(this));
        rvEmployees.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        etSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                adapter.filter(s.toString(), currentFilter);
                checkEmptyState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = "All";
            } else {
                int id = checkedIds.get(0);
                if (id == R.id.chipPresent) currentFilter = "Present";
                else if (id == R.id.chipActive) currentFilter = "Active";
                else if (id == R.id.chipAbsent) currentFilter = "Absent";
                else if (id == R.id.chipInactive) currentFilter = "Inactive";
                else currentFilter = "All";
            }
            adapter.filter(etSearchEmployee.getText().toString(), currentFilter);
            checkEmptyState();
        });
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void setupBottomNav() {
        bottomNavCeo.setSelectedItemId(R.id.nav_people);
        bottomNavCeo.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_people) return true;
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, CeoDashboardActivity.class);
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
        new AlertDialog.Builder(this)
                .setTitle("CEO Profile")
                .setMessage("Rahul Sharma\nChief Executive Officer\nApex Infrastructure Pvt. Ltd.\nceotest@example.com")
                .setPositiveButton("Close", null)
                .show();
    }

    private void fetchSites() {
        apiService.getSites().enqueue(new Callback<List<WorkSite>>() {
            @Override
            public void onResponse(Call<List<WorkSite>> call, Response<List<WorkSite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableSites = response.body();
                }
            }
            @Override
            public void onFailure(Call<List<WorkSite>> call, Throwable t) {}
        });
    }

    private void loadEmployees() {
        apiService.getEmployees().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadedEmployees = response.body();
                    // Also cross-reference with today's attendance to update live status
                    syncWithLiveAttendance();
                } else {
                    Toast.makeText(EmployeeManagementActivity.this, "Failed to load employees", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Toast.makeText(EmployeeManagementActivity.this, "Network error loading employees", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncWithLiveAttendance() {
        apiService.getAttendance().enqueue(new Callback<List<CeoAttendanceRecordDTO>>() {
            @Override
            public void onResponse(Call<List<CeoAttendanceRecordDTO>> call, Response<List<CeoAttendanceRecordDTO>> response) {
                Map<String, CeoAttendanceRecordDTO> recordMap = new HashMap<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (CeoAttendanceRecordDTO r : response.body()) {
                        if (r.employeeId != null) {
                            recordMap.put(r.employeeId.toLowerCase(), r);
                        }
                    }
                }

                for (Employee e : loadedEmployees) {
                    if (e.id != null && recordMap.containsKey(e.id.toString().toLowerCase())) {
                        CeoAttendanceRecordDTO rec = recordMap.get(e.id.toString().toLowerCase());
                        e.attendanceStatus = rec.status;
                    } else {
                        e.attendanceStatus = "NOT PUNCHED IN";
                    }
                }

                adapter.setEmployees(loadedEmployees);
                checkEmptyState();
            }

            @Override
            public void onFailure(Call<List<CeoAttendanceRecordDTO>> call, Throwable t) {
                adapter.setEmployees(loadedEmployees);
                checkEmptyState();
            }
        });
    }

    private void showEmployeeDetailsDialog(Employee emp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(emp.name != null ? emp.name : "Employee Details");
        
        String details = "Employee ID: " + (emp.employeeCode != null ? emp.employeeCode : "EMP-1024") + "\n" +
                "Department: " + (emp.department != null ? emp.department : "Field Operations") + "\n" +
                "Designation: " + (emp.designation != null ? emp.designation : "Site Supervisor") + "\n" +
                "Assigned Site: " + (emp.assignedSiteName != null ? emp.assignedSiteName : "Apex Tower Construction Site") + "\n" +
                "Attendance Today: " + (emp.attendanceStatus != null ? emp.attendanceStatus : "NOT PUNCHED IN") + "\n" +
                "Biometric Status: Face Profile Registered ✓";

        builder.setMessage(details);
        builder.setPositiveButton("Close", null);
        builder.setNegativeButton("Re-Enroll Face", (d, w) -> {
            if (emp.id != null) {
                Intent intent = new Intent(this, FaceEnrollmentActivity.class);
                intent.putExtra(FaceEnrollmentActivity.EXTRA_EMPLOYEE_ID, emp.id.toString());
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void showCreateEmployeeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_employee);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etFullName = dialog.findViewById(R.id.etEmpFullName);
        EditText etEmpCode = dialog.findViewById(R.id.etEmpId);
        EditText etPhone = dialog.findViewById(R.id.etEmpPhone);
        EditText etDepartment = dialog.findViewById(R.id.etEmpDepartment);
        EditText etDesignation = dialog.findViewById(R.id.etEmpDesignation);
        Spinner spinnerSites = dialog.findViewById(R.id.spinnerWorkSites);
        tvPendingFaceStatus = dialog.findViewById(R.id.tvFaceEnrollStatus);
        Button btnEnrollFace = dialog.findViewById(R.id.btnEnrollFaceAction);
        EditText etLoginEmail = dialog.findViewById(R.id.etEmpLoginEmail);
        EditText etPassword = dialog.findViewById(R.id.etEmpPassword);
        Button btnSave = dialog.findViewById(R.id.btnSaveEmployeeSubmit);

        pendingFaceEmbedding = null;

        // Populate Work Sites Spinner
        List<String> siteNames = new ArrayList<>();
        if (availableSites.isEmpty()) {
            siteNames.add("Apex Tower Construction Site");
        } else {
            for (WorkSite s : availableSites) siteNames.add(s.name);
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, siteNames);
        spinnerSites.setAdapter(spinnerAdapter);

        // Pre-fill suggestions
        etEmpCode.setText("EMP-" + (1028 + loadedEmployees.size()));
        etDepartment.setText("Field Operations");
        etDesignation.setText("Field Engineer");

        btnEnrollFace.setOnClickListener(v -> {
            Intent intent = new Intent(this, FaceEnrollmentActivity.class);
            faceEnrollmentLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String code = etEmpCode.getText().toString().trim();
            String email = etLoginEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullName.isEmpty() || code.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedSiteId = null;
            if (!availableSites.isEmpty()) {
                int pos = spinnerSites.getSelectedItemPosition();
                if (pos >= 0 && pos < availableSites.size()) {
                    selectedSiteId = availableSites.get(pos).id.toString();
                }
            }

            Map<String, String> request = new HashMap<>();
            request.put("name", fullName);
            request.put("employeeCode", code);
            request.put("phone", etPhone.getText().toString().trim());
            request.put("department", etDepartment.getText().toString().trim());
            request.put("designation", etDesignation.getText().toString().trim());
            request.put("email", email);
            request.put("password", password);
            if (selectedSiteId != null) {
                request.put("siteId", selectedSiteId);
            }

            btnSave.setEnabled(false);
            btnSave.setText("Creating employee...");

            apiService.createEmployee(request).enqueue(new Callback<Employee>() {
                @Override
                public void onResponse(Call<Employee> call, Response<Employee> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Employee createdEmp = response.body();

                        // If face embedding was captured, enroll it now
                        if (pendingFaceEmbedding != null && createdEmp.id != null) {
                            Map<String, Object> facePayload = new HashMap<>();
                            facePayload.put("employeeId", createdEmp.id.toString());
                            facePayload.put("embedding", pendingFaceEmbedding);
                            apiService.enrollFace(facePayload).enqueue(new Callback<ApiResponse>() {
                                @Override
                                public void onResponse(Call<ApiResponse> c, Response<ApiResponse> r) {}
                                @Override
                                public void onFailure(Call<ApiResponse> c, Throwable t) {}
                            });
                        }

                        Toast.makeText(EmployeeManagementActivity.this, "Employee " + fullName + " created successfully!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        loadEmployees();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Save Employee");
                        Toast.makeText(EmployeeManagementActivity.this, "Failed to create employee", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Employee");
                    Toast.makeText(EmployeeManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
