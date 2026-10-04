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

import com.company.fieldattendance.data.local.SessionManager;
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
    private SessionManager sessionManager;
    private UUID employeeId; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verification);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        sessionManager = new SessionManager(this);
        
        String empIdStr = sessionManager.getEmployeeId();
        if (empIdStr != null) {
            employeeId = UUID.fromString(empIdStr);
        }

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
        Toast.makeText(this, "Capturing and analyzing face...", Toast.LENGTH_SHORT).show();
        
        if (imageCapture == null) {
            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull androidx.camera.core.ImageProxy imageProxy) {
                // Convert ImageProxy to Bitmap
                android.graphics.Bitmap bitmap = convertImageProxyToBitmap(imageProxy);
                imageProxy.close();
                
                // Initialize FaceRecognitionManager and extract embedding
                com.company.fieldattendance.utils.FaceRecognitionManager faceRecognitionManager = new com.company.fieldattendance.utils.FaceRecognitionManager(FaceVerificationActivity.this);
                faceRecognitionManager.extractEmbedding(bitmap, new com.company.fieldattendance.utils.FaceRecognitionManager.FaceRecognitionCallback() {
                    @Override
                    public void onSuccess(float[] embeddingArray) {
                        faceRecognitionManager.close();
                        
                        // Convert float[] to List<Float>
                        java.util.List<Float> embeddingList = new java.util.ArrayList<>();
                        for (float f : embeddingArray) {
                            embeddingList.add(f);
                        }

                        // Send Punch API Request
                        PunchRequest request = new PunchRequest(employeeId, lat, lon, accuracy, "VERIFIED");
                        request.setFaceEmbedding(embeddingList);

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
                                    Toast.makeText(FaceVerificationActivity.this, "Verification Failed on Server", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(String error) {
                        faceRecognitionManager.close();
                        Toast.makeText(FaceVerificationActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
            
            @Override
            public void onError(@NonNull androidx.camera.core.ImageCaptureException exception) {
                Toast.makeText(FaceVerificationActivity.this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private android.graphics.Bitmap convertImageProxyToBitmap(androidx.camera.core.ImageProxy image) {
        java.nio.ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
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
