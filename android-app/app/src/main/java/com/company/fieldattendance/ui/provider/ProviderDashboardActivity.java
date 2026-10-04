package com.company.fieldattendance.ui.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.ui.auth.LoginActivity;

public class ProviderDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private View btnLogout;
    private TextView tvDemoBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        sessionManager = new SessionManager(this);
        btnLogout = findViewById(R.id.btnLogout);
        tvDemoBadge = findViewById(R.id.tvDemoBadge);

        tvDemoBadge.setVisibility(View.GONE);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
