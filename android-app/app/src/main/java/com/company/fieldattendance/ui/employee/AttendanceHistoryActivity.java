package com.company.fieldattendance.ui.employee;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;

public class AttendanceHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);
        Toast.makeText(this, "Loading history...", Toast.LENGTH_SHORT).show();
        // Recycler view binding would go here hitting API GET /api/attendance/history/{id}
    }
}
