package com.company.fieldattendance;

import android.app.Application;
import com.company.fieldattendance.data.api.RetrofitClient;

public class FieldAttendanceApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}
