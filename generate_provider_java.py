import os

base_dir = r"d:\studioProject\smart_attendance\android-app\app\src\main"
java_dir = os.path.join(base_dir, r"java\com\company\fieldattendance\ui\provider")
layout_dir = os.path.join(base_dir, r"res\layout")

activities = [
    "ProviderHomeActivity",
    "ProviderCeoListActivity",
    "ProviderCeoDetailsActivity",
    "ProviderCreateCeoActivity",
    "ProviderWorkforceActivity",
    "ProviderAnalyticsActivity",
    "ProviderAlertsActivity",
    "ProviderProfileActivity",
    "ProviderSettingsActivity"
]

layouts = {
    "ProviderHomeActivity": "activity_provider_home",
    "ProviderCeoListActivity": "activity_provider_ceo_list",
    "ProviderCeoDetailsActivity": "activity_provider_ceo_details",
    "ProviderCreateCeoActivity": "activity_provider_create_ceo",
    "ProviderWorkforceActivity": "activity_provider_workforce",
    "ProviderAnalyticsActivity": "activity_provider_analytics",
    "ProviderAlertsActivity": "activity_provider_alerts",
    "ProviderProfileActivity": "activity_provider_profile",
    "ProviderSettingsActivity": "activity_provider_settings"
}

base_activity_code = """package com.company.fieldattendance.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class {activity_name} extends AppCompatActivity {{
    protected SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.{layout_name});
        sessionManager = new SessionManager(this);
        setupBottomNavigation();
        initViews();
    }}
    
    protected void setupBottomNavigation() {{
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavProvider);
        if (bottomNav == null) return;
        
        int selectedId = {selected_nav_id};
        bottomNav.setSelectedItemId(selectedId);
        
        bottomNav.setOnItemSelectedListener(item -> {{
            int itemId = item.getItemId();
            if (itemId == selectedId) return true;
            
            Intent intent = null;
            if (itemId == R.id.nav_provider_home) {{
                intent = new Intent(this, ProviderHomeActivity.class);
            }} else if (itemId == R.id.nav_provider_ceos) {{
                intent = new Intent(this, ProviderCeoListActivity.class);
            }} else if (itemId == R.id.nav_provider_workforce) {{
                intent = new Intent(this, ProviderWorkforceActivity.class);
            }} else if (itemId == R.id.nav_provider_analytics) {{
                intent = new Intent(this, ProviderAnalyticsActivity.class);
            }} else if (itemId == R.id.nav_provider_profile) {{
                intent = new Intent(this, ProviderProfileActivity.class);
            }}
            
            if (intent != null) {{
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }}
            return true;
        }});
    }}
    
    protected void initViews() {{
        // Override in child
    }}
}}
"""

for activity in activities:
    selected_nav_id = "R.id.nav_provider_home"
    if activity == "ProviderCeoListActivity":
        selected_nav_id = "R.id.nav_provider_ceos"
    elif activity == "ProviderWorkforceActivity":
        selected_nav_id = "R.id.nav_provider_workforce"
    elif activity == "ProviderAnalyticsActivity":
        selected_nav_id = "R.id.nav_provider_analytics"
    elif activity == "ProviderProfileActivity":
        selected_nav_id = "R.id.nav_provider_profile"
        
    code = base_activity_code.format(activity_name=activity, layout_name=layouts[activity], selected_nav_id=selected_nav_id)
    
    # Custom logic for some activities
    if activity == "ProviderProfileActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        findViewById(R.id.btnLogout).setOnClickListener(v -> {\n            sessionManager.clearSession();\n            Intent intent = new Intent(this, LoginActivity.class);\n            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);\n            startActivity(intent);\n            finish();\n        });\n        findViewById(R.id.btnSettings).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderSettingsActivity.class));\n        });")
    elif activity == "ProviderHomeActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        android.widget.TextView tvGreeting = findViewById(R.id.tvGreeting);\n        String name = sessionManager.getDisplayName();\n        tvGreeting.setText(\"Good morning, \" + (name != null ? name : \"Provider\"));\n        \n        findViewById(R.id.btnCreateCeo).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderCreateCeoActivity.class));\n        });\n        findViewById(R.id.btnViewCeos).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderCeoListActivity.class));\n        });\n        findViewById(R.id.btnAlerts).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderAlertsActivity.class));\n        });\n")
    elif activity == "ProviderCeoListActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        findViewById(R.id.btnCeoCard).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderCeoDetailsActivity.class));\n        });\n")
    elif activity == "ProviderCeoDetailsActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        findViewById(R.id.btnBack).setOnClickListener(v -> finish());\n        findViewById(R.id.btnViewEmployees).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderWorkforceActivity.class));\n        });\n        findViewById(R.id.btnViewAnalytics).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderAnalyticsActivity.class));\n        });\n")
    elif activity == "ProviderCreateCeoActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        findViewById(R.id.btnBack).setOnClickListener(v -> finish());\n        findViewById(R.id.btnCreate).setOnClickListener(v -> {\n            findViewById(R.id.formLayout).setVisibility(android.view.View.GONE);\n            findViewById(R.id.successLayout).setVisibility(android.view.View.VISIBLE);\n        });\n        findViewById(R.id.btnViewCeo).setOnClickListener(v -> {\n            startActivity(new Intent(this, ProviderCeoDetailsActivity.class));\n            finish();\n        });\n        findViewById(R.id.btnDone).setOnClickListener(v -> finish());\n")
    elif activity == "ProviderSettingsActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        findViewById(R.id.btnBack).setOnClickListener(v -> finish());\n")
    elif activity == "ProviderAlertsActivity":
        code = code.replace("protected void initViews() {", "protected void initViews() {\n        findViewById(R.id.btnBack).setOnClickListener(v -> finish());\n")
    
    with open(os.path.join(java_dir, f"{activity}.java"), "w") as f:
        f.write(code)

print("Java files generated successfully")
