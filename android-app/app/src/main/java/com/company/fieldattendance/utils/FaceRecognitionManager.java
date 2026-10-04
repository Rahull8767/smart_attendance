package com.company.fieldattendance.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.List;

public class FaceRecognitionManager {

    private static final String TAG = "FaceRecognition";
    private static final String MODEL_FILE = "MobileFaceNet.tflite";
    private static final int INPUT_IMAGE_SIZE = 112; 
    private static final int OUTPUT_SIZE = 192; // Default for MobileFaceNet

    private Interpreter tflite;
    private FaceDetector faceDetector;

    public interface FaceRecognitionCallback {
        void onSuccess(float[] embedding);
        void onFailure(String error);
    }

    public FaceRecognitionManager(Context context) {
        // Initialize ML Kit Face Detector
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();
        faceDetector = FaceDetection.getClient(options);

        // Initialize TensorFlow Lite
        try {
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, MODEL_FILE);
            Interpreter.Options tfliteOptions = new Interpreter.Options();
            tfliteOptions.setNumThreads(4);
            tflite = new Interpreter(tfliteModel, tfliteOptions);
            Log.d(TAG, "TFLite model loaded successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error loading TFLite model", e);
        }
    }

    public void extractEmbedding(Bitmap bitmap, FaceRecognitionCallback callback) {
        if (tflite == null) {
            callback.onFailure("TFLite model not initialized");
            return;
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.isEmpty()) {
                        callback.onFailure("No face detected");
                        return;
                    }
                    if (faces.size() > 1) {
                        callback.onFailure("Multiple faces detected");
                        return;
                    }
                    // Process the single face
                    Face face = faces.get(0);
                    Rect bounds = face.getBoundingBox();

                    // Ensure bounds are within bitmap
                    bounds.left = Math.max(bounds.left, 0);
                    bounds.top = Math.max(bounds.top, 0);
                    bounds.right = Math.min(bounds.right, bitmap.getWidth());
                    bounds.bottom = Math.min(bounds.bottom, bitmap.getHeight());

                    if (bounds.width() <= 0 || bounds.height() <= 0) {
                        callback.onFailure("Invalid face bounds");
                        return;
                    }

                    Bitmap croppedFace = Bitmap.createBitmap(
                            bitmap,
                            bounds.left,
                            bounds.top,
                            bounds.width(),
                            bounds.height()
                    );
                    
                    float[] embedding = getEmbedding(croppedFace);
                    callback.onSuccess(embedding);
                })
                .addOnFailureListener(e -> callback.onFailure("Face detection failed: " + e.getMessage()));
    }

    private float[] getEmbedding(Bitmap croppedFace) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedFace, INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, false);
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(scaledBitmap);
        
        float[][] output = new float[1][OUTPUT_SIZE];
        tflite.run(inputBuffer, output);
        return output[0];
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_IMAGE_SIZE * INPUT_IMAGE_SIZE * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        
        int[] intValues = new int[INPUT_IMAGE_SIZE * INPUT_IMAGE_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < INPUT_IMAGE_SIZE; ++i) {
            for (int j = 0; j < INPUT_IMAGE_SIZE; ++j) {
                int val = intValues[pixel++];
                // Normalize to [-1, 1] as required by MobileFaceNet
                byteBuffer.putFloat((((val >> 16) & 0xFF) - 127.5f) / 127.5f);
                byteBuffer.putFloat((((val >> 8) & 0xFF) - 127.5f) / 127.5f);
                byteBuffer.putFloat(((val & 0xFF) - 127.5f) / 127.5f);
            }
        }
        return byteBuffer;
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        if (faceDetector != null) {
            faceDetector.close();
        }
    }
}
