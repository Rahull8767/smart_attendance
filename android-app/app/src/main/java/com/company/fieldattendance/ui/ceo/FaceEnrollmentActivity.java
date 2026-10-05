package com.company.fieldattendance.ui.ceo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.CeoApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.model.ApiResponse;
import com.company.fieldattendance.utils.FaceRecognitionManager;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceEnrollmentActivity extends AppCompatActivity {

    public static final String EXTRA_EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String EXTRA_ENROLLED_EMBEDDING = "ENROLLED_EMBEDDING";
    private static final int CAMERA_PERMISSION_CODE = 2001;

    private PreviewView enrollViewFinder;
    private TextView tvEnrollStatus;
    private ProgressBar pbEnrollLoading;
    private Button btnCaptureFace;
    private ImageCapture imageCapture;
    private String employeeIdStr;
    private FaceRecognitionManager faceRecognitionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_enrollment);

        employeeIdStr = getIntent().getStringExtra(EXTRA_EMPLOYEE_ID);
        enrollViewFinder = findViewById(R.id.enrollViewFinder);
        tvEnrollStatus = findViewById(R.id.tvEnrollStatus);
        pbEnrollLoading = findViewById(R.id.pbEnrollLoading);
        btnCaptureFace = findViewById(R.id.btnCaptureFace);

        faceRecognitionManager = new FaceRecognitionManager(this);

        findViewById(R.id.btnCancelEnroll).setOnClickListener(v -> finish());
        btnCaptureFace.setOnClickListener(v -> captureAndProcessFace());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void startCamera() {
        tvEnrollStatus.setText("Preparing camera...");
        pbEnrollLoading.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(enrollViewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                tvEnrollStatus.setText("Face detected • Align face");
                pbEnrollLoading.setVisibility(View.GONE);
            } catch (Exception e) {
                tvEnrollStatus.setText("Camera initialization failed");
                pbEnrollLoading.setVisibility(View.GONE);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureAndProcessFace() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        tvEnrollStatus.setText("Capturing face...");
        pbEnrollLoading.setVisibility(View.VISIBLE);
        btnCaptureFace.setEnabled(false);

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                Bitmap bitmap = convertImageProxyToBitmap(imageProxy);
                imageProxy.close();

                tvEnrollStatus.setText("Processing face...");
                faceRecognitionManager.extractEmbedding(bitmap, new FaceRecognitionManager.FaceRecognitionCallback() {
                    @Override
                    public void onSuccess(float[] embedding) {
                        ArrayList<Float> embeddingList = new ArrayList<>();
                        for (float f : embedding) {
                            embeddingList.add(f);
                        }

                        if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                            // Register with backend immediately
                            enrollWithBackend(employeeIdStr, embeddingList);
                        } else {
                            // Return embedding to calling activity (e.g. Create Employee)
                            tvEnrollStatus.setText("Face registered successfully ✓");
                            pbEnrollLoading.setVisibility(View.GONE);
                            Toast.makeText(FaceEnrollmentActivity.this, "Face registered successfully", Toast.LENGTH_SHORT).show();

                            Intent resultIntent = new Intent();
                            float[] primArray = new float[embeddingList.size()];
                            for (int i = 0; i < embeddingList.size(); i++) primArray[i] = embeddingList.get(i);
                            resultIntent.putExtra(EXTRA_ENROLLED_EMBEDDING, primArray);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        btnCaptureFace.setEnabled(true);
                        pbEnrollLoading.setVisibility(View.GONE);
                        tvEnrollStatus.setText(error != null ? error : "Face detection failed");
                        Toast.makeText(FaceEnrollmentActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                btnCaptureFace.setEnabled(true);
                pbEnrollLoading.setVisibility(View.GONE);
                tvEnrollStatus.setText("Capture failed");
                Toast.makeText(FaceEnrollmentActivity.this, "Failed to capture photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enrollWithBackend(String empId, List<Float> embeddingList) {
        tvEnrollStatus.setText("Registering with platform...");
        CeoApiService apiService = RetrofitClient.getRetrofitInstance().create(CeoApiService.class);

        Map<String, Object> payload = new HashMap<>();
        payload.put("employeeId", empId);
        payload.put("embedding", embeddingList);

        apiService.enrollFace(payload).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                pbEnrollLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    tvEnrollStatus.setText("Face registered successfully ✓");
                    Toast.makeText(FaceEnrollmentActivity.this, "Face registered successfully!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    btnCaptureFace.setEnabled(true);
                    tvEnrollStatus.setText("Enrollment failed on server");
                    Toast.makeText(FaceEnrollmentActivity.this, "Enrollment failed on server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnCaptureFace.setEnabled(true);
                pbEnrollLoading.setVisibility(View.GONE);
                tvEnrollStatus.setText("Network error");
                Toast.makeText(FaceEnrollmentActivity.this, "Network error during enrollment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required for face registration", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceRecognitionManager != null) {
            faceRecognitionManager.close();
        }
    }
}
