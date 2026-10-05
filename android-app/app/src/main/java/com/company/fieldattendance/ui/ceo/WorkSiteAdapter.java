package com.company.fieldattendance.ui.ceo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.fieldattendance.R;
import com.company.fieldattendance.data.model.WorkSite;

import java.util.ArrayList;
import java.util.List;

public class WorkSiteAdapter extends RecyclerView.Adapter<WorkSiteAdapter.ViewHolder> {

    public interface OnSiteClickListener {
        void onSiteClick(WorkSite site);
    }

    private Context context;
    private List<WorkSite> siteList = new ArrayList<>();
    private OnSiteClickListener listener;

    public WorkSiteAdapter(Context context, OnSiteClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setSites(List<WorkSite> list) {
        this.siteList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_work_site_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkSite site = siteList.get(position);

        holder.tvSiteCardName.setText(site.name != null ? site.name : "Work Site");
        holder.tvSiteCardAddress.setText(site.address != null ? site.address : "Location configured");
        holder.tvSiteCardStatus.setText(site.status != null ? site.status : "ACTIVE");

        double lat = site.latitude != null ? site.latitude : 21.1458;
        double lon = site.longitude != null ? site.longitude : 79.0882;
        double alt = site.altitude != null ? site.altitude : 312.0;
        int rad = site.geofenceRadius != null ? site.geofenceRadius : 150;

        holder.tvSiteCardLat.setText(String.format("%.4f° N", lat));
        holder.tvSiteCardLon.setText(String.format("%.4f° E", lon));
        holder.tvSiteCardAlt.setText(String.format("%.0f m", alt));
        holder.tvSiteCardRadius.setText(rad + " m");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSiteClick(site);
        });
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSiteCardName, tvSiteCardAddress, tvSiteCardStatus;
        TextView tvSiteCardLat, tvSiteCardLon, tvSiteCardAlt, tvSiteCardRadius;

        ViewHolder(View itemView) {
            super(itemView);
            tvSiteCardName = itemView.findViewById(R.id.tvSiteCardName);
            tvSiteCardAddress = itemView.findViewById(R.id.tvSiteCardAddress);
            tvSiteCardStatus = itemView.findViewById(R.id.tvSiteCardStatus);
            tvSiteCardLat = itemView.findViewById(R.id.tvSiteCardLat);
            tvSiteCardLon = itemView.findViewById(R.id.tvSiteCardLon);
            tvSiteCardAlt = itemView.findViewById(R.id.tvSiteCardAlt);
            tvSiteCardRadius = itemView.findViewById(R.id.tvSiteCardRadius);
        }
    }
}
