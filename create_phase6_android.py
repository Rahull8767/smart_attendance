import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"

create_file(f"{java_dir}/data/model/PunchRequest.java", """
package com.company.fieldattendance.data.model;

import java.util.UUID;

public class PunchRequest {
    public UUID employeeId;
    public Double latitude;
    public Double longitude;
    public Float accuracy;
    public String faceStatus;
    
    public PunchRequest(UUID employeeId, Double latitude, Double longitude, Float accuracy, String faceStatus) {
        this.employeeId = employeeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.faceStatus = faceStatus;
    }
}
""")

create_file(f"{java_dir}/data/model/ApiResponse.java", """
package com.company.fieldattendance.data.model;

public class ApiResponse {
    public boolean success;
    public String message;
}
""")

# Rewrite FaceVerificationActivity to call the Punch API
create_file(f"{java_dir}/ui/employee/FaceVerificationActivity.java", """
package com.company.fieldattendance.ui.employee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.ApiResponse;
import com.company.fieldattendance.data.model.PunchRequest;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceVerificationActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 1002;
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ApiService apiService;
    
    private double lat, lon;
    private float accuracy;
    // Mock UUID for testing since we don't have full login profile fetch yet
    private UUID mockEmployeeId = UUID.randomUUID(); 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verification);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        lat = getIntent().getDoubleExtra("LAT", 0.0);
        lon = getIntent().getDoubleExtra("LON", 0.0);
        accuracy = getIntent().getFloatExtra("ACC", 0.0f);

        viewFinder = findViewById(R.id.viewFinder);
        Button btnCapture = findViewById(R.id.btnCapture);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        btnCapture.setOnClickListener(v -> takePhotoAndVerify());
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                Toast.makeText(this, "Camera setup failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhotoAndVerify() {
        Toast.makeText(this, "Verifying face...", Toast.LENGTH_SHORT).show();
        
        // Mocking face success, hitting Punch API
        PunchRequest request = new PunchRequest(mockEmployeeId, lat, lon, accuracy, "VERIFIED");
        
        apiService.punchIn(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().success) {
                        Toast.makeText(FaceVerificationActivity.this, "Punch In Successful!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FaceVerificationActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FaceVerificationActivity.this, "Server rejected punch-in", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(FaceVerificationActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
""")

# Re-link EmployeeDashboardActivity to pass Intent extras
create_file(f"patch_dashboard_phase6.py", """
import re
path = 'android-app/app/src/main/java/com/company/fieldattendance/ui/employee/EmployeeDashboardActivity.java'
with open(path, 'r') as f:
    content = f.read()

old_code = '''                // Launch Face Verification if location succeeds
                Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                startActivity(intent);'''
                
new_code = '''                Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                intent.putExtra("LAT", location.getLatitude());
                intent.putExtra("LON", location.getLongitude());
                intent.putExtra("ACC", location.getAccuracy());
                startActivity(intent);'''

if old_code in content:
    content = content.replace(old_code, new_code)
    with open(path, 'w') as f:
        f.write(content)
""")

# Add punch-in to ApiService
create_file(f"patch_apiservice.py", """
path = 'android-app/app/src/main/java/com/company/fieldattendance/data/api/ApiService.java'
with open(path, 'r') as f:
    content = f.read()

new_imports = '''import com.company.fieldattendance.data.model.PunchRequest;
import com.company.fieldattendance.data.model.ApiResponse;'''

new_methods = '''    @POST("/api/attendance/punch-in")
    Call<ApiResponse> punchIn(@Body PunchRequest request);

    @POST("/api/attendance/punch-out")
    Call<ApiResponse> punchOut(@Body PunchRequest request);
}'''

if 'PunchRequest' not in content:
    content = content.replace('import retrofit2.http.POST;', 'import retrofit2.http.POST;\\n' + new_imports)
    content = content.replace('}', new_methods)
    with open(path, 'w') as f:
        f.write(content)
""")

print("Phase 6 Android script complete.")
