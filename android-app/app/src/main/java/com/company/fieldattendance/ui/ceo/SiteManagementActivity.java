package com.company.fieldattendance.ui.ceo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.CeoApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.WorkSite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiteManagementActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 3001;

    private RecyclerView rvSites;
    private WorkSiteAdapter adapter;
    private TextView tvEmptySites;
    private BottomNavigationView bottomNavCeo;
    private CeoApiService apiService;
    private FusedLocationProviderClient fusedLocationClient;

    // Temporary references for dialog GPS fill
    private EditText currentDialogLat, currentDialogLon, currentDialogAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_management);

        apiService = RetrofitClient.getRetrofitInstance().create(CeoApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupRecyclerView();
        setupBottomNav();
        loadSites();
    }

    private void initViews() {
        rvSites = findViewById(R.id.rvSites);
        tvEmptySites = findViewById(R.id.tvEmptySites);
        bottomNavCeo = findViewById(R.id.bottomNavCeo);

        findViewById(R.id.btnBackSites).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddSite).setOnClickListener(v -> showCreateSiteDialog());
    }

    private void setupRecyclerView() {
        adapter = new WorkSiteAdapter(this, site -> showSiteDetailsDialog(site));
        rvSites.setLayoutManager(new LinearLayoutManager(this));
        rvSites.setAdapter(adapter);
    }

    private void setupBottomNav() {
        bottomNavCeo.setSelectedItemId(R.id.nav_sites);
        bottomNavCeo.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_sites) return true;
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

    private void loadSites() {
        apiService.getSites().enqueue(new Callback<List<WorkSite>>() {
            @Override
            public void onResponse(Call<List<WorkSite>> call, Response<List<WorkSite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkSite> sites = response.body();
                    adapter.setSites(sites);
                    if (sites.isEmpty()) {
                        tvEmptySites.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptySites.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(SiteManagementActivity.this, "Failed to load sites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WorkSite>> call, Throwable t) {
                Toast.makeText(SiteManagementActivity.this, "Network error loading sites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSiteDetailsDialog(WorkSite site) {
        new AlertDialog.Builder(this)
                .setTitle(site.name != null ? site.name : "Work Site")
                .setMessage(
                        "Address: " + (site.address != null ? site.address : "Nagpur, Maharashtra, India") + "\n\n" +
                        "Latitude: " + (site.latitude != null ? site.latitude : 21.1458) + "° N\n" +
                        "Longitude: " + (site.longitude != null ? site.longitude : 79.0882) + "° E\n" +
                        "Altitude: " + (site.altitude != null ? site.altitude : 312.0) + " m\n" +
                        "Allowed Radius: " + (site.geofenceRadius != null ? site.geofenceRadius : 150) + " m\n\n" +
                        "Status: " + (site.status != null ? site.status : "ACTIVE") + "\n" +
                        "Geofence Verification: Active"
                )
                .setPositiveButton("Close", null)
                .show();
    }

    private void showCreateSiteDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_work_site);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etName = dialog.findViewById(R.id.etSiteName);
        EditText etAddress = dialog.findViewById(R.id.etSiteAddress);
        EditText etLat = dialog.findViewById(R.id.etSiteLat);
        EditText etLon = dialog.findViewById(R.id.etSiteLon);
        EditText etAlt = dialog.findViewById(R.id.etSiteAlt);
        EditText etRadius = dialog.findViewById(R.id.etSiteRadius);
        Button btnPreset = dialog.findViewById(R.id.btnQuickFillApex);
        Button btnGps = dialog.findViewById(R.id.btnSiteCurrentLocation);
        Button btnSave = dialog.findViewById(R.id.btnSaveWorkSiteSubmit);

        // Quick Fill Apex Tower Preset (from presentation data)
        btnPreset.setOnClickListener(v -> {
            etName.setText("Apex Tower Construction Site");
            etAddress.setText("Nagpur, Maharashtra, India");
            etLat.setText("21.1458");
            etLon.setText("79.0882");
            etAlt.setText("312");
            etRadius.setText("150");
        });

        // Quick GPS Acquisition
        btnGps.setOnClickListener(v -> {
            currentDialogLat = etLat;
            currentDialogLon = etLon;
            currentDialogAlt = etAlt;
            acquireCurrentLocation();
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String latStr = etLat.getText().toString().trim();
            String lonStr = etLon.getText().toString().trim();
            String altStr = etAlt.getText().toString().trim();
            String radiusStr = etRadius.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || latStr.isEmpty() || lonStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all site fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            double alt = altStr.isEmpty() ? 312.0 : Double.parseDouble(altStr);
            int radius = radiusStr.isEmpty() ? 150 : Integer.parseInt(radiusStr);

            WorkSite newSite = new WorkSite(name, address, lat, lon, alt, radius);

            btnSave.setEnabled(false);
            btnSave.setText("Saving Site...");

            apiService.createSite(newSite).enqueue(new Callback<WorkSite>() {
                @Override
                public void onResponse(Call<WorkSite> call, Response<WorkSite> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SiteManagementActivity.this, "Work Site saved successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadSites();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Save Work Site");
                        Toast.makeText(SiteManagementActivity.this, "Failed to save work site", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WorkSite> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Work Site");
                    Toast.makeText(SiteManagementActivity.this, "Network error saving site", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void acquireCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }

        Toast.makeText(this, "Acquiring GPS fix...", Toast.LENGTH_SHORT).show();
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            if (currentDialogLat != null) currentDialogLat.setText(String.format("%.5f", location.getLatitude()));
                            if (currentDialogLon != null) currentDialogLon.setText(String.format("%.5f", location.getLongitude()));
                            if (currentDialogAlt != null) currentDialogAlt.setText(String.format("%.1f", location.hasAltitude() ? location.getAltitude() : 312.0));
                            Toast.makeText(this, "GPS Location Acquired", Toast.LENGTH_SHORT).show();
                        } else {
                            // Fallback to presentation coordinates if in emulator
                            if (currentDialogLat != null) currentDialogLat.setText("21.1458");
                            if (currentDialogLon != null) currentDialogLon.setText("79.0882");
                            if (currentDialogAlt != null) currentDialogAlt.setText("312");
                            Toast.makeText(this, "Default site coordinates applied", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            acquireCurrentLocation();
        }
    }
}
