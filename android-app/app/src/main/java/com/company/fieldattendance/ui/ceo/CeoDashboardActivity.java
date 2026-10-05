package com.company.fieldattendance.ui.ceo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class CeoDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceo_dashboard);

        sessionManager = new SessionManager(this);

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavCeo);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_more) {
                // Temporary logic: Log out on 'More' since we don't have the Settings screen yet
                sessionManager.clearSession();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }
}
