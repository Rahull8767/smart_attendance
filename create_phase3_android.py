import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"
res_dir = f"{base_dir}/res"

# Layout: activity_employee_management.xml
create_file(f"{res_dir}/layout/activity_employee_management.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Employee Management"
        android:textSize="22sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btnAddEmployee"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="ADD NEW EMPLOYEE"
        android:layout_marginBottom="16dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvEmployees"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""")

# Layout: activity_site_management.xml
create_file(f"{res_dir}/layout/activity_site_management.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Site Management"
        android:textSize="22sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btnAddSite"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="CREATE NEW SITE"
        android:layout_marginBottom="16dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvSites"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""")

# Models
create_file(f"{java_dir}/data/model/Employee.java", """
package com.company.fieldattendance.data.model;
import java.util.UUID;
public class Employee {
    public UUID id;
    public String name;
    public String employeeCode;
    public String department;
    public UUID assignedSiteId;
}
""")

create_file(f"{java_dir}/data/model/WorkSite.java", """
package com.company.fieldattendance.data.model;
import java.util.UUID;
public class WorkSite {
    public UUID id;
    public String name;
    public String address;
    public Double latitude;
    public Double longitude;
    public Integer geofenceRadius;
}
""")

# API extensions
create_file(f"{java_dir}/data/api/ManagerApiService.java", """
package com.company.fieldattendance.data.api;

import com.company.fieldattendance.data.model.Employee;
import com.company.fieldattendance.data.model.WorkSite;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ManagerApiService {
    @GET("/api/employees")
    Call<List<Employee>> getEmployees();

    @POST("/api/employees")
    Call<Employee> createEmployee(@Body Employee employee);

    @GET("/api/sites")
    Call<List<WorkSite>> getSites();

    @POST("/api/sites")
    Call<WorkSite> createSite(@Body WorkSite site);
}
""")

# Activities
create_file(f"{java_dir}/ui/manager/EmployeeManagementActivity.java", """
package com.company.fieldattendance.ui.manager;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ManagerApiService;
import com.company.fieldattendance.data.api.RetrofitClient;

public class EmployeeManagementActivity extends AppCompatActivity {
    private ManagerApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);
        
        apiService = RetrofitClient.getRetrofitInstance().create(ManagerApiService.class);
        
        findViewById(R.id.btnAddEmployee).setOnClickListener(v -> {
            Toast.makeText(this, "Add Employee Dialog would open here", Toast.LENGTH_SHORT).show();
        });
        
        // Setup RecyclerView...
    }
}
""")

create_file(f"{java_dir}/ui/manager/SiteManagementActivity.java", """
package com.company.fieldattendance.ui.manager;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ManagerApiService;
import com.company.fieldattendance.data.api.RetrofitClient;

public class SiteManagementActivity extends AppCompatActivity {
    private ManagerApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_management);
        
        apiService = RetrofitClient.getRetrofitInstance().create(ManagerApiService.class);
        
        findViewById(R.id.btnAddSite).setOnClickListener(v -> {
            Toast.makeText(this, "Add Site Dialog would open here", Toast.LENGTH_SHORT).show();
        });
        
        // Setup RecyclerView...
    }
}
""")

# Update ManagerDashboardActivity to include buttons
create_file(f"{java_dir}/ui/manager/ManagerDashboardActivity.java", """
package com.company.fieldattendance.ui.manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class ManagerDashboardActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private Button btnLogout, btnManageEmployees, btnManageSites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We will inflate a modified layout or just dynamically add buttons here for brevity,
        // but let's assume we update the XML next.
        setContentView(R.layout.activity_manager_dashboard);

        sessionManager = new SessionManager(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnManageEmployees = findViewById(R.id.btnManageEmployees);
        btnManageSites = findViewById(R.id.btnManageSites);

        btnManageEmployees.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployeeManagementActivity.class));
        });

        btnManageSites.setOnClickListener(v -> {
            startActivity(new Intent(this, SiteManagementActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
""")

create_file(f"{res_dir}/layout/activity_manager_dashboard.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center_horizontal">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Manager Dashboard"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginTop="32dp"
        android:layout_marginBottom="32dp"/>
        
    <Button
        android:id="@+id/btnManageEmployees"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:layout_marginBottom="16dp"
        android:text="Manage Employees" />

    <Button
        android:id="@+id/btnManageSites"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:layout_marginBottom="32dp"
        android:text="Manage Work Sites" />

    <Button
        android:id="@+id/btnLogout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LOGOUT"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
</LinearLayout>
""")

# Register new activities in Manifest
create_file(f"update_manifest.py", """
import re

path = 'android-app/app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

new_activities = '''
        <activity android:name=".ui.manager.EmployeeManagementActivity" />
        <activity android:name=".ui.manager.SiteManagementActivity" />
'''

if 'EmployeeManagementActivity' not in content:
    content = content.replace('</application>', new_activities + '</application>')
    with open(path, 'w') as f:
        f.write(content)
""")

print("Phase 3 Android files created successfully.")
