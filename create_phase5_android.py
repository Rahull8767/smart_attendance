import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"
res_dir = f"{base_dir}/res"

create_file(f"{res_dir}/layout/activity_face_verification.xml", """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.camera.view.PreviewView
        android:id="@+id/viewFinder"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <TextView
        android:id="@+id/tvInstruction"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Align your face and hold still"
        android:textColor="@android:color/white"
        android:textSize="18sp"
        android:background="#88000000"
        android:padding="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="32dp"/>

    <Button
        android:id="@+id/btnCapture"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Verify Face"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginBottom="32dp"/>

</androidx.constraintlayout.widget.ConstraintLayout>
""")

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

public class FaceVerificationActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 1002;
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verification);

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
        // Mocking the capture and verification step.
        // In full Phase 6, we will hit the backend POST /api/attendance/punch-in here
        Toast.makeText(this, "Face verified successfully. Punch In complete!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required for Face Verification", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
""")

create_file(f"update_manifest_phase5.py", """
path = 'android-app/app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

new_activity = '        <activity android:name=".ui.employee.FaceVerificationActivity" />\\n'
if 'FaceVerificationActivity' not in content:
    content = content.replace('</application>', new_activity + '</application>')
    with open(path, 'w') as f:
        f.write(content)
""")

print("Phase 5 Android script complete.")
