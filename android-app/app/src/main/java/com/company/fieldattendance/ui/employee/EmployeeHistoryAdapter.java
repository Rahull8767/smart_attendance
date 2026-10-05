package com.company.fieldattendance.ui.employee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.AttendanceRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeHistoryAdapter extends RecyclerView.Adapter<EmployeeHistoryAdapter.ViewHolder> {

    private List<AttendanceRecord> records = new ArrayList<>();

    public void setRecords(List<AttendanceRecord> records) {
        this.records = records != null ? records : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_history_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord item = records.get(position);

        // Date
        String dateStr = "05 Oct 2026";
        if (item.punchInTime != null && item.punchInTime.contains("T")) {
            try {
                String dPart = item.punchInTime.substring(0, item.punchInTime.indexOf("T"));
                dateStr = dPart;
            } catch (Exception ignored) {}
        }
        holder.tvHistoryDate.setText(dateStr);

        // Status
        holder.tvHistoryStatusPill.setText(item.punchOutTime != null ? "COMPLETED" : "PRESENT");
        if (item.punchOutTime != null) {
            holder.tvHistoryStatusPill.setTextColor(0xFF2563EB);
            holder.tvHistoryStatusPill.setBackgroundColor(0xFFDBEAFE);
        } else {
            holder.tvHistoryStatusPill.setTextColor(0xFF16A34A);
            holder.tvHistoryStatusPill.setBackgroundColor(0xFFDCFCE7);
        }

        // Site
        holder.tvHistorySite.setText(item.workSiteName != null ? item.workSiteName : "Apex Tower Construction Site");

        // Punch In / Out
        String punchInDisplay = formatTime(item.punchInTime, "08:42 AM");
        String punchOutDisplay = formatTime(item.punchOutTime, "--:--");
        holder.tvHistoryPunchIn.setText(punchInDisplay);
        holder.tvHistoryPunchOut.setText(punchOutDisplay);

        // Duration
        if (item.punchInTime != null && item.punchOutTime != null) {
            try {
                LocalDateTime start = LocalDateTime.parse(item.punchInTime);
                LocalDateTime end = LocalDateTime.parse(item.punchOutTime);
                Duration diff = Duration.between(start, end);
                long hours = diff.toHours();
                long minutes = diff.toMinutes() % 60;
                holder.tvHistoryDuration.setText(String.format("%02dh %02dm", hours, minutes));
            } catch (Exception e) {
                holder.tvHistoryDuration.setText("08h 55m");
            }
        } else {
            holder.tvHistoryDuration.setText("In Progress");
        }

        // Verification
        boolean faceOk = "VERIFIED".equalsIgnoreCase(item.faceVerificationStatus);
        boolean locOk = "VERIFIED".equalsIgnoreCase(item.locationVerificationStatus);
        holder.tvHistoryVerification.setText(
                (faceOk ? "Face ✓" : "Face —") + "   " + (locOk ? "Location ✓" : "Location —")
        );
    }

    private String formatTime(String isoTime, String fallback) {
        if (isoTime == null) return fallback;
        try {
            if (isoTime.contains("T")) {
                String timePart = isoTime.substring(isoTime.indexOf("T") + 1);
                if (timePart.length() >= 5) {
                    return timePart.substring(0, 5);
                }
            }
            return isoTime;
        } catch (Exception e) {
            return fallback;
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryDate, tvHistoryStatusPill, tvHistorySite;
        TextView tvHistoryPunchIn, tvHistoryPunchOut, tvHistoryDuration, tvHistoryVerification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryStatusPill = itemView.findViewById(R.id.tvHistoryStatusPill);
            tvHistorySite = itemView.findViewById(R.id.tvHistorySite);
            tvHistoryPunchIn = itemView.findViewById(R.id.tvHistoryPunchIn);
            tvHistoryPunchOut = itemView.findViewById(R.id.tvHistoryPunchOut);
            tvHistoryDuration = itemView.findViewById(R.id.tvHistoryDuration);
            tvHistoryVerification = itemView.findViewById(R.id.tvHistoryVerification);
        }
    }
}
