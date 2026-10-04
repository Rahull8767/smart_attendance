import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"
res_dir = f"{base_dir}/res"

# Layout: activity_login.xml
create_file(f"{res_dir}/layout/activity_login.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Field Attendance"
        android:textSize="28sp"
        android:textStyle="bold"
        android:layout_marginBottom="32dp"/>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp">
        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textEmailAddress"
            android:hint="Email Address" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="24dp">
        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etPassword"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textPassword"
            android:hint="Password" />
    </com.google.android.material.textfield.TextInputLayout>

    <Button
        android:id="@+id/btnLogin"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:text="LOGIN" />
        
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:visibility="gone" />

</LinearLayout>
""")

# Layout: activity_employee_dashboard.xml
create_file(f"{res_dir}/layout/activity_employee_dashboard.xml", """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center_horizontal">

    <TextView
        android:id="@+id/tvWelcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Employee Dashboard"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginTop="32dp"
        android:layout_marginBottom="32dp"/>
        
    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_marginBottom="16dp">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:gravity="center">
            
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Ready to punch in?"
                android:textSize="18sp"
                android:layout_marginBottom="16dp"/>
                
            <Button
                android:id="@+id/btnPunchIn"
                android:layout_width="160dp"
                android:layout_height="64dp"
                android:text="PUNCH IN" />
        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <Button
        android:id="@+id/btnLogout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LOGOUT"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
</LinearLayout>
""")

# Layout: activity_manager_dashboard.xml
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
        android:id="@+id/btnLogout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LOGOUT"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
</LinearLayout>
""")

# Data Models
create_file(f"{java_dir}/data/model/AuthRequest.java", """
package com.company.fieldattendance.data.model;

public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
""")

create_file(f"{java_dir}/data/model/AuthResponse.java", """
package com.company.fieldattendance.data.model;

public class AuthResponse {
    private String token;
    private String role;

    public String getToken() { return token; }
    public String getRole() { return role; }
}
""")

# ApiService and Retrofit
create_file(f"{java_dir}/data/api/ApiService.java", """
package com.company.fieldattendance.data.api;

import com.company.fieldattendance.data.model.AuthRequest;
import com.company.fieldattendance.data.model.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);
}
""")

create_file(f"{java_dir}/data/api/RetrofitClient.java", """
package com.company.fieldattendance.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 10.0.2.2 is localhost for Android Emulator
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
""")

# SessionManager
create_file(f"{java_dir}/data/local/SessionManager.java", """
package com.company.fieldattendance.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FieldAttendanceSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token, String role) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
""")

# Activities
create_file(f"{java_dir}/ui/auth/LoginActivity.java", """
package com.company.fieldattendance.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.ApiService;
import com.company.fieldattendance.data.api.RetrofitClient;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.AuthRequest;
import com.company.fieldattendance.data.model.AuthResponse;
import com.company.fieldattendance.ui.employee.EmployeeDashboardActivity;
import com.company.fieldattendance.ui.manager.ManagerDashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        AuthRequest request = new AuthRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    sessionManager.saveSession(authResponse.getToken(), authResponse.getRole());
                    navigateToRoleDashboard(authResponse.getRole());
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRoleDashboard(String role) {
        Intent intent;
        if ("ROLE_MANAGER".equals(role)) {
            intent = new Intent(this, ManagerDashboardActivity.class);
        } else if ("ROLE_PROVIDER".equals(role)) {
            // Placeholder for provider
            Toast.makeText(this, "Provider Login", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent = new Intent(this, EmployeeDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
""")

create_file(f"{java_dir}/ui/employee/EmployeeDashboardActivity.java", """
package com.company.fieldattendance.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private Button btnLogout, btnPunchIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        sessionManager = new SessionManager(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnPunchIn = findViewById(R.id.btnPunchIn);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        
        btnPunchIn.setOnClickListener(v -> {
            // Placeholder for phase 6
        });
    }
}
""")

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
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        sessionManager = new SessionManager(this);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
""")

create_file(f"{java_dir}/ui/common/SplashActivity.java", """
package com.company.fieldattendance.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.company.fieldattendance.ui.employee.EmployeeDashboardActivity;
import com.company.fieldattendance.ui.manager.ManagerDashboardActivity;

public class SplashActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        
        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                String role = sessionManager.getRole();
                if ("ROLE_MANAGER".equals(role)) {
                    startActivity(new Intent(this, ManagerDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, EmployeeDashboardActivity.class));
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}
""")

print("Phase 2 Android files created successfully.")
