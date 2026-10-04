import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

# Backend Files
base_dir_backend = "backend/src/main/java/com/company/attendance"

create_file(f"{base_dir_backend}/entity/Company.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String name;
    private String planType; // "BASIC", "PREMIUM", "ENTERPRISE"
    private String status;
    private Integer maxEmployees;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
""")

create_file(f"{base_dir_backend}/repository/CompanyRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
""")

create_file(f"{base_dir_backend}/controller/ProviderController.java", """
package com.company.attendance.controller;

import com.company.attendance.entity.Company;
import com.company.attendance.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        company.setStatus("ACTIVE");
        if (company.getMaxEmployees() == null) {
            company.setMaxEmployees(50); // Default for basic
        }
        return ResponseEntity.ok(companyRepository.save(company));
    }
}
""")

# Android Files
base_dir_android = "android-app/app/src/main"
java_dir = f"{base_dir_android}/java/com/company/fieldattendance"
res_dir = f"{base_dir_android}/res"

create_file(f"{res_dir}/layout/activity_provider_dashboard.xml", """
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
        android:text="Provider (Admin) Dashboard"
        android:textSize="22sp"
        android:textStyle="bold"
        android:layout_marginTop="32dp"
        android:layout_marginBottom="32dp"/>

    <Button
        android:id="@+id/btnManageCompanies"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:text="MANAGE CLIENT COMPANIES"
        android:layout_marginBottom="16dp"/>

    <Button
        android:id="@+id/btnGlobalAnalytics"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:text="GLOBAL SYSTEM ANALYTICS"
        android:layout_marginBottom="32dp"/>

    <Button
        android:id="@+id/btnLogout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LOGOUT"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
</LinearLayout>
""")

create_file(f"{java_dir}/ui/provider/ProviderDashboardActivity.java", """
package com.company.fieldattendance.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class ProviderDashboardActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        sessionManager = new SessionManager(this);

        findViewById(R.id.btnManageCompanies).setOnClickListener(v -> {
            Toast.makeText(this, "Loading Client Companies...", Toast.LENGTH_SHORT).show();
        });
        
        findViewById(R.id.btnGlobalAnalytics).setOnClickListener(v -> {
            Toast.makeText(this, "Loading Analytics...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
""")

# Patch LoginActivity to route to ProviderDashboardActivity
path_login = f"{java_dir}/ui/auth/LoginActivity.java"
with open(path_login, 'r') as f:
    content_login = f.read()

import_provider = 'import com.company.fieldattendance.ui.provider.ProviderDashboardActivity;'
if import_provider not in content_login:
    content_login = content_login.replace('import com.company.fieldattendance.ui.manager.ManagerDashboardActivity;', 
                                          'import com.company.fieldattendance.ui.manager.ManagerDashboardActivity;\\n' + import_provider)

old_provider_route = '''        } else if ("ROLE_PROVIDER".equals(role)) {
            // Placeholder for provider
            Toast.makeText(this, "Provider Login", Toast.LENGTH_SHORT).show();
            return;'''
new_provider_route = '''        } else if ("ROLE_PROVIDER".equals(role)) {
            intent = new Intent(this, ProviderDashboardActivity.class);'''

if old_provider_route in content_login:
    content_login = content_login.replace(old_provider_route, new_provider_route)

with open(path_login, 'w') as f:
    f.write(content_login)

# Patch Manifest
path_man = f"{base_dir_android}/AndroidManifest.xml"
with open(path_man, 'r') as f:
    content_man = f.read()

new_activity = '        <activity android:name=".ui.provider.ProviderDashboardActivity" />\\n'

if 'ProviderDashboardActivity' not in content_man:
    content_man = content_man.replace('</application>', new_activity + '</application>')
    with open(path_man, 'w') as f:
        f.write(content_man)

print("Phase 10 script complete.")
