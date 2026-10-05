package com.company.fieldattendance.ui.provider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.CeoDetailsResponse;

public class ProviderCeoDetailsActivity extends AppCompatActivity {
    private ProviderViewModel viewModel;
    private String ceoId;
    private CeoDetailsResponse currentCeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_ceo_details);
        viewModel = new ViewModelProvider(this).get(ProviderViewModel.class);
        
        ceoId = getIntent().getStringExtra("CEO_ID");
        if (ceoId == null) {
            Toast.makeText(this, "CEO ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initViews();
        loadDetails();
    }
    
    protected void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        Button btnDeactivate = findViewById(R.id.btnDeactivate);
        btnDeactivate.setOnClickListener(v -> {
            if (currentCeo != null) {
                String newStatus = "ACTIVE".equalsIgnoreCase(currentCeo.status) ? "INACTIVE" : "ACTIVE";
                viewModel.updateCeoStatus(ceoId, newStatus).observe(this, resource -> {
                    if (resource != null && resource.status == com.company.fieldattendance.utils.Resource.Status.SUCCESS) {
                        Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        loadDetails(); // Reload
                    } else if (resource != null && resource.status == com.company.fieldattendance.utils.Resource.Status.ERROR) {
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    private void loadDetails() {
        viewModel.getCeoDetails(ceoId).observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        currentCeo = resource.data;
                        updateUI(resource.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
    
    private void updateUI(CeoDetailsResponse data) {
        ((TextView)findViewById(R.id.tvName)).setText(data.name != null ? data.name : "-");
        ((TextView)findViewById(R.id.tvCompany)).setText(data.companyName != null ? data.companyName : "-");
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText(data.status);
        if ("INACTIVE".equalsIgnoreCase(data.status)) {
            tvStatus.setTextColor(android.graphics.Color.parseColor("#EF4444"));
            tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#1AEF4444"));
            ((Button)findViewById(R.id.btnDeactivate)).setText("Activate CEO");
            ((Button)findViewById(R.id.btnDeactivate)).setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#3322C55E")));
            ((Button)findViewById(R.id.btnDeactivate)).setTextColor(android.graphics.Color.parseColor("#22C55E"));
        } else {
            tvStatus.setTextColor(android.graphics.Color.parseColor("#22C55E"));
            tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#1A22C55E"));
            ((Button)findViewById(R.id.btnDeactivate)).setText("Deactivate CEO");
            ((Button)findViewById(R.id.btnDeactivate)).setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#33EF4444")));
            ((Button)findViewById(R.id.btnDeactivate)).setTextColor(android.graphics.Color.parseColor("#EF4444"));
        }
        
        ((TextView)findViewById(R.id.tvEmployeeCount)).setText(String.valueOf(data.employeeCount));
        ((TextView)findViewById(R.id.tvSiteCount)).setText(String.valueOf(data.siteCount));
        
        ((TextView)findViewById(R.id.tvIndustry)).setText("Industry: " + (data.industry != null ? data.industry : "-"));
        ((TextView)findViewById(R.id.tvEmail)).setText("Email: " + (data.companyEmail != null ? data.companyEmail : "-"));
        ((TextView)findViewById(R.id.tvPhone)).setText("Phone: " + (data.companyPhone != null ? data.companyPhone : "-"));
    }
}
