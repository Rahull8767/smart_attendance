package com.company.fieldattendance.ui.provider;
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
