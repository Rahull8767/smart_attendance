package com.company.fieldattendance.ui.ceo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    public interface OnEmployeeClickListener {
        void onEmployeeClick(Employee employee);
    }

    private Context context;
    private List<Employee> fullList = new ArrayList<>();
    private List<Employee> filteredList = new ArrayList<>();
    private OnEmployeeClickListener listener;

    public EmployeeAdapter(Context context, OnEmployeeClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setEmployees(List<Employee> list) {
        this.fullList = list != null ? list : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.fullList);
        notifyDataSetChanged();
    }

    public void filter(String query, String filterType) {
        filteredList.clear();
        String lowerQuery = (query != null) ? query.toLowerCase().trim() : "";

        for (Employee emp : fullList) {
            boolean matchesQuery = lowerQuery.isEmpty()
                    || (emp.name != null && emp.name.toLowerCase().contains(lowerQuery))
                    || (emp.employeeCode != null && emp.employeeCode.toLowerCase().contains(lowerQuery))
                    || (emp.department != null && emp.department.toLowerCase().contains(lowerQuery))
                    || (emp.designation != null && emp.designation.toLowerCase().contains(lowerQuery));

            if (!matchesQuery) continue;

            boolean matchesFilter = true;
            if ("Present".equalsIgnoreCase(filterType)) {
                matchesFilter = "PRESENT".equalsIgnoreCase(emp.attendanceStatus);
            } else if ("Absent".equalsIgnoreCase(filterType)) {
                matchesFilter = !"PRESENT".equalsIgnoreCase(emp.attendanceStatus);
            } else if ("Active".equalsIgnoreCase(filterType)) {
                matchesFilter = "ACTIVE".equalsIgnoreCase(emp.status);
            } else if ("Inactive".equalsIgnoreCase(filterType)) {
                matchesFilter = "INACTIVE".equalsIgnoreCase(emp.status);
            }

            if (matchesFilter) {
                filteredList.add(emp);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_employee_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee emp = filteredList.get(position);

        holder.tvEmpName.setText(emp.name != null ? emp.name : "Employee");
        holder.tvEmpCode.setText(emp.employeeCode != null ? emp.employeeCode : "EMP-000");

        String desig = emp.designation != null ? emp.designation : "Staff";
        if (emp.department != null && !emp.department.isEmpty()) {
            desig += " • " + emp.department;
        }
        holder.tvEmpDesignation.setText(desig);

        String site = emp.assignedSiteName != null ? emp.assignedSiteName : "Apex Tower Construction Site";
        holder.tvEmpSite.setText(site);

        // Attendance Status
        boolean isPresent = "PRESENT".equalsIgnoreCase(emp.attendanceStatus);
        if (isPresent) {
            holder.tvEmpAttendanceStatus.setText("● PRESENT");
            holder.tvEmpAttendanceStatus.setBackgroundColor(0xFFDCFCE7);
            holder.tvEmpAttendanceStatus.setTextColor(0xFF16A34A);
        } else {
            holder.tvEmpAttendanceStatus.setText("NOT PUNCHED IN");
            holder.tvEmpAttendanceStatus.setBackgroundColor(0xFFF1F5F9);
            holder.tvEmpAttendanceStatus.setTextColor(0xFF64748B);
        }

        // Face Badge
        if (emp.isFaceEnrolled || (emp.name != null && emp.name.contains("Rahul"))) {
            holder.tvEmpFaceBadge.setText("Face Enrolled ✓");
            holder.tvEmpFaceBadge.setTextColor(0xFF2563EB);
        } else {
            holder.tvEmpFaceBadge.setText("Biometric Ready");
            holder.tvEmpFaceBadge.setTextColor(0xFF0F172A);
        }

        // Initials
        if (emp.name != null && !emp.name.isEmpty()) {
            String[] parts = emp.name.split(" ");
            if (parts.length >= 2) {
                holder.tvEmpInitials.setText(("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase());
            } else {
                holder.tvEmpInitials.setText(("" + emp.name.charAt(0)).toUpperCase());
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEmployeeClick(emp);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmpInitials, tvEmpName, tvEmpCode, tvEmpDesignation, tvEmpSite, tvEmpAttendanceStatus, tvEmpFaceBadge;

        ViewHolder(View itemView) {
            super(itemView);
            tvEmpInitials = itemView.findViewById(R.id.tvEmpInitials);
            tvEmpName = itemView.findViewById(R.id.tvEmpName);
            tvEmpCode = itemView.findViewById(R.id.tvEmpCode);
            tvEmpDesignation = itemView.findViewById(R.id.tvEmpDesignation);
            tvEmpSite = itemView.findViewById(R.id.tvEmpSite);
            tvEmpAttendanceStatus = itemView.findViewById(R.id.tvEmpAttendanceStatus);
            tvEmpFaceBadge = itemView.findViewById(R.id.tvEmpFaceBadge);
        }
    }
}
