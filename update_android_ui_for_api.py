import os

base_dir = r"d:\studioProject\smart_attendance\android-app\app\src\main"
java_dir = os.path.join(base_dir, r"java\com\company\fieldattendance\ui\provider")
layout_dir = os.path.join(base_dir, r"res\layout")

# 1. Update activity_provider_ceo_list.xml
list_layout = """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/provider_background">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:orientation="vertical"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavProvider">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp"
            android:background="@color/provider_surface_secondary">
            
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="CEOs"
                android:textColor="@color/provider_text_primary"
                android:textSize="28sp"
                android:textStyle="bold" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Manage organizations using your platform"
                android:textColor="@color/provider_text_secondary"
                android:textSize="14sp"
                android:layout_marginTop="4dp"
                android:layout_marginBottom="16dp"/>

            <EditText
                android:id="@+id/etSearch"
                android:layout_width="match_parent"
                android:layout_height="48dp"
                android:background="@color/provider_surface"
                android:hint="Search CEO or company..."
                android:textColorHint="@color/provider_text_muted"
                android:textColor="@color/provider_text_primary"
                android:paddingStart="16dp"
                android:paddingEnd="16dp"
                android:drawableStart="@android:drawable/ic_menu_search"
                android:drawablePadding="8dp"
                android:layout_marginBottom="12dp"/>
                
        </LinearLayout>

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginTop="32dp"
            android:visibility="gone"
            android:indeterminateTint="@color/provider_primary"/>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rvCeos"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clipToPadding="false"
            android:padding="16dp"/>
            
    </LinearLayout>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavProvider"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@color/provider_surface"
        app:itemIconTint="@color/nav_active"
        app:itemTextColor="@color/nav_active"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_provider" />

</androidx.constraintlayout.widget.ConstraintLayout>
"""
with open(os.path.join(layout_dir, "activity_provider_ceo_list.xml"), "w") as f: f.write(list_layout)

# 2. Update activity_provider_create_ceo.xml
create_layout = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/provider_background">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical"
        android:background="@color/provider_surface_secondary">
        <ImageView
            android:id="@+id/btnBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@android:drawable/ic_menu_revert"
            android:padding="8dp"
            android:clickable="true"
            android:focusable="true"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Create CEO"
            android:textColor="@color/provider_text_primary"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginStart="16dp"/>
    </LinearLayout>
    
    <LinearLayout
        android:id="@+id/successLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="24dp"
        android:visibility="gone">
        <TextView android:text="CEO CREATED" android:textColor="@color/provider_success" android:textSize="24sp" android:textStyle="bold"/>
        <TextView android:id="@+id/tvCreatedCeoName" android:text="Account is ready" android:textColor="@color/provider_text_secondary" android:layout_marginTop="8dp"/>
        <Button android:id="@+id/btnViewCeo" android:layout_width="match_parent" android:layout_height="wrap_content" android:text="VIEW CEO" android:layout_marginTop="24dp" android:backgroundTint="@color/provider_primary" android:textColor="@color/provider_background"/>
        <Button android:id="@+id/btnDone" android:layout_width="match_parent" android:layout_height="wrap_content" android:text="DONE" android:layout_marginTop="8dp" android:backgroundTint="@color/provider_surface" android:textColor="@color/provider_text_primary"/>
    </LinearLayout>

    <ScrollView
        android:id="@+id/formLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">
            
            <TextView android:text="PERSONAL INFORMATION" android:textColor="@color/provider_primary" android:textStyle="bold" android:layout_marginBottom="8dp"/>
            <EditText android:id="@+id/etName" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Full Name" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:id="@+id/etEmail" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Contact Email" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:id="@+id/etPhone" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Phone" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="32dp"/>
            
            <TextView android:text="ORGANIZATION" android:textColor="@color/provider_primary" android:textStyle="bold" android:layout_marginBottom="8dp"/>
            <EditText android:id="@+id/etCompanyName" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Company Name" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:id="@+id/etIndustry" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Industry" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="32dp"/>
            
            <TextView android:text="ACCOUNT" android:textColor="@color/provider_primary" android:textStyle="bold" android:layout_marginBottom="8dp"/>
            <EditText android:id="@+id/etLoginEmail" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Login Email" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            <EditText android:id="@+id/etPassword" android:layout_width="match_parent" android:layout_height="56dp" android:hint="Password" android:inputType="textPassword" android:background="@color/provider_surface" android:textColor="@color/provider_text_primary" android:textColorHint="@color/provider_text_muted" android:padding="16dp" android:layout_marginBottom="16dp"/>
            
            <Button
                android:id="@+id/btnCreate"
                android:layout_width="match_parent"
                android:layout_height="56dp"
                android:text="CREATE CEO"
                android:backgroundTint="@color/provider_primary"
                android:textColor="@color/provider_background"
                android:layout_marginTop="24dp"
                android:layout_marginBottom="40dp"/>
        </LinearLayout>
    </ScrollView>
</LinearLayout>
"""
with open(os.path.join(layout_dir, "activity_provider_create_ceo.xml"), "w") as f: f.write(create_layout)

# 3. Create item_ceo.xml
item_ceo = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@color/provider_surface"
    android:padding="16dp"
    android:layout_marginBottom="16dp"
    android:clickable="true"
    android:focusable="true">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <ImageView
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:background="@color/provider_surface_elevated"
            android:src="@android:drawable/ic_menu_camera"/>
            
        <LinearLayout
            android:layout_width="0dp"
            android:layout_weight="1"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:layout_marginStart="12dp">
            <TextView
                android:id="@+id/tvName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Rahul Sharma"
                android:textColor="@color/provider_text_primary"
                android:textSize="18sp"
                android:textStyle="bold"/>
            <TextView
                android:id="@+id/tvCompany"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="ABC Developers"
                android:textColor="@color/provider_text_secondary"
                android:textSize="14sp"/>
        </LinearLayout>
        
        <TextView
            android:id="@+id/tvStatus"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="ACTIVE"
            android:textColor="@color/provider_success"
            android:textSize="12sp"
            android:textStyle="bold"
            android:background="#1A22C55E"
            android:paddingHorizontal="8dp"
            android:paddingVertical="4dp"/>
    </LinearLayout>
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="16dp">
        <TextView
            android:id="@+id/tvEmployees"
            android:layout_width="0dp"
            android:layout_weight="1"
            android:layout_height="wrap_content"
            android:text="0 employees"
            android:textColor="@color/provider_text_primary"/>
        <TextView
            android:id="@+id/tvSites"
            android:layout_width="0dp"
            android:layout_weight="1"
            android:layout_height="wrap_content"
            android:text="0 sites"
            android:textColor="@color/provider_text_primary"/>
    </LinearLayout>
</LinearLayout>
"""
with open(os.path.join(layout_dir, "item_ceo.xml"), "w") as f: f.write(item_ceo)

# 4. Update activity_provider_ceo_details.xml
details_layout = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/provider_background">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical"
        android:background="@color/provider_surface_secondary">
        <ImageView
            android:id="@+id/btnBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@android:drawable/ic_menu_revert"
            android:padding="8dp"
            android:clickable="true"
            android:focusable="true"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="CEO Details"
            android:textColor="@color/provider_text_primary"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginStart="16dp"/>
    </LinearLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">
            
            <ImageView
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_gravity="center"
                android:src="@android:drawable/ic_menu_camera"
                android:background="@color/provider_surface"/>
                
            <TextView
                android:id="@+id/tvName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Loading..."
                android:textColor="@color/provider_text_primary"
                android:textSize="24sp"
                android:textStyle="bold"
                android:layout_gravity="center"
                android:layout_marginTop="12dp"/>
                
            <TextView
                android:id="@+id/tvCompany"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text=""
                android:textColor="@color/provider_text_secondary"
                android:textSize="16sp"
                android:layout_gravity="center"
                android:layout_marginTop="4dp"/>
                
            <TextView
                android:id="@+id/tvStatus"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text=""
                android:textColor="@color/provider_success"
                android:textSize="12sp"
                android:textStyle="bold"
                android:background="#1A22C55E"
                android:paddingHorizontal="12dp"
                android:paddingVertical="4dp"
                android:layout_gravity="center"
                android:layout_marginTop="8dp"/>
                
            <GridLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:columnCount="2"
                android:rowCount="2"
                android:layout_marginTop="24dp">
                
                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginEnd="8dp"
                    android:layout_marginBottom="16dp"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:orientation="vertical"
                    android:gravity="center">
                    <TextView android:text="Employees" android:textColor="@color/provider_text_secondary" android:textSize="12sp"/>
                    <TextView android:id="@+id/tvEmployeeCount" android:text="-" android:textColor="@color/provider_text_primary" android:textSize="20sp" android:textStyle="bold" android:layout_marginTop="4dp"/>
                </LinearLayout>

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_columnWeight="1"
                    android:layout_marginStart="8dp"
                    android:layout_marginBottom="16dp"
                    android:background="@color/provider_surface"
                    android:padding="16dp"
                    android:orientation="vertical"
                    android:gravity="center">
                    <TextView android:text="Sites" android:textColor="@color/provider_text_secondary" android:textSize="12sp"/>
                    <TextView android:id="@+id/tvSiteCount" android:text="-" android:textColor="@color/provider_text_primary" android:textSize="20sp" android:textStyle="bold" android:layout_marginTop="4dp"/>
                </LinearLayout>
            </GridLayout>
            
            <TextView android:text="ORGANIZATION" android:textColor="@color/provider_text_secondary" android:textStyle="bold" android:layout_marginTop="24dp" android:layout_marginBottom="8dp"/>
            <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:background="@color/provider_surface" android:padding="16dp" android:orientation="vertical">
                <TextView android:id="@+id/tvIndustry" android:text="Industry: -" android:textColor="@color/provider_text_primary" android:layout_marginBottom="8dp"/>
                <TextView android:id="@+id/tvEmail" android:text="Email: -" android:textColor="@color/provider_text_primary" android:layout_marginBottom="8dp"/>
                <TextView android:id="@+id/tvPhone" android:text="Phone: -" android:textColor="@color/provider_text_primary"/>
            </LinearLayout>
            
            <TextView android:text="ACTIONS" android:textColor="@color/provider_text_secondary" android:textStyle="bold" android:layout_marginTop="24dp" android:layout_marginBottom="8dp"/>
            <Button
                android:id="@+id/btnDeactivate"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Deactivate CEO"
                android:backgroundTint="#33EF4444"
                android:textColor="@color/provider_error"
                android:layout_marginBottom="8dp"/>
        </LinearLayout>
    </ScrollView>
</LinearLayout>
"""
with open(os.path.join(layout_dir, "activity_provider_ceo_details.xml"), "w") as f: f.write(details_layout)

# Generate Java code for these activities.
# Provide a CeoAdapter.java too.
ceo_adapter = """package com.company.fieldattendance.ui.provider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.CeoResponse;
import java.util.ArrayList;
import java.util.List;

public class CeoAdapter extends RecyclerView.Adapter<CeoAdapter.CeoViewHolder> {
    private List<CeoResponse> ceos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CeoResponse ceo);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCeos(List<CeoResponse> ceos) {
        this.ceos = ceos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CeoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ceo, parent, false);
        return new CeoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CeoViewHolder holder, int position) {
        CeoResponse ceo = ceos.get(position);
        holder.tvName.setText(ceo.name);
        holder.tvCompany.setText(ceo.companyName);
        holder.tvStatus.setText(ceo.status);
        holder.tvEmployees.setText(ceo.employeeCount + " employees");
        holder.tvSites.setText(ceo.siteCount + " sites");
        if ("INACTIVE".equalsIgnoreCase(ceo.status)) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#EF4444"));
            holder.tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#1AEF4444"));
        } else {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#22C55E"));
            holder.tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#1A22C55E"));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(ceo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ceos.size();
    }

    static class CeoViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCompany, tvStatus, tvEmployees, tvSites;
        public CeoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvEmployees = itemView.findViewById(R.id.tvEmployees);
            tvSites = itemView.findViewById(R.id.tvSites);
        }
    }
}
"""
with open(os.path.join(java_dir, "CeoAdapter.java"), "w") as f: f.write(ceo_adapter)

print("UI rewrite python script written.")
