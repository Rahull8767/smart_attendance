import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"

create_file(f"{java_dir}/utils/LocationManagerHelper.java", """
package com.company.fieldattendance.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class LocationManagerHelper {

    public interface LocationCallback {
        void onLocationReceived(Location location);
        void onError(String message);
    }

    public static void getCurrentLocation(Activity activity, LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED 
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Location permission not granted");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
            .addOnSuccessListener(activity, location -> {
                if (location != null) {
                    callback.onLocationReceived(location);
                } else {
                    callback.onError("Failed to obtain location");
                }
            })
            .addOnFailureListener(activity, e -> callback.onError("Location error: " + e.getMessage()));
    }
}
""")

create_file(f"{java_dir}/ui/employee/EmployeeDashboardActivity.java", """
package com.company.fieldattendance.ui.employee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.company.fieldattendance.utils.LocationManagerHelper;

public class EmployeeDashboardActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private SessionManager sessionManager;
    private Button btnLogout, btnPunchIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        sessionManager = new SessionManager(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnPunchIn = findViewById(R.id.btnPunchIn);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        
        btnPunchIn.setOnClickListener(v -> {
            checkPermissionsAndPunchIn();
        });
    }

    private void checkPermissionsAndPunchIn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 
                LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        performLocationVerification();
    }

    private void performLocationVerification() {
        Toast.makeText(this, "Acquiring high accuracy location...", Toast.LENGTH_SHORT).show();
        btnPunchIn.setEnabled(false);
        
        LocationManagerHelper.getCurrentLocation(this, new LocationManagerHelper.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                btnPunchIn.setEnabled(true);
                boolean isMock = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    isMock = location.isMock();
                } else {
                    isMock = location.isFromMockProvider();
                }
                
                String msg = "Location: " + location.getLatitude() + ", " + location.getLongitude() 
                           + "\\nAccuracy: " + location.getAccuracy() + "m"
                           + "\\nMock: " + isMock;
                Toast.makeText(EmployeeDashboardActivity.this, msg, Toast.LENGTH_LONG).show();
                
                // In Phase 6 we will send this to backend. For Phase 4, we display the result locally.
            }

            @Override
            public void onError(String message) {
                btnPunchIn.setEnabled(true);
                Toast.makeText(EmployeeDashboardActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performLocationVerification();
            } else {
                Toast.makeText(this, "Location permission is required to verify your field attendance.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
""")

print("Phase 4 Android script complete.")
