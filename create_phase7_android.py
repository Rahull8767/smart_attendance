import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"
res_dir = f"{base_dir}/res"

# FCM Service
create_file(f"{java_dir}/services/MyFirebaseMessagingService.java", """
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
""")

# Layouts
create_file(f"{res_dir}/layout/activity_attendance_history.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Attendance History"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvHistory"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""")

create_file(f"{res_dir}/layout/activity_correction_request.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Request Correction"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <EditText
        android:id="@+id/etReason"
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:hint="Explain why you are requesting an attendance correction (e.g. forgot to punch out)"
        android:gravity="top|start"
        android:inputType="textMultiLine"
        android:background="@android:drawable/edit_text"
        android:layout_marginBottom="16dp"/>

    <Button
        android:id="@+id/btnSubmitCorrection"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:text="SUBMIT REQUEST" />
</LinearLayout>
""")

# Activities
create_file(f"{java_dir}/ui/employee/AttendanceHistoryActivity.java", """
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
""")

create_file(f"{java_dir}/ui/employee/CorrectionRequestActivity.java", """
package com.company.fieldattendance.ui.employee;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;

public class CorrectionRequestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction_request);

        EditText etReason = findViewById(R.id.etReason);
        Button btnSubmit = findViewById(R.id.btnSubmitCorrection);

        btnSubmit.setOnClickListener(v -> {
            if(etReason.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Correction Requested Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
""")

# Patch EmployeeDashboard layout
create_file(f"patch_dashboard_layout_phase7.py", """
path = 'android-app/app/src/main/res/layout/activity_employee_dashboard.xml'
with open(path, 'r') as f:
    content = f.read()

new_buttons = '''
    <Button
        android:id="@+id/btnHistory"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="View History"
        android:layout_marginBottom="8dp"/>
        
    <Button
        android:id="@+id/btnCorrection"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Request Correction"
        android:layout_marginBottom="16dp"/>
'''

if 'btnHistory' not in content:
    content = content.replace('<Button\\n        android:id="@+id/btnLogout"', new_buttons + '\\n    <Button\\n        android:id="@+id/btnLogout"')
    with open(path, 'w') as f:
        f.write(content)
""")

# Patch EmployeeDashboardActivity logic
create_file(f"patch_dashboard_activity_phase7.py", """
path = 'android-app/app/src/main/java/com/company/fieldattendance/ui/employee/EmployeeDashboardActivity.java'
with open(path, 'r') as f:
    content = f.read()

new_logic = '''
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, AttendanceHistoryActivity.class));
        });
        
        findViewById(R.id.btnCorrection).setOnClickListener(v -> {
            startActivity(new Intent(this, CorrectionRequestActivity.class));
        });
'''

if 'btnHistory' not in content:
    content = content.replace('btnPunchIn.setOnClickListener', new_logic + '\\n        btnPunchIn.setOnClickListener')
    with open(path, 'w') as f:
        f.write(content)
""")

create_file(f"update_manifest_phase7.py", """
path = 'android-app/app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

new_activities = '''
        <activity android:name=".ui.employee.AttendanceHistoryActivity" />
        <activity android:name=".ui.employee.CorrectionRequestActivity" />
'''

if 'AttendanceHistoryActivity' not in content:
    content = content.replace('</application>', new_activities + '</application>')
    with open(path, 'w') as f:
        f.write(content)
""")

print("Phase 7 Android script complete.")
