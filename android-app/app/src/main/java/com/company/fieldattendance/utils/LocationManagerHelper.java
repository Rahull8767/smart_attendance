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
