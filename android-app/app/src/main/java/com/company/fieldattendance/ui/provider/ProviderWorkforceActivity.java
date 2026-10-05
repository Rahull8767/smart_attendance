package com.company.fieldattendance.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProviderWorkforceActivity extends AppCompatActivity {
    protected SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_workforce);
        sessionManager = new SessionManager(this);
        setupBottomNavigation();
        initViews();
    }
    
    protected void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavProvider);
        if (bottomNav == null) return;
        
        int selectedId = R.id.nav_provider_workforce;
        bottomNav.setSelectedItemId(selectedId);
        
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedId) return true;
            
            Intent intent = null;
            if (itemId == R.id.nav_provider_home) {
                intent = new Intent(this, ProviderHomeActivity.class);
            } else if (itemId == R.id.nav_provider_ceos) {
                intent = new Intent(this, ProviderCeoListActivity.class);
            } else if (itemId == R.id.nav_provider_workforce) {
                intent = new Intent(this, ProviderWorkforceActivity.class);
            } else if (itemId == R.id.nav_provider_analytics) {
                intent = new Intent(this, ProviderAnalyticsActivity.class);
            } else if (itemId == R.id.nav_provider_profile) {
                intent = new Intent(this, ProviderProfileActivity.class);
            }
            
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }
    
    protected void initViews() {
        // Override in child
    }
}
