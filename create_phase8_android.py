import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"
res_dir = f"{base_dir}/res"

create_file(f"{res_dir}/layout/activity_reports.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Attendance Reports"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <Button
        android:id="@+id/btnExportCsv"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:text="EXPORT CSV REPORT"
        android:layout_marginBottom="16dp" />

    <TextView
        android:id="@+id/tvAnalytics"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Analytics will load here..."
        android:textSize="16sp" />
</LinearLayout>
""")

create_file(f"{java_dir}/ui/manager/ReportsActivity.java", """
package com.company.fieldattendance.ui.manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;

public class ReportsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Button btnExportCsv = findViewById(R.id.btnExportCsv);

        btnExportCsv.setOnClickListener(v -> {
            Toast.makeText(this, "Downloading CSV Report...", Toast.LENGTH_SHORT).show();
            // In a real implementation, we use DownloadManager or Retrofit to stream the file bytes
        });
    }
}
""")

# Patch ManagerDashboard layout
create_file(f"patch_manager_dashboard_layout.py", """
path = 'android-app/app/src/main/res/layout/activity_manager_dashboard.xml'
with open(path, 'r') as f:
    content = f.read()

new_button = '''
    <Button
        android:id="@+id/btnReports"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:layout_marginBottom="32dp"
        android:text="View Reports &amp; Analytics" />
'''

if 'btnReports' not in content:
    content = content.replace('<Button\\n        android:id="@+id/btnLogout"', new_button + '\\n    <Button\\n        android:id="@+id/btnLogout"')
    with open(path, 'w') as f:
        f.write(content)
""")

# Patch ManagerDashboardActivity logic
create_file(f"patch_manager_dashboard_activity.py", """
path = 'android-app/app/src/main/java/com/company/fieldattendance/ui/manager/ManagerDashboardActivity.java'
with open(path, 'r') as f:
    content = f.read()

new_logic = '''
        Button btnReports = findViewById(R.id.btnReports);
        btnReports.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportsActivity.class));
        });
'''

if 'ReportsActivity' not in content:
    content = content.replace('btnLogout.setOnClickListener', new_logic + '\\n        btnLogout.setOnClickListener')
    with open(path, 'w') as f:
        f.write(content)
""")

# Patch Manifest
create_file(f"patch_manifest_phase8.py", """
path = 'android-app/app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

new_activity = '        <activity android:name=".ui.manager.ReportsActivity" />\\n'

if 'ReportsActivity' not in content:
    content = content.replace('</application>', new_activity + '</application>')
    with open(path, 'w') as f:
        f.write(content)
""")

print("Phase 8 Android script complete.")
