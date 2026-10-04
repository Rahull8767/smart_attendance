package com.company.fieldattendance.ui.ceo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.company.fieldattendance.R;

public class ReportsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Button btnExportCsv = findViewById(R.id.btnExportCsv);

        btnExportCsv.setOnClickListener(v -> {
            Toast.makeText(this, "Downloading CSV Report...", Toast.LENGTH_SHORT).show();
            // In a real implementation, we use DownloadManager or Retrofit to stream the file bytes
        });
    }
}
