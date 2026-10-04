package com.company.fieldattendance.services;

import android.util.Log;
import androidx.annotation.NonNull;
// import com.google.firebase.messaging.FirebaseMessagingService;
// import com.google.firebase.messaging.RemoteMessage;

// Mocked without actual FCM SDK for compilation success in this Phase MVP.
// In production, extend FirebaseMessagingService.
public class MyFirebaseMessagingService {
    private static final String TAG = "FCMService";

    // @Override
    public void onMessageReceived(Object remoteMessage) {
        // Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Handle notification payload
    }

    // @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to backend
    }
}
