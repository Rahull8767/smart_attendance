package com.company.fieldattendance.ui.provider;
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
