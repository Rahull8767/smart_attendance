package com.company.fieldattendance.ui.provider;
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
                        ((TextView)findViewById(R.id.tvCreatedCeoName)).setText(resource.data.name + "  " + resource.data.companyName);
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
