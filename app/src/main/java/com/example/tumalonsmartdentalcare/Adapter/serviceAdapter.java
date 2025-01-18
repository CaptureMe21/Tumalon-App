package com.example.tumalonsmartdentalcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tumalonsmartdentalcare.Model.Service;
import com.example.tumalonsmartdentalcare.R;

import java.util.List;

public class serviceAdapter extends RecyclerView.Adapter<serviceAdapter.ServiceViewHolder> {

    private final List<Service> services;

    public serviceAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);

        // Bind data to the views
        holder.txtServiceName.setText(service.getServiceName());
        holder.txtServicePrice.setText("₱" + service.getServiceFee());

        // Set checkbox state based on the `isSelected` field
        holder.checkbox.setChecked(service.isSelected());

        // Handle checkbox selection changes
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            service.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView txtServiceName, txtServicePrice;
        CheckBox checkbox;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtServiceName = itemView.findViewById(R.id.txt_service_name);
            txtServicePrice = itemView.findViewById(R.id.txt_service_price);
            checkbox = itemView.findViewById(R.id.checkbox_service);
        }
    }
}
