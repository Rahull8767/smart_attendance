package com.company.fieldattendance.ui.ceo;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.api.CeoApiService;
import com.company.fieldattendance.data.api.RetrofitClient;

public class SiteManagementActivity extends AppCompatActivity {
    private CeoApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_management);
        
        apiService = RetrofitClient.getRetrofitInstance().create(CeoApiService.class);
        
        findViewById(R.id.btnAddSite).setOnClickListener(v -> {
            Toast.makeText(this, "Add Site Dialog would open here", Toast.LENGTH_SHORT).show();
        });
        
        // Setup RecyclerView...
    }
}
