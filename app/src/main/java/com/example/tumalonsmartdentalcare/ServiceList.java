package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tumalonsmartdentalcare.Adapter.serviceAdapter;
import com.example.tumalonsmartdentalcare.Fragment.ScheduleFragment;
import com.example.tumalonsmartdentalcare.Model.Service;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceList extends AppCompatActivity {

    private RecyclerView recyclerServiceList;
    private MaterialButton btnBookAppointment;
    private EditText searchEditText;
    private ImageView backArrow, searchIcon;
    private LinearLayout searchBarLayout;

    private List<Service> services;
    private serviceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_service_list);

        // Initialize views
        recyclerServiceList = findViewById(R.id.recycler_service_list);
        backArrow = findViewById(R.id.back_arrow);
        searchIcon = findViewById(R.id.search_icon);
        searchEditText = findViewById(R.id.search_edit_text);
        searchBarLayout = findViewById(R.id.search_bar_layout);
        btnBookAppointment = findViewById(R.id.btn_book_appointment);

        // Back arrow click listener to navigate to MainActivity
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ServiceList.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize RecyclerView
        recyclerServiceList.setLayoutManager(new LinearLayoutManager(this));
        services = new ArrayList<>();
        serviceAdapter = new serviceAdapter(services);
        recyclerServiceList.setAdapter(serviceAdapter);

        fetchServicesFromDatabase();

        btnBookAppointment.setOnClickListener(view -> {
            // Check if any service is selected
            boolean hasSelectedService = false;
            for (Service service : services) {
                if (service.isSelected()) {
                    hasSelectedService = true;
                    break;
                }
            }

            if (hasSelectedService) {
                openScheduleFragment();
            } else {
                Toast.makeText(ServiceList.this, "No services selected. Please select at least one service.", Toast.LENGTH_SHORT).show();
            }
        });}

    private void openScheduleFragment() {

        ScheduleFragment scheduleFragment = new ScheduleFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, scheduleFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public List<Service> getSelectedServices() {
        List<Service> selectedServices = new ArrayList<>();
        for (Service service : services) {
            if (service.isSelected()) {
                selectedServices.add(service);
            }
        }
        return selectedServices;
    }

    private void fetchServicesFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("services");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                services.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {

                    String serviceId = serviceSnapshot.getKey();
                    String serviceName = serviceSnapshot.child("serviceName").getValue(String.class);
                    Long serviceFee = serviceSnapshot.child("serviceFee").getValue(Long.class); // Ensure correct type

                    if (serviceId != null && serviceName != null && serviceFee != null) {
                        services.add(new Service(serviceId, serviceName, serviceFee.toString()));
                    }
                }
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServiceList.this, "Failed to load services: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
