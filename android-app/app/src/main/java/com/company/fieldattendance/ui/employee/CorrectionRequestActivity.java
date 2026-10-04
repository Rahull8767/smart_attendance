package com.company.fieldattendance.ui.employee;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;

public class CorrectionRequestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction_request);

        EditText etReason = findViewById(R.id.etReason);
        Button btnSubmit = findViewById(R.id.btnSubmitCorrection);

        btnSubmit.setOnClickListener(v -> {
            if(etReason.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Correction Requested Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
