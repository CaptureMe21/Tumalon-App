package com.example.tumalonsmartdentalcare.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tumalonsmartdentalcare.MainActivity;
import com.example.tumalonsmartdentalcare.Model.Appointment;
import com.example.tumalonsmartdentalcare.Model.Service;
import com.example.tumalonsmartdentalcare.R;
import com.example.tumalonsmartdentalcare.ServiceList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String selectedDateStr, selectedTime, userId;
    private CalendarView calendarView;
    private TextView txtMorning, txtAfternoon;
    private ImageView backArrow;
    private Calendar calendar;

    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        txtMorning = view.findViewById(R.id.txt_morning);
        txtAfternoon = view.findViewById(R.id.txt_afternoon);
        calendarView = view.findViewById(R.id.calendar_view);
        backArrow = view.findViewById(R.id.back_arrow);
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        selectedDateStr = dateFormat.format(calendar.getTime());
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDateStr = dateFormat.format(calendar.getTime());
        });

        // Retrieve the userId from the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");
        }

        Button btnNextDialog = view.findViewById(R.id.btn_next_dialog);

        // Set up click listeners for the TextViews
        txtMorning.setOnClickListener(v -> {
            // Set selected state for Morning
            txtMorning.setBackgroundResource(R.drawable.rounded_lblue);
            txtMorning.setTextColor(getResources().getColor(R.color.rblue, null));

            // Set unselected state for Afternoon
            txtAfternoon.setBackgroundResource(R.drawable.rounded_lgrey);
            txtAfternoon.setTextColor(getResources().getColor(R.color.black, null));

            selectedTime = "Morning"; // Update selected time
        });

        txtAfternoon.setOnClickListener(v -> {
            // Set selected state for Afternoon
            txtAfternoon.setBackgroundResource(R.drawable.rounded_lblue);
            txtAfternoon.setTextColor(getResources().getColor(R.color.rblue, null));

            // Set unselected state for Morning
            txtMorning.setBackgroundResource(R.drawable.rounded_lgrey);
            txtMorning.setTextColor(getResources().getColor(R.color.black, null));

            selectedTime = "Afternoon"; // Update selected time
        });

        // Set a click listener on the back arrow to go back to the previous page
        backArrow.setOnClickListener(v -> {
            // Navigate back to the previous fragment or activity
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        // Set click listener for Next Dialog button
        btnNextDialog.setOnClickListener(v -> {
            // Check if a time is selected
            if (selectedTime == null || selectedTime.isEmpty()) {
                Toast.makeText(getContext(), "Please select a time to proceed.", Toast.LENGTH_SHORT).show();
                return; // Do not proceed if no time is selected
            }

            // If a time is selected, show the dialog
            showNextDialogToast();
        });


        return view;
    }

    private void showNextDialogToast() {
        Dialog confirmationDialog = new Dialog(getContext());
        confirmationDialog.setContentView(R.layout.confirmation_dialog);
        confirmationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView serviceNameText = confirmationDialog.findViewById(R.id.service_name);
        TextView serviceFeeText = confirmationDialog.findViewById(R.id.service_fee);
        TextView totalFeeText = confirmationDialog.findViewById(R.id.total_fee);
        TextView scheduleDateText = confirmationDialog.findViewById(R.id.schedule_date);
        TextView scheduleTimeText = confirmationDialog.findViewById(R.id.schedule_time);
        Button confirmButton = confirmationDialog.findViewById(R.id.confirm_button);

        ServiceList serviceListActivity = (ServiceList) getActivity();
        if (serviceListActivity == null) {
            Toast.makeText(getContext(), "Service list activity not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Service> selectedServices = serviceListActivity.getSelectedServices();
        if (selectedServices == null || selectedServices.isEmpty()) {
            Toast.makeText(getContext(), "No services selected. Please select at least one service.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder serviceNames = new StringBuilder();
        StringBuilder serviceFees = new StringBuilder();
        double totalFee = 0;

        for (Service service : selectedServices) {
            if (serviceNames.length() > 0) {
                serviceNames.append("\n");
                serviceFees.append("\n");
            }
            serviceNames.append(service.getServiceName());
            double fee;
            try {
                fee = Double.parseDouble(service.getServiceFee());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid service fee format.", Toast.LENGTH_SHORT).show();
                return;
            }
            serviceFees.append("₱").append(String.format("%.2f", fee));
            totalFee += fee;
        }

        serviceNameText.setText(serviceNames.toString());
        serviceFeeText.setText(serviceFees.toString());
        totalFeeText.setText("₱" + String.format("%.2f", totalFee));

        if (selectedDateStr == null || selectedTime == null) {
            Toast.makeText(getContext(), "Schedule details are missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        scheduleDateText.setText(selectedDateStr);
        scheduleTimeText.setText(selectedTime);

        // Retrieve userId
        final String userId;
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");
        } else {
            Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) {
            Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        confirmButton.setOnClickListener(v -> {
            try {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
                String appointmentId = databaseReference.push().getKey();

                if (appointmentId == null) {
                    Toast.makeText(getContext(), "Failed to create appointment.", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> appointmentData = new HashMap<>();
                appointmentData.put("userId", userId);
                appointmentData.put("scheduleDate", selectedDateStr);
                appointmentData.put("scheduleTime", selectedTime);
                appointmentData.put("createdAt", System.currentTimeMillis());
                appointmentData.put("appointmentStatus", false);

                HashMap<String, Boolean> servicesData = new HashMap<>();
                for (Service service : selectedServices) {
                    String serviceId = service.getServiceId();
                    if (serviceId != null) {
                        servicesData.put(serviceId, true);
                    }
                }
                appointmentData.put("services", servicesData);

                databaseReference.child(appointmentId).setValue(appointmentData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Appointment saved successfully.", Toast.LENGTH_SHORT).show();
                            try {
                                // Create an Intent to navigate to MainActivity
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                // Pass the userId to MainActivity
                                FirebaseAuth.getInstance().getCurrentUser().getUid();  // Retrieve the current user ID
                                intent.putExtra("currentUser", userId);  // Pass userId to MainActivity

                                getContext().startActivity(intent);
                            } catch (Exception e) {
                                Log.e("AppointmentSave", "Error starting MainActivity: " + e.getMessage());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to save appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                confirmationDialog.dismiss();
            } catch (Exception e) {
                Log.e("FirebaseSave", "Error saving appointment: " + e.getMessage());
                Toast.makeText(getContext(), "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
            }
        });

        confirmationDialog.show();
    }
}