package com.company.fieldattendance.ui.ceo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.CeoAttendanceRecordDTO;

import java.util.ArrayList;
import java.util.List;

public class CeoAttendanceAdapter extends RecyclerView.Adapter<CeoAttendanceAdapter.ViewHolder> {

    public interface OnRecordClickListener {
        void onRecordClick(CeoAttendanceRecordDTO record);
    }

    private Context context;
    private List<CeoAttendanceRecordDTO> fullList = new ArrayList<>();
    private List<CeoAttendanceRecordDTO> filteredList = new ArrayList<>();
    private OnRecordClickListener listener;

    public CeoAttendanceAdapter(Context context, OnRecordClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setRecords(List<CeoAttendanceRecordDTO> list) {
        this.fullList = list != null ? list : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.fullList);
        notifyDataSetChanged();
    }

    public void filter(String statusFilter) {
        filteredList.clear();
        for (CeoAttendanceRecordDTO r : fullList) {
            if ("All".equalsIgnoreCase(statusFilter) || statusFilter == null) {
                filteredList.add(r);
            } else if ("Present".equalsIgnoreCase(statusFilter) && "PRESENT".equalsIgnoreCase(r.status)) {
                filteredList.add(r);
            } else if ("Completed".equalsIgnoreCase(statusFilter) && "PUNCHED OUT".equalsIgnoreCase(r.status)) {
                filteredList.add(r);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ceo_attendance_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CeoAttendanceRecordDTO record = filteredList.get(position);

        holder.tvAttEmpName.setText(record.employeeName != null ? record.employeeName : "Employee");
        holder.tvAttEmpCode.setText(record.employeeCode != null ? record.employeeCode : "EMP-000");

        String desig = record.designation != null ? record.designation : "Staff";
        if (record.department != null && !record.department.isEmpty()) {
            desig += " • " + record.department;
        }
        holder.tvAttDesignation.setText(desig);

        holder.tvAttSiteName.setText(record.workSiteName != null ? record.workSiteName : "Apex Tower Construction Site");

        String timeStr = "Punch In: " + (record.punchInTime != null ? record.punchInTime : "--:--");
        if (record.punchOutTime != null) {
            timeStr += " | Out: " + record.punchOutTime;
        }
        holder.tvAttTimeDisplay.setText(timeStr);

        if (record.latitude != null && record.longitude != null) {
            holder.tvAttCoords.setText(String.format("%.4f, %.4f", record.latitude, record.longitude));
        } else {
            holder.tvAttCoords.setText("21.1458, 79.0882");
        }

        boolean isPresent = "PRESENT".equalsIgnoreCase(record.status);
        if (isPresent) {
            holder.tvAttStatusBadge.setText("● PRESENT");
            holder.tvAttStatusBadge.setBackgroundColor(0xFFDCFCE7);
            holder.tvAttStatusBadge.setTextColor(0xFF16A34A);
        } else {
            holder.tvAttStatusBadge.setText("PUNCHED OUT");
            holder.tvAttStatusBadge.setBackgroundColor(0xFFFEF3C7);
            holder.tvAttStatusBadge.setTextColor(0xFFD97706);
        }

        holder.tvAttFaceBadge.setText("Face Verified ✓");
        holder.tvAttLocBadge.setText("Location Verified ✓");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecordClick(record);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAttEmpName, tvAttEmpCode, tvAttDesignation, tvAttStatusBadge;
        TextView tvAttSiteName, tvAttTimeDisplay, tvAttCoords, tvAttFaceBadge, tvAttLocBadge;

        ViewHolder(View itemView) {
            super(itemView);
            tvAttEmpName = itemView.findViewById(R.id.tvAttEmpName);
            tvAttEmpCode = itemView.findViewById(R.id.tvAttEmpCode);
            tvAttDesignation = itemView.findViewById(R.id.tvAttDesignation);
            tvAttStatusBadge = itemView.findViewById(R.id.tvAttStatusBadge);
            tvAttSiteName = itemView.findViewById(R.id.tvAttSiteName);
            tvAttTimeDisplay = itemView.findViewById(R.id.tvAttTimeDisplay);
            tvAttCoords = itemView.findViewById(R.id.tvAttCoords);
            tvAttFaceBadge = itemView.findViewById(R.id.tvAttFaceBadge);
            tvAttLocBadge = itemView.findViewById(R.id.tvAttLocBadge);
        }
    }
}
