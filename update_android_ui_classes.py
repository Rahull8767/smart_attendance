import os

base_dir = r"d:\studioProject\smart_attendance\android-app\app\src\main"
java_dir = os.path.join(base_dir, r"java\com\company\fieldattendance\ui\provider")

# 1. ProviderCeoListActivity
list_java = """package com.company.fieldattendance.ui.provider;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.CeoResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ProviderCeoListActivity extends AppCompatActivity {
    protected SessionManager sessionManager;
    private ProviderViewModel viewModel;
    private CeoAdapter adapter;
    private List<CeoResponse> allCeos = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_ceo_list);
        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(ProviderViewModel.class);
        setupBottomNavigation();
        initViews();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadCeos();
    }
    
    protected void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavProvider);
        if (bottomNav == null) return;
        int selectedId = R.id.nav_provider_ceos;
        bottomNav.setSelectedItemId(selectedId);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedId) return true;
            Intent intent = null;
            if (itemId == R.id.nav_provider_home) intent = new Intent(this, ProviderHomeActivity.class);
            else if (itemId == R.id.nav_provider_ceos) intent = new Intent(this, ProviderCeoListActivity.class);
            else if (itemId == R.id.nav_provider_workforce) intent = new Intent(this, ProviderWorkforceActivity.class);
            else if (itemId == R.id.nav_provider_analytics) intent = new Intent(this, ProviderAnalyticsActivity.class);
            else if (itemId == R.id.nav_provider_profile) intent = new Intent(this, ProviderProfileActivity.class);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }
    
    protected void initViews() {
        RecyclerView rvCeos = findViewById(R.id.rvCeos);
        rvCeos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CeoAdapter();
        rvCeos.setAdapter(adapter);
        
        adapter.setOnItemClickListener(ceo -> {
            Intent intent = new Intent(this, ProviderCeoDetailsActivity.class);
            intent.putExtra("CEO_ID", ceo.id);
            startActivity(intent);
        });
        
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCeos(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
    
    private void filterCeos(String query) {
        if (query.isEmpty()) {
            adapter.setCeos(allCeos);
            return;
        }
        List<CeoResponse> filtered = new ArrayList<>();
        String q = query.toLowerCase();
        for (CeoResponse ceo : allCeos) {
            if ((ceo.name != null && ceo.name.toLowerCase().contains(q)) ||
                (ceo.companyName != null && ceo.companyName.toLowerCase().contains(q))) {
                filtered.add(ceo);
            }
        }
        adapter.setCeos(filtered);
    }
    
    private void loadCeos() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        viewModel.getCeos().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        allCeos = resource.data;
                        filterCeos(((EditText)findViewById(R.id.etSearch)).getText().toString());
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
"""
with open(os.path.join(java_dir, "ProviderCeoListActivity.java"), "w") as f: f.write(list_java)

# 2. ProviderCeoDetailsActivity
details_java = """package com.company.fieldattendance.ui.provider;
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
"""
with open(os.path.join(java_dir, "ProviderCeoDetailsActivity.java"), "w") as f: f.write(details_java)

# 3. ProviderCreateCeoActivity
create_java = """package com.company.fieldattendance.ui.provider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.CreateCeoRequest;

public class ProviderCreateCeoActivity extends AppCompatActivity {
    private ProviderViewModel viewModel;
    private String createdCeoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_create_ceo);
        viewModel = new ViewModelProvider(this).get(ProviderViewModel.class);
        initViews();
    }
    
    protected void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            createCeo();
        });
        
        findViewById(R.id.btnViewCeo).setOnClickListener(v -> {
            if (createdCeoId != null) {
                Intent intent = new Intent(this, ProviderCeoDetailsActivity.class);
                intent.putExtra("CEO_ID", createdCeoId);
                startActivity(intent);
                finish();
            }
        });
        
        findViewById(R.id.btnDone).setOnClickListener(v -> {
            finish();
        });
    }
    
    private void createCeo() {
        String name = ((EditText)findViewById(R.id.etName)).getText().toString();
        String email = ((EditText)findViewById(R.id.etEmail)).getText().toString();
        String phone = ((EditText)findViewById(R.id.etPhone)).getText().toString();
        
        String companyName = ((EditText)findViewById(R.id.etCompanyName)).getText().toString();
        String industry = ((EditText)findViewById(R.id.etIndustry)).getText().toString();
        
        String loginEmail = ((EditText)findViewById(R.id.etLoginEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.etPassword)).getText().toString();
        
        if (name.isEmpty() || companyName.isEmpty() || loginEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        CreateCeoRequest req = new CreateCeoRequest();
        req.name = name;
        req.email = email;
        req.phone = phone;
        req.companyName = companyName;
        req.industry = industry;
        req.loginEmail = loginEmail;
        req.password = password;
        req.status = "ACTIVE";
        
        findViewById(R.id.btnCreate).setEnabled(false);
        ((android.widget.Button)findViewById(R.id.btnCreate)).setText("CREATING...");
        
        viewModel.createCeo(req).observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null) {
                        createdCeoId = resource.data.id;
                        findViewById(R.id.formLayout).setVisibility(View.GONE);
                        findViewById(R.id.successLayout).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.tvCreatedCeoName)).setText(resource.data.name + " • " + resource.data.companyName);
                    }
                    break;
                case ERROR:
                    findViewById(R.id.btnCreate).setEnabled(true);
                    ((android.widget.Button)findViewById(R.id.btnCreate)).setText("CREATE CEO");
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    break;
            }
        });
    }
}
"""
with open(os.path.join(java_dir, "ProviderCreateCeoActivity.java"), "w") as f: f.write(create_java)

print("Java UI integration code written.")
